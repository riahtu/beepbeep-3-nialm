package electric;

import java.util.Queue;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Pushable;

/**
 * <b>BEWARE:</b> when the processor returns the last received event, it
 * returns <em>the exact same object</em>. If you need to modify it, you should
 * create a copy.
 * @author sylvain
 *
 */
public class PersistentProcessor extends Processor
{
	/**
	 * The last set of events completely received from the input
	 */
	private Object[] m_last;

	public PersistentProcessor(int arity)
	{
		super(arity, arity);
		m_last = new Object[arity];
	}
	
	public PersistentProcessor setInitialEvent(Object[] o)
	{
		m_last = o;
		return this;
	}

	@Override
	public Pushable getPushableInput(int index)
	{
		return new PersistentPushable(index);
	}

	@Override
	public Pullable getPullableOutput(int index)
	{
		return new PersistentPullable(index);
	}
	
	protected class PersistentPushable implements Pushable
	{
		/**
		 * The index of the processor's input this pushable refers to
		 */
		private final int m_index;
		
		/**
		 * The number of events pushed so far
		 */
		private int m_pushCount;
		
		/**
		 * Creates a pushable associated to some of a processor's input
		 * traces. 
		 * @param index The index of the trace. Should be between 0 and
		 *   the processor's input arity - 1. This is not checked by the
		 *   constructor, so beware.
		 */
		PersistentPushable(int index)
		{
			super();
			m_index = index;
			m_pushCount = 0;
		}

		@Override
		public Pushable push(Object o)
		{
			m_pushCount++;
			if (m_index < m_inputQueues.length)
			{
				Queue<Object> q = m_inputQueues[m_index];
				q.add(o);
			}
			// Check if each input queue has an event ready
			for (int i = 0; i < m_inputArity; i++)
			{
				Queue<Object> queue = m_inputQueues[i];
				if (queue.isEmpty())
				{
					// One of them doesn't: we can't produce an output yet
					return this;
				}
			}
			// Pick an event from each input queue and push it to the output queue
			for (int i = 0; i < m_inputArity; i++)
			{
				Queue<Object> queue = m_inputQueues[i];
				Object ob = queue.remove();
				Pushable p = m_outputPushables[i];
				p.push(ob);
			}
			return this;
		}

		@Override
		public int getPushCount()
		{
			return m_pushCount;
		}
	}
	
	protected class PersistentPullable implements Pullable
	{
		/**
		 * The index of the processor's output this pullable refers to
		 */
		private final int m_index;
		
		/**
		 * The number of events pulled so far
		 */
		private int m_pullCount;

		/**
		 * Creates a pullable associated to some of a processor's output
		 * traces. 
		 * @param index The index of the trace. Should be between 0 and
		 *   the processor's output arity - 1. This is not checked by the
		 *   constructor, so beware.
		 */
		public PersistentPullable(int index)
		{
			super();
			m_index = index;
			m_pullCount = 0;
		}

		@Override
		public int getPullCount()
		{
			return m_pullCount;
		}

		@Override
		public Object pull()
		{
			if (hasNext() != NextStatus.YES)
			{
				return null;
			}
			Queue<Object> out_queue = m_outputQueues[m_index];
			// If an event is already waiting in the output queue,
			// return it and don't pull anything from the input
			if (!out_queue.isEmpty())
			{
				Object o = out_queue.remove();
				m_pullCount++;
				return o;
			}
			return null;
		}

		@Override
		public Object pullHard()
		{
			if (hasNextHard() != NextStatus.YES)
			{
				return null;
			}				
			Queue<Object> out_queue = m_outputQueues[m_index];
			// If an event is already waiting in the output queue,
			// return it and don't pull anything from the input
			if (!out_queue.isEmpty())
			{
				Object o = out_queue.remove();
				m_pullCount++;
				return o;
			}
			return null;
		}

		@Override
		public NextStatus hasNext()
		{
			Queue<Object> out_queue = m_outputQueues[m_index];
			// If an event is already waiting in the output queue,
			// return it and don't pull anything from the input
			if (!out_queue.isEmpty())
			{
				return NextStatus.YES;
			}
			// Check if each pullable has an event ready
			for (int i = 0; i < m_inputArity; i++)
			{
				Pullable p = m_inputPullables[i];
				assert p != null;
				NextStatus status = p.hasNext();
				if (status != NextStatus.YES)
				{
					// One of the input pullables has no event ready;
					// put in the output queue the last set of events
					if (m_last[0] == null)
					{
						// NOTE: we use m_last[0] == null as a surrogate to
						// check whether some event was received
						return NextStatus.MAYBE;
					}
					for (int j = 0; j < m_last.length; j++)
					{
						m_outputQueues[j].add(m_last[j]);
					}
					return NextStatus.YES;
				}
			}
			// We are here only if every input pullable has answered YES
			// Pull an event from each
			for (int i = 0; i < m_inputArity; i++)
			{
				Pullable p = m_inputPullables[i];
				Object o = p.pull();
				m_outputQueues[i].add(o);
				m_last[i] = o;
			}
			return NextStatus.YES;
		}

		@Override
		public NextStatus hasNextHard()
		{
			Queue<Object> out_queue = m_outputQueues[m_index];
			// If an event is already waiting in the output queue,
			// return it and don't pull anything from the input
			if (!out_queue.isEmpty())
			{
				return NextStatus.YES;
			}
			// Check if each pullable has an event ready
			for (int i = 0; i < m_inputArity; i++)
			{
				Pullable p = m_inputPullables[i];
				assert p != null;
				NextStatus status = p.hasNextHard();
				if (status == NextStatus.NO)
				{
					if (m_last[0] == null)
					{
						// NOTE: we use m_last[0] == null as a surrogate to
						// check whether some event was received
						return NextStatus.NO;
					}
					// One of the input pullables has no event ready;
					// put in the output queue the last set of events
					for (int j = 0; j < m_last.length; j++)
					{
						m_outputQueues[j].add(m_last[j]);
					}
					return NextStatus.YES;
				}
			}
			// We are here only if every input pullable has answered YES
			// Pull an event from each
			for (int i = 0; i < m_inputArity; i++)
			{
				Pullable p = m_inputPullables[i];
				Object o = p.pullHard();
				m_outputQueues[i].add(o);
				m_last[i] = o;
			}
			return NextStatus.YES;
		}
		
	}
	
}
