package neoelectric;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.signal.Limiter;
import ca.uqac.lif.cep.signal.PeakFinderLocalMaximum;
import ca.uqac.lif.cep.signal.Persist;
import ca.uqac.lif.cep.signal.PlateauFinder;
import ca.uqac.lif.cep.signal.Threshold;
import ca.uqac.lif.cep.tmf.Fork;

public class ProcessEnvelope extends GroupProcessor
{
  public static final int THRESHOLD = 100;
  
  protected static final int RANGE = 80;

  public ProcessEnvelope()
  {
    super(1, 2);
    Fork f = new Fork(2);
    // First branch: peak detection
    Processor peak_finder = new PeakFinderLocalMaximum(5);
    Connector.connect(f, 0, peak_finder, 0);
    // Threshold to avoid finding peaks due to noise
    Threshold peak_th = new Threshold(THRESHOLD);
    Connector.connect(peak_finder, peak_th);
    // Dampen to avoid double peaks
    Processor peak_damper = new Limiter(10);
    Connector.connect(peak_th, peak_damper);
    Persist peak_persist = new Persist(4);
    Connector.connect(peak_damper, peak_persist);
    
    // Second branch: plateau detection
    Processor plateau_finder = new PlateauFinder().setPlateauRange(RANGE).setRelative(true);
    Connector.connect(f, 1, plateau_finder, 0);
    // Threshold to avoid finding plateaus due to noise
    Threshold plateau_th = new Threshold(THRESHOLD);
    Connector.connect(plateau_finder, plateau_th);
    Processor plateau_damper = new Limiter(10);
    Connector.connect(plateau_th, plateau_damper);
    Persist plateau_persist = new Persist(4);
    Connector.connect(plateau_damper, plateau_persist);
    
    // Bundle everything into a group
    addProcessors(f, peak_finder, peak_th, peak_damper, peak_persist, plateau_finder, plateau_th, plateau_damper, plateau_persist);
    associateInput(0, f, 0);
    associateOutput(0, peak_persist, 0);
    associateOutput(1, plateau_persist, 0);
  }
}
