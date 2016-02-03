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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class Utilities
{
	/**
	 * Get an InputStream from a filename
	 * @param filename The filename
	 * @return The input stream
	 */
	public static InputStream getFileInputStream(String filename)
	{
		InputStream is = null;
		try
		{
			is = new FileInputStream(new File(filename));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return is;
	}

	/**
	 * Write bytes to a file
	 */
	public static void writeBytes(String filename, byte[] contents)
	{
		FileOutputStream stream = null;
		try
		{
			stream = new FileOutputStream(filename);
		} catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try 
		{
			stream.write(contents);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		finally 
		{
			try
			{
				stream.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}

	/**
	 * Write a string to a file
	 */
	public static void writeString(String filename, String contents)
	{
		FileOutputStream stream = null;
		PrintStream ps = null;
		try
		{
			stream = new FileOutputStream(filename);
			ps = new PrintStream(stream);
		} catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try 
		{
			ps.print(contents);
		}
		finally 
		{
			try
			{
				stream.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}

}
