package neoelectric;

import java.util.Map;

import ca.uqac.lif.cep.functions.BinaryFunction;
import ca.uqac.lif.cep.tuples.Tuple;

public class TupleDistance extends BinaryFunction<Tuple,Tuple,Float>
{
  public static final transient TupleDistance instance = new TupleDistance();

  protected TupleDistance()
  {
    super(Tuple.class, Tuple.class, Float.class);
  }

  @Override
  public Float getValue(Tuple x, Tuple y)
  {
    float d = 0;
    for (Map.Entry<String,Object> e : x.entrySet())
    {
      float v_x = ((Number) e.getValue()).floatValue();
      float v_y = ((Number) y.get(e.getKey())).floatValue();
      d += Math.abs(v_x - v_y);
    }
    return d;
  }
}
