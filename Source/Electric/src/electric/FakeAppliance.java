package electric;

import java.util.Queue;
import java.util.Random;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.tmf.Source;


public class FakeAppliance extends Source
{
	protected final String m_name;
	
	protected final float m_peak;
	
	protected final float m_plateau;
	
	protected final float m_onTime;
	
	protected final float m_offTime;
	
	protected final float m_increment;
	
	protected float m_currentTimePoint;
	
	protected static float s_noiseInterval = 0f; 
	
	protected static final Random s_random = new Random();

	public FakeAppliance(String name, float peak, float plateau, float on_time, float off_time, float time_increment)
	{
		super(1);
		m_name = name;
		m_peak = peak;
		m_plateau = plateau;
		m_onTime = on_time;
		m_offTime = off_time;
		m_increment = time_increment;
		m_currentTimePoint = 0;
	}
	
	public void setNoiseRatio(float ratio)
	{
		s_noiseInterval = ratio;
	}
	
	public String getName()
	{
		return m_name;
	}
	
	public float getPlateau()
	{
		return m_plateau;
	}

	@Override
	protected boolean compute(Object[] arg0, Queue<Object[]> outputs)
	{
		Float[] out = new Float[1];
		out[0] = 0f;
		if (Math.abs(m_currentTimePoint - m_onTime) < m_increment / 3)
		{
			out[0] = getValueWithNoise(m_peak);
		}
		else if (m_currentTimePoint > m_onTime && m_currentTimePoint < m_offTime)
		{
			out[0] = getValueWithNoise(m_plateau);
		}
		m_currentTimePoint += m_increment;
		outputs.add(out);
		return true;
	}
	
	protected static float getValueWithNoise(float x)
	{
		return x + (s_random.nextFloat() * s_noiseInterval);
	}

	@Override
	public Processor duplicate(boolean with_state)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
