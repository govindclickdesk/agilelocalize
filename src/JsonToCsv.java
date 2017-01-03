
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JsonToCsv {

	public static void addMapToCSV(String csvPath, LinkedHashMap<String, String> newMap) {

		try {
			if(newMap.size() == 0)
				  return;
			
			// Open file in append mode
			FileWriter pw = new FileWriter(new File(csvPath), true);
			pw.write("\n");
			
			StringBuilder sb = new StringBuilder();
			
			for (Iterator iterator = newMap.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				String val = (String) newMap.get(key);
				
				String encodedValueKey = ParserUtil.encodeDelimeter(key);
				String encodedVal = ParserUtil.encodeDelimeter(val);

				sb.append(encodedValueKey);
				sb.append(',');
				sb.append(encodedVal);

				sb.append('\n');
			}
			
			pw.write(sb.toString());
			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void createLanguageCSVFromJSON() {

		try {

			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(new FileReader(
					"/Users/govindchunchula/Documents/agile-git/agile-java-server/agile-frontend/WebContent/locales/json/resources_es-4.json"));

			obj = (JSONObject) obj.get("es");

			PrintWriter pw = new PrintWriter(new File("test_es" + Calendar.getInstance().getTimeInMillis() + ".csv"));
			StringBuilder sb = new StringBuilder();
			sb.append("Key");
			sb.append(',');
			sb.append("Value");
			sb.append('\n');
			int i = 0;

			Iterator iterator = obj.keySet().iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				System.out.println(key);

				JSONObject valueJSON = (JSONObject) obj.get(key);
				Iterator valueIterator = valueJSON.keySet().iterator();
				while (valueIterator.hasNext()) {
					String valueKey = (String) valueIterator.next();

					String encodedValueKey = ParserUtil.encodeDelimeter(valueKey);
					String encodedVal = ParserUtil.encodeDelimeter(valueJSON.get(valueKey).toString());

					sb.append(key + "." + encodedValueKey);
					sb.append(',');
					sb.append(encodedVal);

					sb.append('\n');
				}
			}

			pw.write(sb.toString());
			pw.close();
			System.out.println("done!" + i);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void compareCSV() throws FileNotFoundException {
		String csvPath1 = "/Users/govindchunchula/Documents/agileworkspace/agilelocalize/en.csv";
		String csvPath2 = "/Users/govindchunchula/Documents/agileworkspace/agilelocalize/es.csv";

		/*Map<String, String> enMap = Csv2Json.readCSV(csvPath1);
		Map<String, String> esMap = Csv2Json.readCSV(csvPath2);

		System.out.println(enMap.size() - esMap.size());

		PrintWriter pw = new PrintWriter(new File("en_es" + Calendar.getInstance().getTimeInMillis() + ".csv"));
		StringBuilder sb = new StringBuilder();
		sb.append("Key");
		sb.append(',');
		sb.append("en");
		sb.append(',');
		sb.append("es");
		sb.append('\n');

		for (Iterator iterator = enMap.keySet().iterator(); iterator.hasNext();) {
			String enKey = (String) iterator.next();
			String enVal = enMap.get(enKey);
			String encodedEnKey = ParserUtil.encodeDelimeter(enKey);
			enVal = ParserUtil.encodeDelimeter(enVal);
			
			String esVal = "";
			if (esMap.containsKey(enKey))
				esVal = esMap.get(enKey);
			if(StringUtils.isNotBlank(esVal))
				esVal = ParserUtil.encodeDelimeter(esVal);
			
			sb.append(encodedEnKey);
			sb.append(',');
			sb.append(enVal);
			sb.append(',');
			sb.append(esVal);

			sb.append('\n');

		}

		pw.write(sb.toString());
		pw.close();
		System.out.println("done!"); */
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, JSONException {
		String csvPath = "/Users/govindchunchula/Documents/agileworkspace/agilelocalize/i18n-final.csv";
		Map<String, String> esMap = Csv2Json.readCSV(csvPath, "es");
		Map<String, String> enMap = Csv2Json.readCSV(csvPath, "en");
		
		String json = IOUtils.toString(new FileInputStream("/Users/govindchunchula/Documents/agileworkspace/agilelocalize/keys.json"), "UTF-8");
		org.json.JSONObject convertedJSON = new org.json.JSONObject(json);
		System.out.println(convertedJSON.length());
		
		
		PrintWriter pw = new PrintWriter(new File("en_es" + Calendar.getInstance().getTimeInMillis() + ".csv"));
				StringBuilder sb = new StringBuilder();
				sb.append("Key");
				sb.append(',');
				sb.append("en");
				sb.append(',');
				sb.append("es");
				sb.append('\n');
				int converted = 0;
				
				for (Iterator iterator = enMap.keySet().iterator(); iterator.hasNext();) {
					String enKey = (String) iterator.next();
					String enVal = enMap.get(enKey);
					String encodedEnKey = ParserUtil.encodeDelimeter(enKey);
					enVal = ParserUtil.encodeDelimeter(enVal);
					
					String esVal = "";
					if(convertedJSON.has(enKey)){
						esVal =  convertedJSON.getString(enKey);
						converted++;
					}
					else if (esMap.containsKey(enKey))
						esVal = esMap.get(enKey);
					if(StringUtils.isNotBlank(esVal))
						esVal = ParserUtil.encodeDelimeter(esVal);
					
					sb.append(encodedEnKey);
					sb.append(',');
					sb.append(enVal);
					sb.append(',');
					sb.append(esVal);

					sb.append('\n');

				}

				pw.write(sb.toString());
				pw.close();
				System.out.println("done!");
	}
	
	
	
}
