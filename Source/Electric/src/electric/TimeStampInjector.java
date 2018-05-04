package electric;

import java.util.Queue;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.SingleProcessor;
import ca.uqac.lif.cep.tuples.TupleMap;

public class TimeStampInjector extends SingleProcessor
{
	public TimeStampInjector()
	{
		super(2, 1);
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		TupleMap[] out = new TupleMap[1];
		TupleMap ntm1 = new TupleMap((TupleMap) inputs[0]);
		TupleMap ntm2 = new TupleMap((TupleMap) inputs[1]);
		float w_computed = 0;
		for (String key : ntm1.keySet())
		{
			w_computed += (Float) ntm1.get(key);
		}
		float w = (Float) ntm2.get("WL1");
		ntm1.put("Noise", Math.abs(w - w_computed));
		ntm1.put("TIME", ntm2.get("TIME"));
		//ntm.put("TIME", m_timestamp++);
		out[0] = ntm1;
		outputs.add(out);
		return true;
	}

  @Override
  public Processor duplicate(boolean with_state)
  {
    return new TimeStampInjector();
  }
}
