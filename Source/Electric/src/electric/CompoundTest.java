package electric;

import java.util.HashSet;
import java.util.Set;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.eml.tuples.AttributeDefinitionAs;
import ca.uqac.lif.cep.eml.tuples.AttributeNameQualified;
import ca.uqac.lif.cep.eml.tuples.NamedTupleMap;
import ca.uqac.lif.cep.eml.tuples.Select;
import ca.uqac.lif.cep.tmf.CountDecimate;
import ca.uqac.lif.cep.epl.Delay;
import ca.uqac.lif.cep.epl.Insert;
import ca.uqac.lif.cep.gnuplot.GnuplotScatterplot;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Multiplex;
import ca.uqac.lif.cep.tuples.TupleMap;

public class CompoundTest
{

	public static void main(String[] args)
	{
		// Create set of fake appliances
		String[] components = {"WL1"};
		String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O"};
		float time_step = 1/60f;
		Set<FakeAppliance> appliances = new HashSet<FakeAppliance>();
		appliances.add(new FakeAppliance("Kettle", 500, 500, 2, 4, time_step));
		appliances.add(new FakeAppliance("Coffee pot", 1000, 1000, 1, 3, time_step));
		appliances.add(new FakeAppliance("Toaster", 3000, 3000, 1.5f, 5f, time_step));
		FakeAppliance.s_noiseInterval = 2f;
		FakeApplianceSet fas = new FakeApplianceSet(appliances, time_step);
		
		// Fork the input
		Fork fork1 = new Fork(2 * components.length + 2);
		Connector.connect(fas, fork1);
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
		int symb_count = 0;
		Select select = new Select(2 * components.length + 1, 
				new AttributeDefinitionAs(new AttributeNameQualified(letters[symb_count++], "*"), "PK-WL1"),
				/*new AttributeDefinitionAs(new AttributeNameQualified(letters[symb_count++], "*"), "PK-WL2"),
				new AttributeDefinitionAs(new AttributeNameQualified(letters[symb_count++], "*"), "PK-WL3"),
				new AttributeDefinitionAs(new AttributeNameQualified(letters[symb_count++], "*"), "PK-VARL1"),
				new AttributeDefinitionAs(new AttributeNameQualified(letters[symb_count++], "*"), "PK-VARL2"),
				new AttributeDefinitionAs(new AttributeNameQualified(letters[symb_count++], "*"), "PK-VARL3"),*/
				new AttributeDefinitionAs(new AttributeNameQualified(letters[symb_count++], "*"), "PT-WL1"),
				/*new AttributeDefinitionAs(new AttributeNameQualified(letters[symb_count++], "*"), "PT-WL2"),
				new AttributeDefinitionAs(new AttributeNameQualified(letters[symb_count++], "*"), "PT-WL3"),
				new AttributeDefinitionAs(new AttributeNameQualified(letters[symb_count++], "*"), "PT-VARL1"),
				new AttributeDefinitionAs(new AttributeNameQualified(letters[symb_count++], "*"), "PT-VARL2"),
				new AttributeDefinitionAs(new AttributeNameQualified(letters[symb_count++], "*"), "PT-VARL3"),*/
				new AttributeDefinitionAs(new AttributeNameQualified(letters[symb_count++], "TIME"), "TIME"));
		symb_count = 0;
		for (int i = 0; i < 2 * components.length; i++)
		{
			select.setProcessor(letters[symb_count++], null);
		}
		select.setProcessor(letters[symb_count++], null);
		for (int i = 0; i < 2 * components.length; i++)
		{
			Connector.connect(signal[i], select, 0, i);
		}
		Connector.connect(fork1, select, 2 * components.length, 2 * components.length);
		// Fork the output again for as many appliances we have
		Processor[] machines = new Processor[3];
		Fork fork2 = new Fork(machines.length);
		Connector.connect(select, fork2);
		{
			// Coffee
			machines[0] = new ElectricMooreMachine("Kettle", "WL1", 500, 500, 500, 120);
			Connector.connect(fork2, 0, machines[0], 0);
		}
		{
			// Blender
			machines[1] = new ElectricMooreMachine("Coffee pot", "WL1", 1000, 1000, 1000, 120);
			Connector.connect(fork2, 1, machines[1], 0);
		}
		{
			// Toaster
			machines[2] = new ElectricMooreMachine("Toaster", "WL1", 3000, 3000, 3000, 120);
			Connector.connect(fork2, 2, machines[2], 0);
		}
		// And merge again to get a single trace
		Multiplex mux = new Multiplex(machines.length);
		for (int i = 0; i < machines.length; i++)
		{
			Connector.connect(machines[i], 0, mux, i);
		}
		// Create load reconstructor
		SignalReconstructor sr = new SignalReconstructor(appliances);
		Connector.connect(mux, sr);
		PersistentProcessor pp = new PersistentProcessor(1);
		TupleMap[] init = new TupleMap[1];
		init[0] = new TupleMap();
		init[0].put("Kettle", 0f);
		init[0].put("Coffee pot", 0f);
		init[0].put("Toaster", 0f);
		pp.setInitialEvent(init);
		Connector.connect(sr, pp);
		// Reinject timestamp into event
		// Normally, we should get the timestamp from the fork
		Delay decim = new Delay(15);
		Connector.connect(pp, decim);
		TimeStampInjector tsi = new TimeStampInjector();
		Connector.connect(decim, tsi, 0, 0);
		Connector.connect(fork1, 2 * components.length + 1, tsi, 1);
		// Graph that
		//Plotter plotter = new Plotter("TIME", "mystack.pdf", "My stacked plot", "Time", "Power").setPullHard(false);
		Plotter plotter = new Plotter(new GnuplotStackedPlot(), "TIME", "mystack.pdf", "My stacked plot", "Time", "Power").setPullHard(false);
		Connector.connect(tsi, plotter);
		plotter.plot(1000);
		System.exit(0);
		Pullable p = tsi.getPullableOutput(0);
		for (int i = 0; i < 600; i++)
		{
			Object o = p.pull();
			if (o != null)
			{
				System.out.println(o);
			}
		}
	}

}