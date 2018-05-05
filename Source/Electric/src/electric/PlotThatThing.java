package electric;

import java.io.FileNotFoundException;
import java.io.InputStream;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.tuples.TupleFeeder;
import ca.uqac.lif.cep.io.ReadLines;

public class PlotThatThing
{

	public static void main(String[] args) throws FileNotFoundException
	{
		String filename = "data/testB.csv";
		
		
		InputStream is = Utilities.getFileInputStream(filename);
		ReadLines reader = new ReadLines(is);
		// Connect a tuple feeder to the reader
		TupleFeeder feeder = new TupleFeeder();
		Connector.connect(reader, feeder);
		Plotter plot = new Plotter("TIME", "graph.pdf", "", "", "");
		Connector.connect(feeder,  plot);
		plot.plot(3);

	}

}
