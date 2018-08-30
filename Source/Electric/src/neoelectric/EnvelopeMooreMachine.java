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

import ca.uqac.lif.cep.util.Booleans;
import ca.uqac.lif.cep.util.Numbers;
import java.util.Set;
import ca.uqac.lif.cep.fsm.FunctionTransition;
import ca.uqac.lif.cep.fsm.MooreMachine;
import ca.uqac.lif.cep.fsm.TransitionOtherwise;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.tuples.GetAttribute;
import ca.uqac.lif.cep.tuples.Tuple;

public class EnvelopeMooreMachine extends MooreMachine
{
  // State names; this is just to improve readability
  private static final int ST_0 = 0;
  private static final int ST_1 = 1;
  private static final int ST_2 = 2;
  private static final int ST_3 = 3;
  private static final int ST_4 = 4;

  public static enum State {TURN_ON, TURN_OFF}

  public static enum SignalFeature {DROP, PEAK, PLATEAU}

  protected Set<String> m_componentNames;

  /**
   * Instantiates a Moore machine that recognizes an envelope on an
   * input signal.
   * @param sig The signature of the appliance to detect
   * @param interval The interval of error. Any value in the open range
   *   ]x-interval, x+interval[ will fire the transition for x. 
   */
  public EnvelopeMooreMachine(ApplianceSignature sig, int interval)
  {
    super(1, 1);

    // Get the names of the signal components in this machine
    m_componentNames = sig.getComponentNames();

    // Create transition relation
    addTransition(ST_0, new FunctionTransition(// in state 0, event = peak, go to state 1
        checkComponent(sig.getOnEnvelope(), SignalFeature.PEAK, interval),
        ST_1));
    addTransition(ST_0,
        // in state 0, event = otherwise, go to state 0
        new TransitionOtherwise(ST_0));
    addTransition(ST_1, new FunctionTransition(// in state 1, event = plateau, go to state 2
        checkComponent(sig.getOnEnvelope(), SignalFeature.PLATEAU, interval),
        ST_2));
    addTransition(ST_1,
        // in state 1, event = otherwise, go to state 1
        new TransitionOtherwise(ST_1));
    addTransition(ST_2, new FunctionTransition(// in state 2, event = drop, go to state 3
        checkComponent(sig.getOffEnvelope(), SignalFeature.DROP, interval),
        ST_3));
    addTransition(ST_2,
        // in state 2, event = otherwise, go to state 2
        new TransitionOtherwise(ST_2));
    addTransition(ST_3,
        // in state 3, event = otherwise, go to state 0
        new TransitionOtherwise(ST_0));
    // Add symbols to some states
    addSymbol(ST_2, new Constant(State.TURN_ON));
    addSymbol(ST_3, new Constant(State.TURN_ON));
  }

  protected FunctionTree checkComponent(Tuple sig, SignalFeature feature, int interval)
  {
    String component_suffix = "-T";
    if (feature == SignalFeature.PEAK)
    {
      component_suffix = "-P";
    }
    FunctionTree big_and = new FunctionTree(Booleans.and);
    big_and.setChild(0, new Constant(true));
    for (String sig_comp : m_componentNames)
    {
      String attribute_name = sig_comp + component_suffix;
      FunctionTree range = new FunctionTree(Numbers.isLessThan,
          new FunctionTree(Numbers.absoluteValue,
              new FunctionTree(Numbers.subtraction),
              new Constant(sig.get(attribute_name)),
              new FunctionTree(new GetAttribute(attribute_name), 
                  StreamVariable.X)),
          new Constant(interval));
      big_and.setChild(1, range);
      FunctionTree new_and = new FunctionTree(Booleans.and);
      new_and.setChild(0, big_and);
      big_and = new_and;
    }
    return big_and;
  }
}
