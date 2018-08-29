package neoelectric;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.mtnp.DrawPlot;
import ca.uqac.lif.cep.mtnp.UpdateTableStream;
import ca.uqac.lif.cep.signal.Persist;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.KeepLast;
import ca.uqac.lif.cep.tmf.Pump;
import ca.uqac.lif.cep.util.NthElement;
import ca.uqac.lif.cep.widgets.WidgetSink;
import ca.uqac.lif.mtnp.plot.Plot.ImageType;
import ca.uqac.lif.mtnp.plot.gnuplot.Scatterplot;

import java.awt.FlowLayout;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.junit.Test;

public class SimulateApplianceTest
{
	public static void main(String[] args)
	{
		InputStream is = SimulateApplianceTest.class.getResourceAsStream("data/test-stutter.csv");
		SimulateAppliance sm = new SimulateAppliance(is, 60f);
		UpdateTableStream uts = new UpdateTableStream("Time", "W", "Peak", "Plateau");
		Connector.connect(sm, 0, uts, 0);
		Fork fork = new Fork(2);
		Connector.connect(sm, 1, fork, 0);
		Connector.connect(fork, 0, uts, 1);
		EnvelopeDetector ed = new EnvelopeDetector();
		Connector.connect(fork, 1, ed, 0);
		Fork fork2 = new Fork(2);
		Connector.connect(ed, fork2);
		ApplyFunction npk = new ApplyFunction(new NthElement(0));
		Connector.connect(fork2, 0, npk, 0);
		ApplyFunction npt = new ApplyFunction(new NthElement(1));
    Connector.connect(fork2, 1, npt, 0);
    Connector.connect(npk, 0, uts, 2);
    Connector.connect(npt, 0, uts, 3);
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
