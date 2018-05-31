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
    if (m_filename.isEmpty())
    {
      return true;
    }
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
