package neoelectric;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.SynchronousProcessor;
import java.util.Queue;

public class ProgrammedStutter extends SynchronousProcessor
{

  public ProgrammedStutter()
  {
    super(2, 1);
  }
  
  @Override
  protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    float x = ((Number) inputs[0]).floatValue();
    int n = ((Number) inputs[1]).intValue();
    for (int i = 0; i < n; i++)
    {
      outputs.add(new Object[] {x});
    }
    return true;
  }

  @Override
  public Processor duplicate(boolean with_state)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
