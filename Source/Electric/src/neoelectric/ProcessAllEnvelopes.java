package neoelectric;

import ca.uqac.lif.cep.GroupProcessor;

public class ProcessAllEnvelopes extends GroupProcessor
{
  public ProcessAllEnvelopes(String ... names)
  {
    super(names.length, names.length * 2);
    // Create as many envelope processor as there are signals
    ProcessEnvelope[] pe = new ProcessEnvelope[6];
    for (int i = 0; i < names.length; i++)
    {
      pe[i] = new ProcessEnvelope();
      associateInput(i, pe[i], 0);
      associateOutput(2 * i, pe[i], 0);
      associateOutput(2 * i + 1, pe[i], 1);
      addProcessors(pe[i]);
    }
  } 
}
