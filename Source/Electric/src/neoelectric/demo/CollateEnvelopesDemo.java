package neoelectric.demo;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tmf.BlackHole;
import ca.uqac.lif.cep.tmf.KeepLast;
import ca.uqac.lif.cep.tuples.MergeScalars;

import neoelectric.CollateEnvelopes;
import neoelectric.ProcessAllEnvelopes;
import neoelectric.SimulateAppliance;

public class CollateEnvelopesDemo
{
	public static void main(String[] args)
	{
	  SimulateAppliance appl = new SimulateAppliance(CollateEnvelopesDemo.class, "data/A2", "W1", "W2");
	  BlackHole bh = new BlackHole();
    Connector.connect(appl, 0, bh, 0);
		ProcessAllEnvelopes pae = new ProcessAllEnvelopes("W1", "W2");
		Connector.connect(appl, 1, pae, 0);
		Connector.connect(appl, 2, pae, 1);
		CollateEnvelopes ce = new CollateEnvelopes("W1-K", "W1-T", "W2-K", "W2-T");
		Connector.connect(pae, ce);
		KeepLast last = new KeepLast(4);
		Connector.connect(ce, last);
		ApplyFunction to_array = new ApplyFunction(new MergeScalars("W1-K", "W1-T", "W2-K", "W2-T"));
		Connector.connect(last, to_array);
		Pullable p = to_array.getPullableOutput();
		System.out.println(p.pull());
	}
}
