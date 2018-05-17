package electric;

import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.tmf.Sink;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Queue;

public class OverwriteFile extends Sink
{
  protected String m_filename;
  
  public OverwriteFile(String filename)
  {
    super();
    m_filename = filename;
  }
  
  @Override
  protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    FileOutputStream fos = null;
    ProcessorException ex = null;
    try
    {
      fos = new FileOutputStream(m_filename);
      if (inputs[0] instanceof byte[])
      {
        fos.write((byte[]) inputs[0]);
      }
      else
      {
        fos.write(inputs[0].toString().getBytes());
      }
    }
    catch (FileNotFoundException e)
    {
      throw new ProcessorException(e);
    }
    catch (IOException e)
    {
      ex = new ProcessorException(e);
    }
    if (fos != null)
    {
      try
      {
        fos.close();
      }
      catch (IOException e)
      {
        ex = new ProcessorException(e);
      }
    }
    if (ex != null)
    {
      throw ex;
    }
    return true;
  }

  @Override
  public OverwriteFile duplicate(boolean with_state)
  {
    return new OverwriteFile(m_filename);
  }
}
