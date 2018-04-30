package electric;

import ca.uqac.lif.cep.tuples.FetchAttribute;
import ca.uqac.lif.cep.util.Booleans;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.fsm.FunctionTransition;
import ca.uqac.lif.cep.fsm.MooreMachine;
import ca.uqac.lif.cep.fsm.TransitionOtherwise;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;

public class ElectricMooreMachine extends MooreMachine
{
	// State names; this is just to improve readability
	private static final int ST_0 = 0;
	private static final int ST_1 = 1;
	private static final int ST_2 = 2;
	private static final int ST_3 = 3;
	private static final int ST_4 = 4;
	
	/**
	 * Instantiates a Moore machine whose transition relation recognizes the
	 * life cycle of an electrical appliance.
	 * @param app_name The name of the appliance
	 * @param component The name of the electrical component to look for
	 * @param peak The value (in watts) of the peak to be detected 
	 * @param plateau The value of the plateau to be detected
	 * @param drop The value of the drop to be detected
	 * @param interval The interval of error. Any value in the open range
	 *   ]x-interval, x+interval[ will fire the transition for x. 
	 */
	public ElectricMooreMachine(String app_name, String component, int peak, int plateau, int drop, int interval)
	{
		super(1, 1);
		String anq_peak = "PK-" + component;
		String anq_plateau = "PT-" + component;
		// Create transition relation
		addTransition(ST_0, new FunctionTransition(// in state 0, event = peak, go to state 1
		withinRange(anq_peak, peak, interval),
				ST_1));
		addTransition(ST_0,
				// in state 0, event = otherwise, go to state 0
				new TransitionOtherwise(ST_0));
		addTransition(ST_1, new FunctionTransition(// in state 1, event = plateau, go to state 2
		withinRange(anq_plateau, plateau, interval),
				ST_2));
		addTransition(ST_1,
				// in state 1, event = otherwise, go to state 1
				new TransitionOtherwise(ST_1));
		addTransition(ST_2, new FunctionTransition(// in state 2, event = drop, go to state 3
		withinRange(anq_peak, drop, interval),
				ST_3));
		addTransition(ST_2,
				// in state 2, event = otherwise, go to state 4
				new TransitionOtherwise(ST_4));
		addTransition(ST_3, new FunctionTransition(// in state 3, event = peak, go to state 1
		withinRange(anq_peak, peak, interval),
				ST_1));
		addTransition(ST_3,
				// in state 3, event = otherwise, go to state 0
				new TransitionOtherwise(ST_0));
		addTransition(ST_4, new FunctionTransition(// in state 4, event = drop, go to state 3
		withinRange(anq_peak, drop, interval),
				ST_3));
		addTransition(ST_4,
				// in state 4, event = otherwise, go to state 4
				new TransitionOtherwise(ST_4));
		// Add symbols to some states
		addSymbol(ST_2, new Constant(new ApplianceOn(app_name)));
		addSymbol(ST_3, new Constant (new ApplianceOff(app_name)));
	}

	/**
	 * Generate a transition expressing the fact that a value is within a range
	 * @param value The value
	 * @param interval The half-width of the range
	 * @return The condition
	 */
	private static Function withinRange(String component, int value, int interval)
	{
		FunctionTree ft = new FunctionTree(Booleans.and,
				new FunctionTree(Numbers.isGreaterThan, 
						new FunctionTree(new FetchAttribute(component), StreamVariable.X),
						new Constant(value - interval)),
				new FunctionTree(Numbers.isLessThan, 
						new FunctionTree(new FetchAttribute(component), StreamVariable.X),
						new Constant(value + interval)));
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
