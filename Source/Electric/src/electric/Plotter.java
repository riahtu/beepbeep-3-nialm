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
 */
package electric;

import java.io.File;

import ca.uqac.lif.cep.Combiner;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.epl.CountDecimate;
import ca.uqac.lif.cep.gnuplot.GnuplotProcessor;
import ca.uqac.lif.cep.gnuplot.GnuplotScatterplot;
import ca.uqac.lif.cep.io.Caller;
import ca.uqac.lif.cep.io.FileWriter;
import ca.uqac.lif.cep.sets.BagUnion;

public class Plotter extends GroupProcessor
{
	public static final int s_decimateInterval = 200;
	
	public static final GnuplotProcessor.Terminal s_terminal = GnuplotProcessor.Terminal.PDF;
	
	public static final String s_gnuplotCommand = "gnuplot";
	
	private final FileWriter m_writer;
	
	private boolean m_pullHard = true;
	
	public Plotter(GnuplotStackedPlot plot, String x_axis, String filename, String title, String x_title, String y_title)
	{
		super(1, 0);
		Combiner union = new Combiner(new BagUnion());
		// Decimate the results (keep one every 200)
		CountDecimate decimate = new CountDecimate(s_decimateInterval);
		Connector.connect(union, decimate);
		// Connect a Gnuplot to the decimated results
		plot.setX(x_axis).setRaw(true).setTerminal(s_terminal).setTitle(title);
		plot.setXTitle(x_title).setYTitle(y_title);
		Connector.connect(decimate, plot);
		// Connect a caller to gnuplot on the plot
		Caller gnuplot = new Caller(s_gnuplotCommand);
		Connector.connect(plot, gnuplot);
		m_writer = new FileWriter(new File(filename), false);
		Connector.connect(gnuplot, m_writer);
		// Bundle
		addProcessors(union, decimate, plot, gnuplot, m_writer);
		this.associateInput(0, union, 0);
	}

	public Plotter(String x_axis, String filename, String title, String x_title, String y_title)
	{
		super(1, 0);
		Combiner union = new Combiner(new BagUnion());
		// Decimate the results (keep one every 200)
		CountDecimate decimate = new CountDecimate(s_decimateInterval);
		Connector.connect(union, decimate);
		// Connect a Gnuplot to the decimated results
		GnuplotScatterplot plot = new GnuplotScatterplot();
		plot.setX(x_axis).setRaw(true).setTerminal(s_terminal).setTitle(title);
		plot.setXTitle(x_title).setYTitle(y_title);
		Connector.connect(decimate, plot);
		// Connect a caller to gnuplot on the plot
		Caller gnuplot = new Caller(s_gnuplotCommand);
		Connector.connect(plot, gnuplot);
		m_writer = new FileWriter(new File(filename), false);
		Connector.connect(gnuplot, m_writer);
		// Bundle
		addProcessors(union, decimate, plot, gnuplot, m_writer);
		this.associateInput(0, union, 0);
	}
	
	public Plotter setPullHard(boolean b)
	{
		m_pullHard = b;
		return this;
	}
	
	public Plotter close()
	{
		if (m_writer != null)
		{
			m_writer.close();
		}
		return this;
	}
	
	public Plotter plot(int repetitions)
	{
		for (int i = 0; i < repetitions; i++)
		{
			if (m_pullHard)
			{
				m_writer.pullHard();
			}
			else
			{
				m_writer.pull();
			}
		}
		return this;
	}
	
	public Plotter plot()
	{
		if (m_pullHard)
		{
			m_writer.pullHard();
		}
		else
		{
			m_writer.pull();
		}
		return this;
	}
}
