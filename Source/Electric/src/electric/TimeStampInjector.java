/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2015 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package electric;

import java.util.Queue;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.SingleProcessor;
import ca.uqac.lif.cep.tuples.TupleMap;

public class TimeStampInjector extends SingleProcessor
{
	public TimeStampInjector()
	{
		super(2, 1);
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		TupleMap[] out = new TupleMap[1];
		TupleMap ntm1 = new TupleMap((TupleMap) inputs[0]);
		TupleMap ntm2 = new TupleMap((TupleMap) inputs[1]);
		float w_computed = 0;
		for (String key : ntm1.keySet())
		{
			w_computed += (Float) ntm1.get(key);
		}
		float w = (Float) ntm2.get("WL1");
		ntm1.put("Noise", Math.abs(w - w_computed));
		ntm1.put("TIME", ntm2.get("TIME"));
		//ntm.put("TIME", m_timestamp++);
		out[0] = ntm1;
		outputs.add(out);
		return true;
	}

  @Override
  public Processor duplicate(boolean with_state)
  {
    return new TimeStampInjector();
  }
}
