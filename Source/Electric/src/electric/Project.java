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
