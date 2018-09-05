package neoelectric;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.io.ReadLines;
import ca.uqac.lif.cep.signal.Noise;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.VariableStutter;
import ca.uqac.lif.cep.util.Bags;
import ca.uqac.lif.cep.util.NthElement;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.util.Strings;
import java.io.InputStream;

public class SimulateComponent extends GroupProcessor
{
  public SimulateComponent(InputStream is, float noise_range)
  {
    super(0, 1);
    ReadLines rl = new ReadLines(is);
    ApplyFunction split = new ApplyFunction(
        new FunctionTree(new Bags.ApplyToAll(Numbers.numberCast),
        new Strings.SplitString(",")));
    Connector.connect(rl, split);
    Fork fork1 = new Fork(2);
    Connector.connect(split, fork1);
    ApplyFunction v1 = new ApplyFunction(new NthElement(0));
    Connector.connect(fork1, 0, v1, 0);
    ApplyFunction v2 = new ApplyFunction(new NthElement(1));
    Connector.connect(fork1, 1, v2, 0);
    VariableStutter stutter = new VariableStutter();
    Connector.connect(v1, 0, stutter, 0);
    Connector.connect(v2, 0, stutter, 1);
    Cumulate sum_values = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
    Connector.connect(stutter, sum_values);
    Noise noise = new Noise(noise_range);
    Connector.connect(sum_values, noise);
    addProcessors(rl, split, fork1, v1, v2, stutter, sum_values, noise);
    associateOutput(0, noise, 0);
  }
}
