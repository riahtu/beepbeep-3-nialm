package electric;

import java.io.InputStream;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.eml.tuples.TupleFeeder;
import ca.uqac.lif.cep.io.StreamReader;

public class PlotThatThing
{

	public static void main(String[] args)
	{
		String filename = "data/testB.csv";
		
		
		InputStream is = Utilities.getFileInputStream(filename);
		StreamReader reader = new StreamReader(is);
		// Connect a tuple feeder to the reader
		TupleFeeder feeder = new TupleFeeder();
		Connector.connect(reader, feeder);
		Plotter plot = new Plotter("TIME", "graph.pdf", "", "", "");
		Connector.connect(feeder,  plot);
		plot.plot(3);

	}

}
