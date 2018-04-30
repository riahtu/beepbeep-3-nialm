package electric;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.tuples.TupleFeeder;
import ca.uqac.lif.cep.io.ReadLines;
import ca.uqac.lif.cep.signal.PeakFinderLocalMaximum;

@SuppressWarnings("unused")
public class PeakTest
{
	public static void main(String[] args)
	{
		String filename = "data/Blender1.csv";
		// Get the reader from the filename
		InputStream is = getFileInputStream(filename);
		ReadLines reader = new ReadLines(is);
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
		while (p.hasNext())
		{
			o = p.pull();
			System.out.println(evt_cnt + "," + o);
			evt_cnt++;
		}
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
