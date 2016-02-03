package electric;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import ca.uqac.lif.cep.Combiner;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Fork;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.eml.tuples.Select;
import ca.uqac.lif.cep.eml.tuples.TupleFeeder;
import ca.uqac.lif.cep.eml.tuples.TupleGrammar;
import ca.uqac.lif.cep.epl.CountDecimate;
import ca.uqac.lif.cep.gnuplot.GnuplotScatterplot;
import ca.uqac.lif.cep.interpreter.Interpreter;
import ca.uqac.lif.cep.io.Caller;
import ca.uqac.lif.cep.io.StreamReader;
import ca.uqac.lif.cep.sets.BagUnion;
import ca.uqac.lif.cep.sets.SetGrammar;
import ca.uqac.lif.cep.signal.PeakFinderLocalMaximum;
import ca.uqac.lif.cep.signal.SignalGrammar;

@SuppressWarnings("unused")
public class PeakTest
{
	public static void main(String[] args)
	{
		Interpreter my_int = getInterpreter();
		String filename = "data/Blender1.csv";
		// Get the reader from the filename
		InputStream is = getFileInputStream(filename);
		StreamReader reader = new StreamReader(is);
		// Connect a tuple feeder to the reader
		TupleFeeder feeder = new TupleFeeder();
		Connector.connect(reader, feeder);
		PeakFinderLocalMaximum finder;
		{
			// Keep a single attribute
			String[] attributes = {"S.WL1"};
			Select select = new Select(1, attributes);
			select.setProcessor("S", feeder);
			Connector.connect(feeder, select);
			// Pass through peak detector
			finder = new PeakFinderLocalMaximum(15);
			Connector.connect(select, finder);
		}
		// Start pulling data from the pipe
		Pullable p = finder.getPullableOutput(0);
		Object o = null;
		int evt_cnt = 0;
		while (p.hasNextHard() == Pullable.NextStatus.YES)
		{
			o = p.pullHard();
			System.out.println(evt_cnt + "," + o);
			evt_cnt++;
		}
	}
	
	/**
	 * Setup an ESQL interpreter
	 * @return The interpreter
	 */
	protected static Interpreter getInterpreter()
	{
		Interpreter my_int = new Interpreter();
		my_int.extendGrammar(TupleGrammar.class);
		my_int.extendGrammar(SetGrammar.class);
		my_int.extendGrammar(SignalGrammar.class);
		return my_int;
	}
	
	/**
	 * Get an InputStream from a filename
	 * @param filename The filename
	 * @return The input stream
	 */
	protected static InputStream getFileInputStream(String filename)
	{
		InputStream is = null;
		try
		{
			is = new FileInputStream(new File(filename));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return is;
	}
}
