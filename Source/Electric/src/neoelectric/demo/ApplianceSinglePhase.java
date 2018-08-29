package neoelectric.demo;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.mtnp.DrawPlot;
import ca.uqac.lif.cep.mtnp.UpdateTableStream;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.KeepLast;
import ca.uqac.lif.cep.tmf.Pump;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.widgets.WidgetSink;
import ca.uqac.lif.mtnp.plot.Plot.ImageType;
import ca.uqac.lif.mtnp.plot.gnuplot.Scatterplot;
import neoelectric.ProcessEnvelope;
import neoelectric.SimulateAppliance;

import java.awt.FlowLayout;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ApplianceSinglePhase
{
	public static void main(String[] args)
	{
		InputStream is = ApplianceSinglePhase.class.getResourceAsStream("data/test-stutter.csv");
		SimulateAppliance sm = new SimulateAppliance(is, 60f);
		Fork f1 = new Fork(3);
		Connector.connect(sm, f1);
		TurnInto one = new TurnInto(1);
		Connector.connect(f1, 0, one, 0);
		Cumulate sum_one = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
		Connector.connect(one, sum_one);
		UpdateTableStream uts = new UpdateTableStream("Time", "W", "Peak", "Plateau");
		Connector.connect(sum_one, 0, uts, 0);
		Connector.connect(f1, 1, uts, 1);
		ProcessEnvelope ed = new ProcessEnvelope();
		Connector.connect(f1, 2, ed, 0);
    Connector.connect(ed, 0, uts, 2);
    Connector.connect(ed, 1, uts, 3);
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
