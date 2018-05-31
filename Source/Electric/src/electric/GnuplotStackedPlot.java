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

import ca.uqac.lif.cep.gnuplot.GnuplotScatterplot;
import ca.uqac.lif.cep.gnuplot.Multiset;

@SuppressWarnings("deprecation")
public class GnuplotStackedPlot extends GnuplotScatterplot
{
	@Override
	protected String generatePlot(Multiset bag)
	{
		StringBuilder out = new StringBuilder();
		StringBuilder plot_data = generatePlotData(bag);
		out.append("set terminal ").append(m_terminal).append("\n");
		out.append("set title \"").append(m_title).append("\"\n");
		out.append("set xlabel \"").append(m_labelX).append("\"\n");
		out.append("set ylabel \"").append(m_labelY).append("\"\n");
		out.append("set datafile separator \",\"\n");
		out.append("plot ");
		for (int i = 0; i < m_otherHeaders.length; i++)
		{
			String header = m_otherHeaders[m_otherHeaders.length - i - 1];
			if (i > 0)
			{
				out.append(", ");
			}
			out.append("\"-\" u 1:($2");
			for (int j = 3; j < (m_otherHeaders.length - i) + 2; j++)
			{
				out.append("+$").append(j);
			}
			out.append(") t \"").append(header).append("\" w filledcurves x1");
		}
		out.append("\n");
		// Repeat the data as many times as there are columns
		for (int i = 0; i < m_otherHeaders.length; i++)
		{
			out.append(plot_data).append("\ne\n");
		}
		return out.toString();
	}
}
