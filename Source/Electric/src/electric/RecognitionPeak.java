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

import java.io.FileNotFoundException;
import java.io.InputStream;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Passthrough;
import ca.uqac.lif.cep.tuples.FetchAttribute;
import ca.uqac.lif.cep.tuples.MergeScalars;
import ca.uqac.lif.cep.tuples.TupleFeeder;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.io.ReadLines;
import electric.ElectricMooreMachine.ApplianceEvent;

public class RecognitionPeak
{
  protected static boolean PLOT = false;

  public static void main(String[] args) throws FileNotFoundException
  {
    //String[] components = {"WL1", "WL2", "WL3", "VARL1", "VARL2", "VARL3"};
    int num_test = 1;
    String filename = "data/test" + num_test + ".csv";
    System.out.println(filename);
    // Get the reader from the filename
    InputStream is = ElectricMooreMachine.class.getResourceAsStream(filename);
    ReadLines reader = new ReadLines(is);
    // Connect a tuple feeder to the reader
    TupleFeeder feeder = new TupleFeeder();
    Connector.connect(reader, feeder);
    // Fork the input
    Fork fork1 = new Fork(3);
    Connector.connect(feeder, fork1);
    // Send each fork to a different signal processor
    Processor[] signal = new Processor[2];
    signal[0] = new PeakProcessor("WL1", 150); 
    Connector.connect(fork1, 0, signal[0], 0);
    PlateauProcessor pp = new PlateauProcessor(null, "WL1", 150);
    Connector.connect(fork1, 1, pp, 0);
    Object[] dummy_pad = {0};
    Insert in = new Insert(dummy_pad, 4);
    Connector.connect(pp, in);
    signal[1] = in;
    // Merge all the outputs in a single event
    //ApplyFunction select = new ApplyFunction(new MergeTuples("PK-WL1", "PT-WL1", "TIME"));
    ApplyFunction select = new ApplyFunction(new MergeScalars("PK-WL1", "PT-WL1", "TIME"));
    Connector.connect(signal[0], 0, select, 0);
    Connector.connect(signal[1], 0, select, 1);
    ApplyFunction get_time = new ApplyFunction(new FunctionTree(Numbers.numberCast, new FunctionTree(new FetchAttribute("TIME"), StreamVariable.X)));
    Connector.connect(fork1, 2, get_time, 0);
    Connector.connect(get_time, 0, select, 2);
    // What do we do with the output?
    if (PLOT)
    {
      // Plug that into a plotter
      Plotter plotter = new Plotter("TIME", "test_plot" + num_test + ".pdf", 
          "Output of peak detector on all signal components", "Time (s)", "Power (W)");
      Connector.connect(select, plotter);
      plotter.plot(4);
    }
    else
    {
      // Fork the output again for as many appliances we have
      //Fork fork2 = new Fork(1);
      Passthrough fork2 = new Passthrough(1);
      Connector.connect(select, fork2);
      ElectricMooreMachine[] machines = new ElectricMooreMachine[1];
      {
        // Kettle
        machines[0] = new ElectricMooreMachine("Coffee", "WL1", 939, 939, 939, 150);
        Connector.connect(fork2, 0, machines[0], 0);
      }
      Pullable p = machines[0].getPullableOutput(0);
      while (p.hasNext())
      {
        ApplianceEvent ae = (ApplianceEvent) p.pull();
        System.out.println(ae);
      }
    }
  }
}
