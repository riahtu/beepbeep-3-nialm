package neoelectric;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.SynchronousProcessor;
import java.util.Queue;

public class ProgrammedStutter extends SynchronousProcessor
{

  public ProgrammedStutter()
  {
    super(1, 1);
  }
  
  @Override
  protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    Object[] v = (Object[]) inputs[0];
    float x = ((Number) v[0]).floatValue();
    int n = ((Number) v[1]).intValue();
    for (int i = 0; i < n; i++)
    {
      outputs.add(new Object[] {x});
    }
    return false;
  }

  @Override
  public Processor duplicate(boolean with_state)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
