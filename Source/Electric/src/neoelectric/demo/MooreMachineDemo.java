/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2018 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package neoelectric.demo;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tuples.Tuple;
import ca.uqac.lif.cep.tuples.TupleMap;
import ca.uqac.lif.cep.util.Bags;
import neoelectric.ApplianceSignature;
import neoelectric.ApplianceMooreMachine;
import neoelectric.ProcessAllEnvelopesTuple;
import neoelectric.SimulateAppliance;

public class MooreMachineDemo
{

  public static void main(String[] args)
  {
    ApplianceSignature sig;
    {
      Tuple on = new TupleMap();
      on.put("W1-K", 1000);
      on.put("W1-T", 400);
      on.put("W2-K", 500);
      on.put("W2-T", 200);
      Tuple off = new TupleMap();
      off.put("W1-T", -400);
      off.put("W2-T", -200);
      sig = new ApplianceSignature(on, off);
    }
    SimulateAppliance appl = new SimulateAppliance(ProcessAllEnvelopesDemo.class, "data/A1", "W1", "W2");
    
    
    ProcessAllEnvelopesTuple pae = new ProcessAllEnvelopesTuple("W1", "W2");
    Connector.connect(appl, 1, pae, 0);
    Connector.connect(appl, 2, pae, 1);
    ApplianceMooreMachine amm = new ApplianceMooreMachine(sig, 100);
    Connector.connect(pae, amm);
    ApplyFunction ta = new ApplyFunction(new Bags.ToArray(Number.class, ApplianceMooreMachine.State.class));
    Connector.connect(appl, 0, ta, 0);
    Connector.connect(amm, 0, ta, 1);
    Pullable p = ta.getPullableOutput();
    while (p.hasNext())
    {
      Object[] o = (Object[]) p.pull();
      System.out.println(o[0] + "," + o[1]);
    }
  }

}
