package electric;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.SingleProcessor;
import java.util.Queue;

public class Insert extends SingleProcessor
{
  protected Object m_object;
  
  protected int m_times;
  
  protected boolean m_inserted = false;
  
  public Insert(Object o, int times)
  {
    super(1, 1);
    m_object = o;
    m_times = times;
  }

  @Override
  protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    if (!m_inserted)
    {
      for (int i = m_times; i < m_times; i++)
      {
        outputs.add(new Object[]{m_object});
      }
      m_inserted = false;
    }
    outputs.add(new Object[]{inputs[0]});
    return true;
  }

  @Override
  public Processor duplicate(boolean with_state)
  {
    Insert in = new Insert(m_object, m_times);
    if (with_state)
    {
      in.m_inserted = m_inserted;
    }
    return in;
  }
  
  
}
