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
		Plotter plot = new Plotter("TIME", "graph-foo.pdf", "", "", "");
		Connector.connect(feeder,  plot);
		plot.plot(3);

	}

}
