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

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.SynchronousProcessor;
import java.util.Queue;

public class Insert extends SynchronousProcessor
{
  protected Object m_object;
  
  protected int m_times;
  
  protected boolean m_inserted = false;
  
  public Insert(Object o, int times)
  {
    super(1, 1);
    m_object = o;
    m_times = times;
  }

  @Override
  protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    if (!m_inserted)
    {
      for (int i = m_times; i < m_times; i++)
      {
        outputs.add(new Object[]{m_object});
      }
      m_inserted = false;
    }
    outputs.add(new Object[]{inputs[0]});
    return true;
  }

  @Override
  public Processor duplicate(boolean with_state)
  {
    Insert in = new Insert(m_object, m_times);
    if (with_state)
    {
      in.m_inserted = m_inserted;
    }
    return in;
  }
  
  
}
