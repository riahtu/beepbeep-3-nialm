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
 */package electric;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.SingleProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tuples.ScalarIntoTuple;
import ca.uqac.lif.cep.tuples.Select;
import ca.uqac.lif.cep.tuples.TupleFeeder;
import ca.uqac.lif.cep.tmf.CountDecimate;
import ca.uqac.lif.cep.gnuplot.GnuplotProcessor;
import ca.uqac.lif.cep.gnuplot.GnuplotScatterplot;
import ca.uqac.lif.cep.io.ReadLines;
import ca.uqac.lif.cep.signal.PeakFinderLocalMaximum;
import ca.uqac.lif.cep.signal.PeakFinderTravelRise;
import ca.uqac.lif.cep.signal.Threshold;
import ca.uqac.lif.cep.signal.Limiter;

@SuppressWarnings({ "unused", "deprecation" })
public class Learning
{

	public static void main(String[] args) throws FileNotFoundException
	{
		String[] tools = {"test"};
		for (String tool : tools)
		{
			for (int num_test = 1; num_test <= 5; num_test++)
			{
				detectPeakOnAppliance(tool, "WL1", num_test, true);
				detectPlateauOnAppliance(tool, "WL1", num_test, true);
			}
		}
		
	}
	
	static void detectPeakOnAppliance(String appli, String component, int num_test, boolean to_plot) throws FileNotFoundException
	{
		String filename = "data/" + appli + num_test + ".csv";
		// Get the reader from the filename
		InputStream is = Utilities.getFileInputStream(filename);
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
			select_1 = new Project(new String[]{"T"}, new String[]{"TIME", "WL1", "WL2", "WL3", "VARL1", "VARL2", "VARL3"});
			Connector.connect(fork, 0, select_1, 0);
		}
		// On second branch...
		PeakProcessor finder = new PeakProcessor(feeder, component, 100);
		Connector.connect(fork, 1, finder, 0);
		ApplyFunction sit = new ApplyFunction(new ScalarIntoTuple("x"));
		Connector.connect(finder, sit);
		// Join the two outputs
		Project select = new Project(new String[]{"S", "T"}, new String[]{"S.TIME", "S.WL1", "S.WL2", "S.WL3", "S.VARL1", "S.VARL2", "S.VARL3", "T.x"});
		Connector.connect(select_1, 0, select, 0);
		Connector.connect(sit, 0, select, 1);
		// Plug into a plotter
		Plotter plotter = new Plotter("TIME", "data/" + appli + num_test + ".pdf", "Raw signal", "Time(s)", "Power (W)");
		Connector.connect(select, plotter);
		plotter.plot(4);
		plotter.close();
	}
	
	static void detectPlateauOnAppliance(String appli, String component, int num_test, boolean to_plot) throws FileNotFoundException
	{
		String filename = "data/" + appli + num_test + ".csv";
		// Get the reader from the filename
		InputStream is = Utilities.getFileInputStream(filename);
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
		PeakProcessor finder = new PeakProcessor(feeder, component, 100);
		Connector.connect(fork, 1, finder, 0);
		ApplyFunction sit = new ApplyFunction(new ScalarIntoTuple("x"));
		Connector.connect(finder, sit);
		// Join the two outputs
		Project select = new Project(new String[] {"S", "T"}, new String[] {"S.TIME", "S.WL1", "S.WL2", "S.WL3", "S.VARL1", "S.VARL2", "S.VARL3", "T.x"});
		Connector.connect(select_1, 0, select, 0);
		Connector.connect(sit, 0, select, 1);
		// Plug into a plotter
		Plotter plotter = new Plotter("S.TIME", "data/" + appli + num_test + "-plateau.pdf", "Raw signal", "Time(s)", "Power (W)");
		Connector.connect(select, plotter);
		plotter.plot(4);
		plotter.close();
	}
}
