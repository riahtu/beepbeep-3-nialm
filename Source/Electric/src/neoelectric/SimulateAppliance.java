package neoelectric;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.util.Numbers;

public class SimulateAppliance extends GroupProcessor
{
  protected float m_noise = 60f;
  
  public SimulateAppliance(Class<?> ref_class, String folder, String ... comp_names)
  {
    super(0, comp_names.length + 1);
    SimulateComponent[] comp = new SimulateComponent[comp_names.length];
    for (int i = 0; i < comp.length; i++)
    {
      String filename = folder + "/" + comp_names[i] + ".csv";
      comp[i] = new SimulateComponent(ref_class.getResourceAsStream(filename), m_noise);
      addProcessors(comp[i]);
    }
    Fork fork = new Fork(2);
    Connector.connect(comp[0], fork);
    TurnInto one = new TurnInto(1);
    Connector.connect(fork, 0, one, 0);
    Cumulate sum_one = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
    Connector.connect(one, sum_one);
    addProcessors(fork, one, sum_one);
    associateOutput(0, sum_one, 0);
    associateOutput(1, fork, 1);
    for (int i = 2; i < comp_names.length + 1; i++)
    {
      associateOutput(i, comp[i-1], 0);
    }
  }
}
