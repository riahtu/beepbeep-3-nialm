package neoelectric;

import java.util.Random;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.UniformProcessor;

public class AddNoise extends UniformProcessor
{
  protected float m_range;
  
  protected Random m_random;
  
  public AddNoise(float range)
  {
    super(1, 1);
    m_range = range;
    m_random = new Random();
  }

  @Override
  protected boolean compute(Object[] inputs, Object[] outputs)
  {
    float f = ((Number) inputs[0]).floatValue();
    outputs[0] = f + m_random.nextFloat() * (m_range / 2f);
    return true;
  }

  @Override
  public Processor duplicate(boolean with_state)
  {
    return new AddNoise(m_range);
  }
}
