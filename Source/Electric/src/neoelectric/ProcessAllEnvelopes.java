package neoelectric;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tuples.MergeScalars;

public class ProcessAllEnvelopes extends GroupProcessor
{
  public ProcessAllEnvelopes(String ... names)
  {
    super(names.length, 1);
    // Create as many envelope processor as there are signals
    ProcessEnvelope[] pe = new ProcessEnvelope[6];
    for (int i = 0; i < names.length; i++)
    {
      pe[i] = new ProcessEnvelope();
      associateInput(i, pe[i], 0);
      addProcessors(pe[i]);
    }
    String[] comp_names = new String[names.length * 2];
    for (int i = 0; i < names.length; i++)
    {
      comp_names[2 * i] = names[i] + "-K";
      comp_names[2 * i + 1] = names[i] + "-T";
    }
    ApplyFunction tf = new ApplyFunction(new MergeScalars(comp_names));
    for (int i = 0; i < comp_names.length; i++)
    {
      Connector.connect(pe[i/2], i % 2, tf, i);
    }
    addProcessors(tf);
    associateOutput(0, tf, 0);
  } 
}
