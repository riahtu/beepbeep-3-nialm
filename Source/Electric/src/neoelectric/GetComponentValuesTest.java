package neoelectric;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.io.ReadLines;
import ca.uqac.lif.cep.tmf.Pump;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.junit.Test;

public class GetComponentValuesTest
{
  @Test
  public void testPull() throws FileNotFoundException
  {
    FileInputStream is = new FileInputStream(new File("/tmp/test.json"));
    ReadLines lines = new ReadLines(is);
    GetComponentValues gcv = new GetComponentValues();
    Connector.connect(lines, gcv);
    Pullable p = gcv.getPullableOutput();
    while (p.hasNext())
    {
      System.out.println(p.pull());
    }
  }
  
  @Test
  public void testPump() throws FileNotFoundException
  {
    FileInputStream is = new FileInputStream(new File("/tmp/test.json"));
    ReadLines lines = new ReadLines(is);
    Pump pump = new Pump();
    Connector.connect(lines, pump);
    GetComponentValues gcv = new GetComponentValues();
    Connector.connect(pump, gcv);
    Print print = new Print();
    Connector.connect(gcv, print);
    pump.turn(6);
  }
  
  protected String printArray(Object[] array)
  {
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < array.length; i++)
    {
      if (i > 0)
      {
        out.append(",");
      }
      out.append(array[i]);
    }
    return out.toString();
  }
}
