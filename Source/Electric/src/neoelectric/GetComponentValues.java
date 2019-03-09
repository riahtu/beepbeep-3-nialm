package neoelectric;

import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.json.JPathFunction;
import ca.uqac.lif.cep.json.NumberValue;
import ca.uqac.lif.cep.json.ParseJson;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tuples.MergeScalars;

import static ca.uqac.lif.cep.Connector.connect;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;

public class GetComponentValues extends GroupProcessor
{
  /**
   * Original component names in the JSON
   */
  protected static final String[] s_componentNames = new String[] {"W",
      "W L1", "W L2", "W L3", "VAR", "VAR L1", "VAR L2", "VAR L3"};
  
  /**
   * Target component names in the output tuples
   */
  protected static final String[] s_renamedComponentNames = new String[] {"W",
      "WL1", "WL2", "WL3", "VAR", "VAR1", "VAR2", "VAR3"};
  
  public GetComponentValues()
  {
    super(1, 1);
    ApplyFunction parse = new ApplyFunction(ParseJson.instance);
    Fork f = new Fork(s_componentNames.length);
    connect(parse, f);
    ApplyFunction ta = new ApplyFunction(new MergeScalars(s_renamedComponentNames));
    for (int i = 0; i < s_componentNames.length; i++)
    {
      ApplyFunction get = new ApplyFunction(new FunctionTree(
          NumberValue.instance, new JPathFunction(s_componentNames[i])));
      connect(f, i, get, INPUT);
      connect(get, OUTPUT, ta, i);
    }
    associateInput(INPUT, parse, INPUT);
    associateOutput(OUTPUT, ta, OUTPUT);
  }
  
  @Override
  public GetComponentValues duplicate(boolean with_state)
  {
    if (with_state)
    {
      // Pas important pour nous
      throw new UnsupportedOperationException("Stateful duplication of this processor is not implemented for the moment");
    }
    return new GetComponentValues();
  }
}
