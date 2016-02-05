package electric;

import java.util.Queue;

import ca.uqac.lif.cep.SingleProcessor;
import ca.uqac.lif.cep.eml.tuples.NamedTupleMap;

public class TimeStampInjector extends SingleProcessor
{
	protected int m_timestamp = 0;
	
	public TimeStampInjector()
	{
		super(2, 1);
	}

	@Override
	protected Queue<Object[]> compute(Object[] inputs)
	{
		NamedTupleMap[] out = new NamedTupleMap[1];
		NamedTupleMap ntm1 = new NamedTupleMap((NamedTupleMap) inputs[0]);
		NamedTupleMap ntm2 = new NamedTupleMap((NamedTupleMap) inputs[1]);
		float w_computed = 0;
		for (String key : ntm1.keySet())
		{
			w_computed += (float) ntm1.get(key);
		}
		float w = (float) ntm2.get("WL1");
		ntm1.put("Noise", Math.abs(w - w_computed));
		ntm1.put("TIME", ntm2.get("TIME"));
		//ntm.put("TIME", m_timestamp++);
		out[0] = ntm1;
		return wrapVector(out);
	}
}
