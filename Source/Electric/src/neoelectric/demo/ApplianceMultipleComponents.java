package neoelectric.demo;

import ca.uqac.lif.cep.Pullable;
import neoelectric.SimulateAppliance;

public class ApplianceMultipleComponents
{

  public static void main(String[] args)
  {
    SimulateAppliance appl = new SimulateAppliance(ApplianceMultipleComponents.class, "data/A1", "W1", "W2");
    Pullable p = appl.getPullableOutput(0);
    while (p.hasNext())
    {
      System.out.println(p.pull());
    }
  }
}
