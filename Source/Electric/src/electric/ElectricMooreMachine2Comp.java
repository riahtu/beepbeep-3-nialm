package electric;

import ca.uqac.lif.cep.eml.tuples.AttributeNameQualified;
import ca.uqac.lif.cep.eml.tuples.Conjunction;
import ca.uqac.lif.cep.eml.tuples.GreaterThan;
import ca.uqac.lif.cep.eml.tuples.LessThan;
import ca.uqac.lif.cep.eml.tuples.NumberExpression;
import ca.uqac.lif.cep.eml.tuples.Select;
import ca.uqac.lif.cep.ltl.MooreMachine;
import ca.uqac.lif.cep.ltl.ProcessorTransition;

public class ElectricMooreMachine2Comp extends MooreMachine
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
	public ElectricMooreMachine2Comp(String app_name, String component1, String component2, int peak, int plateau, int drop, int interval)
	{
		super(1, 1);
		AttributeNameQualified anq_peak_1 = new AttributeNameQualified("PK-" + component1);
		AttributeNameQualified anq_plateau_1 = new AttributeNameQualified("PT-" + component1);
		AttributeNameQualified anq_peak_2 = new AttributeNameQualified("PK-" + component2);
		AttributeNameQualified anq_plateau_2 = new AttributeNameQualified("PT-" + component2);
		// Create transition relation
		addTransition(ST_0, new ProcessorTransition(ST_1,
				// in state 0, event = peak, go to state 1
				withinRange(anq_peak_1, anq_peak_2, peak, interval)));
		addTransition(ST_0,
				// in state 0, event = otherwise, go to state 0
				new TransitionOtherwise(ST_0));
		addTransition(ST_1, new ProcessorTransition(ST_2,
				// in state 1, event = plateau, go to state 2
				withinRange(anq_plateau_1, anq_plateau_2, plateau, interval)));
		addTransition(ST_1,
				// in state 1, event = otherwise, go to state 1
				new TransitionOtherwise(ST_1));
		addTransition(ST_2, new ProcessorTransition(ST_3,
				// in state 2, event = drop, go to state 3
				withinRange(anq_peak_1, anq_peak_2, drop, interval)));
		addTransition(ST_2,
				// in state 2, event = otherwise, go to state 4
				new TransitionOtherwise(ST_4));
		addTransition(ST_3, new ProcessorTransition(ST_1,
				// in state 3, event = peak, go to state 1
				withinRange(anq_peak_1, anq_peak_2, peak, interval)));
		addTransition(ST_3,
				// in state 3, event = otherwise, go to state 0
				new TransitionOtherwise(ST_0));
		addTransition(ST_4, new ProcessorTransition(ST_3,
				// in state 4, event = drop, go to state 3
				withinRange(anq_peak_1, anq_peak_2, drop, interval)));
		addTransition(ST_4,
				// in state 4, event = otherwise, go to state 4
				new TransitionOtherwise(ST_4));
		// Add symbols to some states
		addSymbol(ST_2, new ElectricMooreMachine.ApplianceOn(app_name));
		addSymbol(ST_3, new ElectricMooreMachine.ApplianceOff(app_name));
	}

	/**
	 * Generate a transition expressing the fact that a value is within a range
	 * (i.e. a <code>SELECT</code> expression)
	 * @param value The value
	 * @param interval The half-width of the range
	 * @return The condition
	 */
	private static Select withinRange(AttributeNameQualified component1, AttributeNameQualified component2, int value, int interval)
	{
		Conjunction and1 = null;
		Conjunction and2 = null;
		{
			GreaterThan gt1 = new GreaterThan(component1, new NumberExpression(value - interval));
			LessThan lt1 = new LessThan(component1, new NumberExpression(value + interval));
			and1 = new Conjunction(gt1, lt1);
		}
		{
			GreaterThan gt2 = new GreaterThan(component2, new NumberExpression(value - interval));
			LessThan lt2 = new LessThan(component2, new NumberExpression(value + interval));
			and2 = new Conjunction(gt2, lt2);
		}
		Conjunction and = new Conjunction(and1, and2);
		return new Select(1, and);
	}
}
