package electric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.SingleProcessor;
import ca.uqac.lif.cep.eml.tuples.NamedTuple;
import ca.uqac.lif.cep.eml.tuples.NamedTupleMap;
import electric.ElectricMooreMachine.ApplianceEvent;

public class SignalReconstructor extends SingleProcessor
{
	protected List<String> m_loadNames;
	
	protected List<Float> m_loadValues;
	
	protected List<Boolean> m_whatsOn;
	
	public SignalReconstructor()
	{
		this(null);
	}
	
	public SignalReconstructor(Collection<FakeAppliance> col)
	{
		super(1, 1);
		m_loadNames = new ArrayList<String>();
		m_loadValues = new ArrayList<Float>();
		m_whatsOn = new ArrayList<Boolean>();
		if (col != null)
		{
			for (FakeAppliance fa : col)
			{
				addLoad(fa.m_name, fa.m_plateau);
			}
		}
	}
	
	public SignalReconstructor addLoad(String name, float value)
	{
		m_loadNames.add(name);
		m_loadValues.add(value);
		m_whatsOn.add(false);
		return this;
	}

	@Override
	protected Queue<Object[]> compute(Object[] inputs)
	{
		NamedTuple[] out = new NamedTuple[1];
		NamedTupleMap nt = new NamedTupleMap();
		ApplianceEvent event = (ApplianceEvent) inputs[0];
		String app_name = event.getName();
		for (int i = 0; i < m_loadNames.size(); i++)
		{
			if (m_loadNames.get(i).compareTo(app_name) == 0)
			{
				if ("ON".compareTo(event.getMessage()) == 0)
				{
					// Turn that device on
					m_whatsOn.set(i, true);
				}
				else
				{
					// Turn that device off
					m_whatsOn.set(i, false);
				}
				break;
			}
		}
		// Fill named tuple with loads
		for (int i = 0; i < m_loadNames.size(); i++)
		{
			String a_name = m_loadNames.get(i);
			if (m_whatsOn.get(i))
			{
				nt.put(a_name, m_loadValues.get(i));
			}
			else
			{
				nt.put(a_name, 0f);
			}
		}
		out[0] = nt;
		return wrapVector(out);
	}
}
