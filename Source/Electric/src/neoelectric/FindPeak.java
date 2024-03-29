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
package neoelectric;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.signal.Limit;
import ca.uqac.lif.cep.signal.PeakFinderLocalMaximum;
import ca.uqac.lif.cep.signal.Threshold;

class FindPeak extends GroupProcessor
{
	/**
	 * Instantiates a signal processor
	 * @param threshold The threshold
	 */
	public FindPeak(int threshold)
	{
		super(1, 1);
		// Pass through peak detector
		Processor finder = new PeakFinderLocalMaximum(5);
		// Threshold to avoid finding peaks due to noise
		Threshold th = new Threshold(threshold);
		Connector.connect(finder, th);
		// Dampen to avoid double peaks
		Processor damper = new Limit(10);
		Connector.connect(th, damper);
		// Bundle everything into a group
		addProcessors(finder, th, damper);
		associateInput(0, finder, 0);
		associateOutput(0, damper, 0);
	}
}
