package neoelectric.demo;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.mtnp.UpdateTableMap;
import ca.uqac.lif.cep.tuples.MergeTuples;
import ca.uqac.lif.cep.tuples.ScalarIntoTuple;
import ca.uqac.lif.cep.tuples.Tuple;
import ca.uqac.lif.cep.tuples.TupleMap;
import neoelectric.ApplianceSignature;
import neoelectric.ApplianceMooreMachine;
import neoelectric.ProcessAllEnvelopes;
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
      Tuple off = new TupleMap();
      off.put("W1-T", -400);
      sig = new ApplianceSignature(on, off);
    }
    SimulateAppliance appl = new SimulateAppliance(ProcessAllEnvelopesDemo.class, "data/A1", "W1", "W2");
    ApplyFunction time_tup = new ApplyFunction(new ScalarIntoTuple("Time"));
    Connector.connect(appl, 0, time_tup, 0);
    ProcessAllEnvelopes pae = new ProcessAllEnvelopes("W1", "W2");
    Connector.connect(appl, 1, pae, 0);
    Connector.connect(appl, 2, pae, 1);
    ApplianceMooreMachine amm = new ApplianceMooreMachine(sig, 100);
    Connector.connect(pae, amm);
    Pullable p = amm.getPullableOutput();
    while (p.hasNext())
    {
      System.out.println(p.pull());
    }
  }

}
