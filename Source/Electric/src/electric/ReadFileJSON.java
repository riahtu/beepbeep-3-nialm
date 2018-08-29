package electric;

import java.io.FileInputStream;
import java.io.FileNotFoundException;



import java.io.IOException;
import java.io.InputStream;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.io.CommandRunner;
import ca.uqac.lif.cep.io.ReadLines;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tuples.ScalarIntoTuple;
import ca.uqac.lif.cep.tuples.Tuple;
import ca.uqac.lif.cep.tuples.TupleFeeder;

public class ReadFileJSON 
{


	public static void main(String[] args) throws IOException {
		ConvertJsonToCSV csv = new ConvertJsonToCSV("EnergeticWSOutput.json");
		csv.compute(null, null);

		
		detectPlateauOnAppliance("energeticWSOutput", "WL1", 2, true);
		 

	}
	
	
	static void detectPlateauOnAppliance(String appli, String component, int num_test, boolean to_plot) throws FileNotFoundException
	{
		//String filename = "../outputConvertion.csv";
		// Get the reader from the filename
		InputStream is = new FileInputStream("outputConvertion.csv");//ElectricMooreMachine.class.getResourceAsStream(filename);

		ReadLines reader = new ReadLines(is);

		// Connect a tuple feeder to the reader
		TupleFeeder feeder = new TupleFeeder();
		Connector.connect(reader, feeder);
		
		// Fork the input
		Fork fork = new Fork(2);
		Connector.connect(feeder, fork);
		
		
		// On first branch...
		Project select_1;
		{
			// Filter a few columns from the tuples
			select_1 = new Project(new String[] {""}, new String[] {"TIME", "WL1", "WL2", "WL3", "VARL1", "VARL2", "VARL3"});
			Connector.connect(fork, 0, select_1, 0);
		}

		
		// On second branch...
		PeakProcessor finder = new PeakProcessor(component, 100);
		Connector.connect(fork, 1, finder, 0);
		ApplyFunction sit = new ApplyFunction(new ScalarIntoTuple("x"));
		Connector.connect(finder, sit);
		// Join the two outputs

		Project select = new Project(new String[] {"S", "T"}, new String[] {"S.TIME", "S.WL1", "S.WL2", "S.WL3", "S.VARL1", "S.VARL2", "S.VARL3", "T.x"});
		Connector.connect(select_1, 0, select, 0);
		Connector.connect(sit, 0, select, 1);
		
		// Plug into a plotter
		Plotter plotter = new Plotter("TIME", appli + num_test + "-plateau.pdf", "Raw signal", "Time(s)", "Power (W)");
		Connector.connect(select, plotter);
		
		plotter.plot();
		plotter.close();
	}
	
}




