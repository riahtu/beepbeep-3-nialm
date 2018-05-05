/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2015 Sylvain Hall�

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
import ca.uqac.lif.cep.tmf.Multiplex;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tuples.MergeScalars;
import ca.uqac.lif.cep.tuples.TupleFeeder;
import ca.uqac.lif.cep.io.ReadLines;
import electric.ElectricMooreMachine.ApplianceEvent;

public class Recognition
{
	public static void main(String[] args) throws FileNotFoundException
	{
		String[] components = {"WL1", "WL2", "WL3", "VARL1", "VARL2", "VARL3"};
		//String[] components = {"WL1"};
		//String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O"};
		int num_test = 1;
		String filename = "data/test" + num_test + ".csv";
		// Get the reader from the filename
		InputStream is = Utilities.getFileInputStream(filename);
		ReadLines reader = new ReadLines(is);
		// Connect a tuple feeder to the reader
		TupleFeeder feeder = new TupleFeeder();
		Connector.connect(reader, feeder);
		// Fork the input
		Fork fork1 = new Fork(2 * components.length + 1);
		Connector.connect(feeder, fork1);
		// Send each fork to a different signal processor
		Processor[] signal = new Processor[2 * components.length];
		for (int i = 0; i < components.length; i++)
		{
			signal[i] = new PeakProcessor(null, components[i], 90); 
			Connector.connect(fork1, i, signal[i], 0);
		}
		for (int i = 0; i < components.length; i++)
		{
			// Delay the plateau detectors by one event; this makes sure that
			// peaks and plateaus occur in two distinct events
			PlateauProcessor pp = new PlateauProcessor(null, components[i], 150);
			Connector.connect(fork1, components.length + i, pp, 0);
			Object[] zero_pad = {0};
			Insert d = new Insert(zero_pad, 10);
			Connector.connect(pp, d);
			signal[components.length + i] = d;
			//signal[components.length + i] = pp;
		}
		// Merge all the outputs in a single event
		//int symb_count = 0;
		
		ApplyFunction select = new ApplyFunction(new MergeScalars("PK-WL1", "PK-WL2", "PK-WL3", 
		    "PK-VARL1", "PK-VARL2", "PK-VARL3",
		    "PT-WL1", "PT-WL2", "PT-WL3",
		    "PT-VARL1", "PT-VARL2", "PT-VARL3", "TIME"));
		for (int i = 0; i < 2 * components.length; i++)
		{
			Connector.connect(signal[i], 0, select, i);
		}
		Connector.connect(fork1, 2 * components.length, select, 2 * components.length);
		// Plug that into a plotter
		
		Plotter plotter = new Plotter("TIME", "data/test" + num_test + ".pdf", 
				"Output of peak detector on all signal components", "Time (s)", "Power (W)");
		Connector.connect(select, plotter);
		plotter.plot(9);
		System.exit(0);
		// Fork the output again for as many appliances we have
		Processor[] machines = new Processor[5];
		Fork fork2 = new Fork(machines.length);
		Connector.connect(select, fork2);
		{
			// Coffee
			machines[0] = new ElectricMooreMachine("Coffee", "WL1", 939, 939, 939, 150);
			Connector.connect(fork2, 0, machines[0], 0);
		}
		{
			// Blender
			machines[1] = new ElectricMooreMachine("Blender", "WL1", 700, 300, 300, 150);
			Connector.connect(fork2, 1, machines[1], 0);
		}
		{
			// Toaster
			machines[2] = new ElectricMooreMachine("Toaster", "WL2", 900, 900, 400, 150);
			Connector.connect(fork2, 2, machines[2], 0);
		}
		{
			// Small burner
			machines[3] = new ElectricMooreMachine2Comp("Small burner", "WL2", "VARL1", 500, 500, 400, 300);
			Connector.connect(fork2, 3, machines[3], 0);
		}
		{
			// Fan
			machines[4] = new ElectricMooreMachine2Comp("Fan", "WL1", "VARL1", 100, 100, 400, 100);
			Connector.connect(fork2, 4, machines[4], 0);
		}

		// And merge again to get a single trace
		Multiplex mux = new Multiplex(machines.length);
		for (int i = 0; i < machines.length; i++)
		{
			Connector.connect(machines[i], 0, mux, i);
		}
		Pullable p = mux.getPullableOutput(0);
		long start = System.currentTimeMillis();
		ApplianceEvent ae = null;
		for (int i = 0; i < 40000; i++)
		{
			ae = (ApplianceEvent) p.pull();
			if (ae != null)
			{
				System.out.println(ae);
			}
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

}
