package neoelectric;

import ca.uqac.lif.cep.Pullable;
import java.io.InputStream;
import org.junit.Test;

public class SimulateMachineTest
{
  @Test
  public void test1()
  {
    InputStream is = SimulateMachineTest.class.getResourceAsStream("data/test-stutter.csv");
    SimulateMachine sm = new SimulateMachine(is);
    Pullable p = sm.getPullableOutput();
    for (int i = 0; i < 10; i++)
    {
      System.out.println(p.pull());
    }
  }
}
