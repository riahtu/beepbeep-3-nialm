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

import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.tuples.FixedTupleBuilder;
import ca.uqac.lif.cep.tuples.Tuple;

public class Project extends UniformProcessor
{
  protected String[] m_attributes;
  
  protected int[] m_indices;
  
  protected FixedTupleBuilder m_builder;
  
  public Project(String[] stream_names, String[] attributes)
  {
    super(stream_names.length, 1);
    m_indices = new int[attributes.length];
    m_attributes = new String[attributes.length];
    for (int i = 0; i < attributes.length; i++)
    {
      String[] parts = attributes[i].split("\\.");
      if (parts.length == 1)
      {
        m_attributes[i] = parts[0];
        m_indices[i] = 0;
      }
      else
      {
        m_attributes[i] = parts[1];
        m_indices[i] = indexOf(parts[0], stream_names);
      }
    }
    m_builder = new FixedTupleBuilder(m_attributes);
  }
  
  private Project(int in_arity)
  {
    super(in_arity, 1);
  }
  
  protected static int indexOf(String name, String[] stream_names)
  {
    int pos = -1;
    for (int i = 0; i < stream_names.length; i++)
    {
      if (name.compareTo(stream_names[i]) == 0)
      {
        pos = i;
        break;
      }
    }
    return pos;
  }

  @Override
  protected boolean compute(Object[] inputs, Object[] outputs)
  {
    Object[] values = new Object[m_attributes.length];
    for (int i = 0; i < m_attributes.length; i++)
    {
      Tuple t = (Tuple) inputs[m_indices[i]];
      values[i] = t.get(m_attributes[i]);
    }
    outputs[0] = m_builder.createTuple(values);
    return true;
  }
  
  @Override
  public Project duplicate(boolean with_state)
  {
    Project p = new Project(getInputArity());
    p.m_attributes = new String[m_attributes.length];
    for (int i = 0; i < m_attributes.length; i++)
    {
      p.m_attributes[i] = m_attributes[i];
    }
    p.m_indices = new int[m_indices.length];
    for (int i = 0; i < m_indices.length; i++)
    {
      p.m_indices[i] = m_indices[i];
    }
    p.m_builder = new FixedTupleBuilder(m_attributes);
    return p;
  }
}
