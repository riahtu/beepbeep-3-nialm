package neoelectric;

import ca.uqac.lif.cep.tuples.Tuple;
import java.util.HashSet;
import java.util.Set;

public class ApplianceSignature
{
  /**
   * A tuple describing the peaks/plateaus to be seen for the
   * appliance to be considered ON
   */
  protected Tuple m_onEnvelope;
  
  /**
   * A tuple describing the peaks/plateaus to be seen for the
   * appliance to be considered OFF
   */
  protected Tuple m_offEnvelope;
  
  public ApplianceSignature(Tuple on, Tuple off)
  {
    super();
    m_onEnvelope = on;
    m_offEnvelope = off;
  }
  
  /**
   * Gets the names of all the signal components mentioned in this signature.
   * A signal component is, for example, "W1" or "VARL2". 
   * @return The set of signal components
   */
  public Set<String> getComponentNames()
  {
    Set<String> comps = new HashSet<String>();
    for (String s : m_onEnvelope.keySet())
    {
      comps.add(s.replaceAll("-.", ""));
    }
    return comps;
  }
  
  @Override
  public String toString()
  {
    return "ON: " + m_onEnvelope + ", OFF: " + m_offEnvelope;
  }
  
  public Tuple getOffEnvelope()
  {
    return m_offEnvelope;
  }
  
  public Tuple getOnEnvelope()
  {
    return m_onEnvelope;
  }
}
