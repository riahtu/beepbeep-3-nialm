package electric;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.tuples.Tuple;
import ca.uqac.lif.cep.tuples.TupleMap;
import ca.uqac.lif.cep.tmf.Source;


public class FakeApplianceSet extends Source
{
	protected List<Pullable> m_appliances;
	
	protected final float m_increment;
	
	protected float m_currentTimePoint;

	public FakeApplianceSet(float increment)
	{
		this(null, increment);
	}
	
	public FakeApplianceSet(Collection<FakeAppliance> col, float increment)
	{
		super(1);
		m_appliances = new LinkedList<Pullable>();
		if (col != null)
		{
			for (FakeAppliance a : col)
			{
				addAppliance(a);				
			}
		}
		m_increment = increment;
		m_currentTimePoint = 0;
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		Tuple[] out = new Tuple[1];
		TupleMap ntm = new TupleMap();
		float total = 0;
		for (Pullable p : m_appliances)
		{
			float f = (Float) p.pull();
			total += f;
		}
		// At the moment, we put everything on a single phase
		ntm.put("WL1", total);
		ntm.put("TIME", m_currentTimePoint);
		out[0] = ntm;
		m_currentTimePoint += m_increment;
		outputs.add(out);
		return true;
	}
	
	public FakeApplianceSet addAppliance(FakeAppliance a)
	{
		m_appliances.add(a.getPullableOutput(0));
		return this;
	}

	@Override
	public Processor duplicate(boolean with_state)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
