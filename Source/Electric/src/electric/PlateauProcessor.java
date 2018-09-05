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

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.signal.Limit;
import ca.uqac.lif.cep.signal.PlateauFinder;
import ca.uqac.lif.cep.tuples.FetchAttribute;

class PlateauProcessor extends GroupProcessor
{
	/**
	 * Instantiates a signal processor
	 * @param input The input for the signal processor
	 * @param component The electrical component to monitor
	 * @param range The threshold 
	 */
	public PlateauProcessor(Processor input, String component, int range)
	{
		super(1, 1);
		// Keep a single attribute
		ApplyFunction select = new ApplyFunction(new FetchAttribute(component));
		//select.setProcessor("", input);
		// Pass through peak detector
		Processor finder = new PlateauFinder().setPlateauRange(range).setRelative(true);
		Connector.connect(select, finder);
		// Threshold to avoid finding peaks due to noise
		//Threshold th = new Threshold(threshold);
		//Connector.connect(finder, th);
		// Dampen to avoid double peaks
		Processor damper = new Limit(10);
		Connector.connect(finder, damper);
		// Bundle everything into a group
		addProcessors(select, finder, damper);
		associateInput(0, select, 0).associateOutput(0, damper, 0);
	}
}
