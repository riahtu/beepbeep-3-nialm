package neoelectric.demo;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.mtnp.DrawPlot;
import ca.uqac.lif.cep.mtnp.UpdateTableMap;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.KeepLast;
import ca.uqac.lif.cep.tmf.Pump;
import ca.uqac.lif.cep.tuples.MergeTuples;
import ca.uqac.lif.cep.tuples.ScalarIntoTuple;
import ca.uqac.lif.cep.util.NthElement;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.widgets.WidgetSink;
import ca.uqac.lif.mtnp.plot.Plot.ImageType;
import ca.uqac.lif.mtnp.plot.gnuplot.Scatterplot;
import neoelectric.ProcessAllEnvelopes;
import neoelectric.SimulateAppliance;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ProcessAllEnvelopesDemo
{
	public static void main(String[] args)
	{
		SimulateAppliance sm_W1 = new SimulateAppliance(ProcessAllEnvelopesDemo.class.getResourceAsStream("data/A1/W1.csv"), 60f);
		SimulateAppliance sm_W2 = new SimulateAppliance(ProcessAllEnvelopesDemo.class.getResourceAsStream("data/A1/W2.csv"), 60f);
		Fork fork = new Fork(2);
		Connector.connect(sm_W1, fork);
		TurnInto one = new TurnInto(1);
		Connector.connect(fork, 0, one, 0);
		Cumulate sum_one = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
		Connector.connect(one, sum_one);
		ApplyFunction time_tuple = new ApplyFunction(new ScalarIntoTuple("Time"));
		Connector.connect(sum_one, time_tuple);
		ProcessAllEnvelopes pae = new ProcessAllEnvelopes("W1", "W2");
		Connector.connect(fork, 1, pae, 0);
		Connector.connect(sm_W2, 0, pae, 1);
		ApplyFunction merge_tup = new ApplyFunction(new MergeTuples());
		Connector.connect(time_tuple, 0, merge_tup, 0);
		Connector.connect(pae, 0, merge_tup, 1);
		UpdateTableMap uts = new UpdateTableMap("Time", "W1-K", "W1-T", "W2-K", "W2-T");
		Connector.connect(merge_tup, 0, uts, 0);
		KeepLast last = new KeepLast(1);
		Connector.connect(uts, last);
		Scatterplot plot = new Scatterplot();
		DrawPlot d_plot = new DrawPlot(plot);
		d_plot.setImageType(ImageType.PNG);
		Connector.connect(last, d_plot);
		Pump pump = new Pump();
		Connector.connect(d_plot, pump);
		JFrame frame = new JFrame("Graph");
		JLabel label;
		{
		  frame.setSize(640, 550);
		  JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		  frame.add(panel);
		  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		  label = new JLabel();
	    panel.add(label);
		}
		WidgetSink ws = new WidgetSink(label);
		Connector.connect(pump, ws);
		frame.setVisible(true);
		pump.start();
	}
}
