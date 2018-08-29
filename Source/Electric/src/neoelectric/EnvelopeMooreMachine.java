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

import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.fsm.FunctionTransition;
import ca.uqac.lif.cep.fsm.MooreMachine;
import ca.uqac.lif.cep.fsm.TransitionOtherwise;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;

public class EnvelopeMooreMachine extends MooreMachine
{
	// State names; this is just to improve readability
	private static final int ST_0 = 0;
	private static final int ST_1 = 1;
	private static final int ST_2 = 2;
	private static final int ST_3 = 3;
	private static final int ST_4 = 4;
	
	public static enum State {TURN_ON, TURN_OFF}
	
	/**
	 * Instantiates a Moore machine that recognizes an envelope on an
	 * input signal.
	 * @param app_name The name of the appliance
	 * @param component The name of the electrical component to look for
	 * @param peak The value (in watts) of the peak to be detected 
	 * @param plateau The value of the plateau to be detected
	 * @param drop The value of the drop to be detected
	 * @param interval The interval of error. Any value in the open range
	 *   ]x-interval, x+interval[ will fire the transition for x. 
	 */
	public EnvelopeMooreMachine(int peak, int plateau, int drop, int interval)
	{
		super(2, 1);
		// Create transition relation
		addTransition(ST_0, new FunctionTransition(// in state 0, event = peak, go to state 1
		withinRange(peak, interval),
				ST_1));
		addTransition(ST_0,
				// in state 0, event = otherwise, go to state 0
				new TransitionOtherwise(ST_0));
		addTransition(ST_1, new FunctionTransition(// in state 1, event = plateau, go to state 2
		withinRange(plateau, interval),
				ST_2));
		addTransition(ST_1,
				// in state 1, event = otherwise, go to state 1
				new TransitionOtherwise(ST_1));
		addTransition(ST_2, new FunctionTransition(// in state 2, event = drop, go to state 3
		withinRange(drop, interval),
				ST_3));
		addTransition(ST_2,
				// in state 2, event = otherwise, go to state 4
				new TransitionOtherwise(ST_4));
		addTransition(ST_3, new FunctionTransition(// in state 3, event = peak, go to state 1
		withinRange(peak, interval),
				ST_1));
		addTransition(ST_3,
				// in state 3, event = otherwise, go to state 0
				new TransitionOtherwise(ST_0));
		addTransition(ST_4, new FunctionTransition(// in state 4, event = drop, go to state 3
		withinRange(drop, interval),
				ST_3));
		addTransition(ST_4,
				// in state 4, event = otherwise, go to state 4
				new TransitionOtherwise(ST_4));
		// Add symbols to some states
		addSymbol(ST_2, new Constant(State.TURN_ON));
		addSymbol(ST_3, new Constant(State.TURN_ON));
	}

	/**
	 * Generate a transition expressing the fact that a value is within a range
	 * @param value The value
	 * @param interval The half-width of the range
	 * @return The condition
	 */
	private static Function withinRange(int value, int interval)
	{
		FunctionTree ft = new FunctionTree(Numbers.isLessThan,
				new FunctionTree(Numbers.absoluteValue,
				    new FunctionTree(Numbers.subtraction,
				        StreamVariable.X, new Constant(value))),
				new Constant(interval));
		return ft;
	}

	public static abstract class ApplianceEvent
	{
		private final String m_name;

		private final String m_message;

		ApplianceEvent(String name, String message)
		{
			m_name = name;
			m_message = message;
		}
		
		public String getName()
		{
			return m_name;
		}
		
		public String getMessage()
		{
			return m_message;
		}

		@Override
		public String toString()
		{
			return m_name + " " + m_message;
		}
	}

	public static class ApplianceOn extends ApplianceEvent
	{
		public ApplianceOn(String name)
		{
			super(name, "ON");
		}
	}

	public static class ApplianceOff extends ApplianceEvent
	{
		public ApplianceOff(String name)
		{
			super(name, "OFF");
		}
	}
	
	public static class Signature
	{
		public String m_component;
		public int m_peak;
		public int m_plateau;
		public int m_drop;
		
		public Signature(String component, int peak, int plateau, int drop)
		{
			super();
			m_component = component;
			m_peak = peak;
			m_plateau = plateau;
			m_drop = drop;
		}
	}
}
