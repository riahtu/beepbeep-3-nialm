package electric;

import ca.uqac.lif.cep.fsm.FunctionTransition;
import ca.uqac.lif.cep.fsm.MooreMachine;
import ca.uqac.lif.cep.fsm.TransitionOtherwise;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.tuples.FetchAttribute;
import ca.uqac.lif.cep.util.Booleans;
import ca.uqac.lif.cep.util.Numbers;

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
    String anq_peak_1 = "PK-" + component1;
    String anq_plateau_1 = "PT-" + component1;
    String anq_peak_2 = "PK-" + component2;
    String anq_plateau_2 = "PT-" + component2;
    // Create transition relation
    addTransition(ST_0, new FunctionTransition(// in state 0, event = peak, go to state 1
        withinRange(anq_peak_1, anq_peak_2, peak, interval), ST_1));
    addTransition(ST_0,
        // in state 0, event = otherwise, go to state 0
        new TransitionOtherwise(ST_0));
    addTransition(ST_1, new FunctionTransition(// in state 1, event = plateau, go to state 2
        withinRange(anq_plateau_1, anq_plateau_2, plateau, interval),
        ST_2));
    addTransition(ST_1,
        // in state 1, event = otherwise, go to state 1
        new TransitionOtherwise(ST_1));
    addTransition(ST_2, new FunctionTransition(// in state 2, event = drop, go to state 3
        withinRange(anq_peak_1, anq_peak_2, drop, interval),
        ST_3));
    addTransition(ST_2,
        // in state 2, event = otherwise, go to state 4
        new TransitionOtherwise(ST_4));
    addTransition(ST_3, new FunctionTransition(// in state 3, event = peak, go to state 1
        withinRange(anq_peak_1, anq_peak_2, peak, interval),
        ST_1));
    addTransition(ST_3,
        // in state 3, event = otherwise, go to state 0
        new TransitionOtherwise(ST_0));
    addTransition(ST_4, new FunctionTransition(// in state 4, event = drop, go to state 3
        withinRange(anq_peak_1, anq_peak_2, drop, interval),
        ST_3));
    addTransition(ST_4,
        // in state 4, event = otherwise, go to state 4
        new TransitionOtherwise(ST_4));
    // Add symbols to some states
    addSymbol(ST_2, new Constant(new ElectricMooreMachine.ApplianceOn(app_name)));
    addSymbol(ST_3, new Constant(new ElectricMooreMachine.ApplianceOff(app_name)));
  }

  /**
   * Generate a transition expressing the fact that a value is within a range
   * (i.e. a <code>SELECT</code> expression)
   * @param value The value
   * @param interval The half-width of the range
   * @return The condition
   */
  private static FunctionTree withinRange(String component1, String component2, int value, int interval)
  {
    FunctionTree ft1 = new FunctionTree(Booleans.and,
        new FunctionTree(Numbers.isGreaterThan, 
            new FunctionTree(new FetchAttribute(component1), StreamVariable.X),
            new Constant(value - interval)),
        new FunctionTree(Numbers.isLessThan, 
            new FunctionTree(new FetchAttribute(component1), StreamVariable.X),
            new Constant(value + interval)));
    FunctionTree ft2 = new FunctionTree(Booleans.and,
        new FunctionTree(Numbers.isGreaterThan, 
            new FunctionTree(new FetchAttribute(component2), StreamVariable.X),
            new Constant(value - interval)),
        new FunctionTree(Numbers.isLessThan, 
            new FunctionTree(new FetchAttribute(component2), StreamVariable.X),
            new Constant(value + interval)));
    return new FunctionTree(Booleans.and, ft1, ft2);
  }
}
