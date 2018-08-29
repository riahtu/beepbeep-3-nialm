package electric;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.tmf.Sink;

/**
 * Class ConvertJsonToCSV enable to convert JSON file to CSV file
 * @author stephaniett
 *
 */
public class ConvertJsonToCSV extends Sink {


	/*===== Variables globales =====*/
	
	protected String filenameJSON; //input
	protected String filenameCSV; //output
	
	/*=============================*/


	/**
	 * ConvertJsonToCSV : Constructor 1 param.
	 * The CSV file will be named outputConvertion.csv
	 * @param filenameJSON name of the JSON file with extension (.json)
	 */
	public ConvertJsonToCSV(String filenameJSON){
		super();
		this.filenameJSON = filenameJSON;
		this.filenameCSV = "outputConvertion.csv";
	}

	/**
	 * ConvertJsonToCSV : Constructor 2 param.
	 * CSV file will be named as you want to (2d param)
	 * @param filenameJSON name of the JSON file with extension (.json)
	 * @param filenameCSV name of the CSV file with extension (.csv)
	 */
	public ConvertJsonToCSV(String filenameJSON, String filenameCSV){
		super();
		this.filenameJSON = filenameJSON;
		this.filenameCSV = filenameCSV;
	}

	/**
	 * compute : Override Sink, write data from JSON File to CSV tab
	 * @param arg0 null
	 * @param arg1 null
	 * @return boolean true : it works false if not
	 */
	@Override
	protected boolean compute(Object[] arg0, Queue<Object[]> arg1) {

		/*===== Variables locales =====*/
		
		StringBuilder sb = new StringBuilder(); 													//Will contains everything we want in our CSV
		JSONParser parser = new JSONParser(); 							
		String lineJson;																			//Will contains one string of the JSON file
		PrintWriter pw = null;																		//Able to write on a file
		ArrayList<JSONObject> jsonArr =new ArrayList<JSONObject>();									//Will contains every line of the JSON file
		List<String> namesValues = Arrays.asList("W L1", "W L2","W L3","VAR L1","VAR L2","VAR L3");	//List of important value to keep
		
		/*=============================*/


		if (filenameJSON.isEmpty())
		{
			return true;
		}
		try
		{
			pw = new PrintWriter(new File(filenameCSV)); 		//Will write on the new CSV file
			File file = new File(this.filenameJSON);			//Get the JSON file
			
			/*===== Add Column Name =====*/
			
			sb.append("TIME");
			sb.append(",");
			sb.append("WL1");
			sb.append(",");
			sb.append("WL2");
			sb.append(",");
			sb.append("WL3");
			sb.append(",");
			sb.append("VARL1");
			sb.append(",");
			sb.append("VARL2");
			sb.append(",");
			sb.append("VARL3");
			sb.append("\n");
			
			/*======================*/
			
			/*===== Get the data =====*/
			
			FileReader fr = new FileReader(file);							//Open and read JSON file
			BufferedReader br = new BufferedReader(fr);						//Get the data from the JSON file
			
			while((lineJson = br.readLine()) != null) {		
				
				ArrayList arrLine = ((ArrayList) parser.parse(lineJson));	//String to Array
				
				for(int i = 0; i < arrLine.size(); i++) {
					
					JSONObject jsonObj = (JSONObject) arrLine.get(i);		//Array to Json
					jsonArr.add(jsonObj);									//Add one by one Json line to the Array
					
				}

			}

			br.close(); 
			
			/*=======================*/

			/*===== Add Data =====*/
			
			for(JSONObject o : jsonArr) {							//Get one line
				
				JSONObject jsonObject= (JSONObject )o;				//Transform into one JSON line
				
				/*===== Convert time to second =====*/

				String time = jsonObject.get("TimeStamp").toString(); 					//get the value 
				time = time.substring(12, time.length() - 1);							//get only hours:min:secons
				
				DateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);	//convert to date
				Date date = format.parse(time);
				long secs = (date.getTime())/1000; 										//convert hours to seconds
				
				/*==================================*/
				
				if(namesValues.contains(jsonObject.get("Name")) ) {
					sb.append(secs);
					sb.append(",");
					for(int i = 0; i < 6 ; i++ ){	//Get the Data we want
						
						Object value = jsonObject.get("Value");
						
						sb.append(value);
						if(i < 5) {
							sb.append(",");
						}
						
						
					} 
					
					
					sb.append("\n");
				
				}
				

			}
			pw.write(sb.toString());
			pw.close();
			System.out.println("done");
			
			/*====================*/

		} catch(IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public Processor duplicate(boolean arg0) {
		return new ConvertJsonToCSV(filenameJSON);
	}

}
