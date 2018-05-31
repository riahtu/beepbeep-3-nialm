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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tuples.FetchAttribute;
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
			ApplyFunction select = new ApplyFunction(new FetchAttribute("WL1"));
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
