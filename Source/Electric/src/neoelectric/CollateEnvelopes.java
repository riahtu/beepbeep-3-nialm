package neoelectric;

import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.util.Sets;

public class CollateEnvelopes extends GroupProcessor
{
  public CollateEnvelopes(String ... names)
  {
    super(names.length, names.length);
    for (int i = 0; i < names.length; i++)
    {
      Sets.PutInto spi = new Sets.PutInto();
      associateInput(i, spi, 0);
      associateOutput(i, spi, 0);
      addProcessor(spi);
    }
  }
}
