package neoelectric;

import ca.uqac.lif.cep.tuples.Tuple;

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
