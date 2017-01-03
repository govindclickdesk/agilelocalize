
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.simple.JSONObject;

public class Csv2Json {

	public static Map<String, String> readCSV(String csvPath, String language) {

		Map<String, String> keyMap = new LinkedHashMap<String, String>();
		JSONObject valueNullMap = new JSONObject();
		ArrayList<String> duplicateKeys = new ArrayList<String>();

		// Input file which needs to be parsed
		BufferedReader fileReader = null;

		// Delimiter used in CSV file
		final String DELIMITER = ",";
		int i = 0, targetIndex = 1, unknownValues = 0;
		try {
			String line = "";
			// Create the file reader
			fileReader = new BufferedReader(new FileReader(csvPath));

			// Read the file line by line
			while ((line = fileReader.readLine()) != null) {
				try {
					// Get all tokens available in line
					String[] tokens = line.split(DELIMITER);
					if (i++ == 0) {
						for (int j = 0; j < tokens.length; j++) {
							if (StringUtils.equalsIgnoreCase(tokens[j], language))
								targetIndex = j;
						}
					}

					// Set to Default language(en)
					String val = tokens[1];
					unknownValues++;
					if (tokens.length > targetIndex) {
						val = tokens[targetIndex];
						unknownValues--;
					}else {
						valueNullMap.put(ParserUtil.decodeDelimeter(tokens[0]), ParserUtil.decodeDelimeter(val));
					}
					
					if(keyMap.containsKey(ParserUtil.decodeDelimeter(tokens[0])))
						  duplicateKeys.add(ParserUtil.decodeDelimeter(tokens[0]));
					
					keyMap.put(ParserUtil.decodeDelimeter(tokens[0]), ParserUtil.decodeDelimeter(val));
				} catch (Exception e) {
					if(e instanceof ArrayIndexOutOfBoundsException)
						  System.out.println("Error in key line " + i + " : " + line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Debug
		// System.out.println("unknownValues = " + unknownValues + " : " + valueNullMap.size());
		// System.out.println(valueNullMap); 
		int index = 0;
		
		// Fill values with target values
		for (Iterator iterator = valueNullMap.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			String value = (String) valueNullMap.get(key);
			
			try {
				// System.out.println(index++);
				// valueNullMap.put(key, readURL(value));
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		// System.out.println(valueNullMap);
		// System.out.println(duplicateKeys);
		// System.out.println("Read Map length = " + keyMap.size());
		// End of Debug
		
		// return sortMap(keyMap);
		return keyMap;
	}

	/*static Map<String, String> sortMap(Map<String, String> map) {
		TreeSet set = (TreeSet) ((TreeSet) entriesSortedByValues(map)).descendingSet();
		Map<String, String> navigableMap = new LinkedHashMap<String, String>();
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
			navigableMap.put(entry.getKey(), entry.getValue());
		}

		return navigableMap;

	}

	static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
				int res = (e1.getValue().toString().length() - e2.getValue().toString().length());
				return res != 0 ? res : 1;
			}
		});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}*/
	
	// Google translate Code
	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
		String csvPath = "/Users/govindchunchula/Documents/agileworkspace/agilelocalize/i18n-final.csv";
		Csv2Json.readCSV(csvPath, "es");
	}
	// Google API Call
	public static String readURL(String value) throws UnsupportedEncodingException, IOException{
		URL url = new URL("https://www.googleapis.com/language/translate/v2?key=AIzaSyCFJ_sheJycHVRpQ62E6ZhedYzGswYJ0Kk&q=" +URLEncoder.encode(value, "UTF-8")+ "&source=en&target=es");
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));

		String readLine, readURlString = "";

		while ((readLine = in.readLine()) != null) {
			readURlString += readLine;
		}

		try {
			System.out.println(readURlString);
			org.json.JSONObject respJSON = new org.json.JSONObject(readURlString);
			return respJSON.getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("translatedText");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
}
