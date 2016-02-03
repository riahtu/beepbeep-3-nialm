package electric;

import java.util.HashSet;
import java.util.Set;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Multiplexer;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.SmartFork;
import ca.uqac.lif.cep.eml.tuples.AttributeDefinitionAs;
import ca.uqac.lif.cep.eml.tuples.AttributeNameQualified;
import ca.uqac.lif.cep.eml.tuples.Select;
import ca.uqac.lif.cep.epl.Insert;

public class CompoundTest
{

	public static void main(String[] args)
	{
		// Create set of fake appliances
		String[] components = {"WL1"};
		String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O"};
		float time_step = 1/60f;
		Set<FakeAppliance> appliances = new HashSet<FakeAppliance>();
		appliances.add(new FakeAppliance("Kettle", 500, 500, 2, 3, time_step));
		appliances.add(new FakeAppliance("Coffee pot", 1000, 1000, 1, 4, time_step));
		FakeApplianceSet fas = new FakeApplianceSet(appliances, time_step);
		
		// Fork the input
		SmartFork fork1 = new SmartFork(2 * components.length + 1);
		Connector.connect(fas, fork1);
		// Send each fork to a different signal processor
		Processor[] signal = new Processor[2 * components.length];
		for (int i = 0; i < components.length; i++)
		{
			signal[i] = new PeakProcessor(null, components[i], 90); 
			Connector.connect(fork1, signal[i], i, 0);
		}
		for (int i = 0; i < components.length; i++)
		{
			// Delay the plateau detectors by one event; this makes sure that
			// peaks and plateaus occur in two distinct events
			PlateauProcessor pp = new PlateauProcessor(null, components[i], 150);
			Connector.connect(fork1, pp, components.length + i, 0);
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
		Processor[] machines = new Processor[2];
		SmartFork fork2 = new SmartFork(machines.length);
		Connector.connect(select, fork2);
		{
			// Coffee
			machines[0] = new ElectricMooreMachine("Kettle", "WL1", 500, 500, 500, 20);
			Connector.connect(fork2, machines[0], 0, 0);
		}
		{
			// Blender
			machines[1] = new ElectricMooreMachine("Coffee pot", "WL1", 1000, 1000, 1000, 20);
			Connector.connect(fork2, machines[1], 1, 0);
		}

		// And merge again to get a single trace
		Multiplexer mux = new Multiplexer(machines.length);
		for (int i = 0; i < machines.length; i++)
		{
			Connector.connect(machines[i], mux, 0, i);
		}
		// Create load reconstructor
		SignalReconstructor sr = new SignalReconstructor(appliances);
		Connector.connect(mux, sr);
		Pullable p = sr.getPullableOutput(0);
		for (int i = 0; i < 6000; i++)
		{
			Object o = p.pull();
			if (o != null)
			{
				System.out.println(o);
			}
		}
	}

}
