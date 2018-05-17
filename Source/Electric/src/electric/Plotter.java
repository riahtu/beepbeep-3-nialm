/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2018 Sylvain Hallé

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
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tmf.CountDecimate;
import ca.uqac.lif.cep.gnuplot.GnuplotCaller;
import ca.uqac.lif.cep.gnuplot.GnuplotScatterplot;
import ca.uqac.lif.cep.gnuplot.Multiset;
import ca.uqac.lif.cep.gnuplot.PlotFunction;

@SuppressWarnings("deprecation")
public class Plotter extends GroupProcessor
{
	public static final int s_decimateInterval = 50;
	
	public static final PlotFunction.Terminal s_terminal = PlotFunction.Terminal.PDF;
	
	private final OverwriteFile m_writer;
	
	private boolean m_pullHard = true;
	
	public Plotter(GnuplotStackedPlot plot, String x_axis, String filename, String title, String x_title, String y_title) throws FileNotFoundException
	{
		super(1, 0);
		Multiset.PutInto union = new Multiset.PutInto();
		// Decimate the results (keep one every 200)
		CountDecimate decimate = new CountDecimate(s_decimateInterval);
		Connector.connect(union, decimate);
		// Connect a Gnuplot to the decimated results
		plot.setX(x_axis);
		plot.setTerminal(s_terminal);
		plot.setTitle(title);
		plot.setLabelX(x_title).setLabelY(y_title);
		ApplyFunction g_plot = new ApplyFunction(plot);
		Connector.connect(decimate, g_plot);
		// Connect a caller to gnuplot on the plot
		GnuplotCaller gnuplot = new GnuplotCaller();
		Connector.connect(g_plot, gnuplot);
		m_writer = new OverwriteFile(filename);
		Connector.connect(gnuplot, m_writer);
		// Bundle
		addProcessors(union, decimate, g_plot, gnuplot, m_writer);
		this.associateInput(0, union, 0);
	}

	public Plotter(String x_axis, String filename, String title, String x_title, String y_title) throws FileNotFoundException
	{
		super(1, 0);
		
		Multiset.PutInto union = new Multiset.PutInto();
		associateInput(0, union, 0);
		// Decimate the results (keep one every 200)
		CountDecimate decimate = new CountDecimate(s_decimateInterval);
		Connector.connect(union, decimate);
		// Connect a Gnuplot to the decimated results
		GnuplotScatterplot plot = new GnuplotScatterplot();
		plot.setX(x_axis);
		plot.setTerminal(s_terminal);
		plot.setTitle(title);
		plot.setLabelX(x_title).setLabelY(y_title);
		ApplyFunction g_plot = new ApplyFunction(plot);
		Connector.connect(decimate, g_plot);
		// Connect a caller to gnuplot on the plot		
		GnuplotCaller gnuplot = new GnuplotCaller();
		Connector.connect(g_plot, gnuplot);
		m_writer = new OverwriteFile(filename);
		Connector.connect(gnuplot, m_writer);
		// Bundle
		addProcessors(union, decimate, g_plot, gnuplot, m_writer);
	}
	
	public Plotter setPullHard(boolean b)
	{
		m_pullHard = b;
		return this;
	}
	
	public Plotter close()
	{
		// This method no longer does anything
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
