package neoelectric;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.io.ReadLines;
import ca.uqac.lif.cep.util.Bags;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.util.Strings;
import java.io.InputStream;

public class SimulateMachine extends GroupProcessor
{
  public SimulateMachine(InputStream is)
  {
    super(0, 1);
    ReadLines rl = new ReadLines(is);
    ApplyFunction split = new ApplyFunction(
        new FunctionTree(new Bags.ApplyToAll(Numbers.numberCast),
        new Strings.SplitString(",")));
    Connector.connect(rl, split);
    addProcessors(rl, split);
    associateOutput(0, split, 0);
  }
}
