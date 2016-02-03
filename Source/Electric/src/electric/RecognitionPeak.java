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

import java.io.InputStream;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.SmartFork;
import ca.uqac.lif.cep.eml.tuples.AttributeDefinitionAs;
import ca.uqac.lif.cep.eml.tuples.AttributeNameQualified;
import ca.uqac.lif.cep.eml.tuples.Select;
import ca.uqac.lif.cep.eml.tuples.TupleFeeder;
import ca.uqac.lif.cep.epl.Delay;
import ca.uqac.lif.cep.epl.Insert;
import ca.uqac.lif.cep.io.StreamReader;
import electric.ElectricMooreMachine.ApplianceEvent;

public class RecognitionPeak
{

	public static void main(String[] args)
	{
		//String[] components = {"WL1", "WL2", "WL3", "VARL1", "VARL2", "VARL3"};
		int num_test = 1;
		String filename = "data/test" + num_test + ".csv";
		// Get the reader from the filename
		InputStream is = Utilities.getFileInputStream(filename);
		StreamReader reader = new StreamReader(is);
		// Connect a tuple feeder to the reader
		TupleFeeder feeder = new TupleFeeder();
		Connector.connect(reader, feeder);
		// Fork the input
		SmartFork fork1 = new SmartFork(3);
		Connector.connect(feeder, fork1);
		// Send each fork to a different signal processor
		Processor[] signal = new Processor[2];
		signal[0] = new PeakProcessor(null, "WL1", 150); 
		Connector.connect(fork1, signal[0], 0, 0);
		PlateauProcessor pp = new PlateauProcessor(null, "WL1", 150);
		Connector.connect(fork1, pp, 1, 0);
		Object[] dummy_pad = {0};
		Insert in = new Insert(dummy_pad, 4);
		Connector.connect(pp, in);
		signal[1] = in;
		// Merge all the outputs in a single event
		Select select = new Select(3, 
				new AttributeDefinitionAs(new AttributeNameQualified("A", "*"), "PK-WL1"),
				new AttributeDefinitionAs(new AttributeNameQualified("B", "*"), "PT-WL1"),
				new AttributeDefinitionAs(new AttributeNameQualified("C", "TIME"), "TIME"));
		select.setProcessor("A", signal[0]);
		select.setProcessor("B", signal[1]);
		select.setProcessor("C", feeder);
		Connector.connect(signal[0], select, 0, 0);
		Connector.connect(signal[1], select, 0, 1);
		Connector.connect(fork1, select, 2, 2);

		// Plug that into a plotter
		/*
		Plotter plotter = new Plotter("TIME", "data/test" + num_test + ".pdf", 
				"Output of peak detector on all signal components", "Time (s)", "Power (W)");
		Connector.connect(select, plotter);
		plotter.plot(4);
		*/
		// Fork the output again for as many appliances we have
		SmartFork fork2 = new SmartFork(2);
		Connector.connect(select, fork2);
		ElectricMooreMachine[] machines = new ElectricMooreMachine[2];
		{
			// Kettle
			machines[0] = new ElectricMooreMachine("Coffee", "WL1", 939, 939, 939, 150);
			Connector.connect(fork2, machines[0], 0, 0);
		}
		Pullable p = machines[0].getPullableOutput(0);
		ApplianceEvent ae = (ApplianceEvent) p.pullHard();
		System.out.println(ae);
		ae = (ApplianceEvent) p.pullHard();
		System.out.println(ae);
	}

}
