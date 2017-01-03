import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Builder {

	public final static String finalPath = "/Users/govindchunchula/Documents/agileworkspace/agilelocalize/i18n-final.csv";
	public final static String finalPath1 = "/Users/govindchunchula/Documents/agileworkspace/agilelocalize/i18n-final1.csv";

	public static void main(String[] args) throws Exception {

		String targetLanguage = args[1];

		// Read source map
		String sourceCSVPath = args[0];
		Map<String, String> sourceMap = Csv2Json.readCSV(sourceCSVPath, targetLanguage);
		Map<String, String> sourceEnMap = Csv2Json.readCSV(sourceCSVPath, "en");

		// Read target map
		Map<String, Map<String, String>> finalMap = new LinkedHashMap<String, Map<String, String>>();

		// Input file which needs to be parsed
		String line = "";
		BufferedReader fileReader = new BufferedReader(new FileReader(finalPath));

		int index = 0;
		// Read the file line by line
		while ((line = fileReader.readLine()) != null) {

			// Get all tokens available in line
			String[] tokens = line.split(",");
			for (int j = 1; j < tokens.length; j++) {
				String tempLng = tokens[j];
				finalMap.put(tempLng, Csv2Json.readCSV(finalPath, tempLng));
			}
			if (++index > 0)
				break;
		}

		fileReader.close();

		// Update the english keys with source file
		Map<String, String> enMap = finalMap.get("en");
		for (Iterator iterator = sourceEnMap.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			if (!enMap.containsKey(key))
				enMap.put(key, sourceEnMap.get(key));
		}
		finalMap.put("en", enMap);

		// Update the map with the given source files
		finalMap = updateFinalMap(finalMap, sourceMap, targetLanguage);

		// Create a final file with the updated map
		createAFinalFile(finalMap);
	}

	private static Map<String, Map<String, String>> updateFinalMap(Map<String, Map<String, String>> finalMap,
			Map<String, String> sourceMap, String targetLanguage) {

		// Iterate english keys in final file and upsert the values
		if (!finalMap.containsKey(targetLanguage)) {
			finalMap.put(targetLanguage, new LinkedHashMap<String, String>());
		}

		Map<String, String> targetMap = finalMap.get(targetLanguage);

		// Get default language Map and compare
		Map<String, String> comparableMap = finalMap.get("en");

		for (Iterator iterator = comparableMap.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			if (!targetMap.containsKey(key))
				targetMap.put(key, sourceMap.get(key));
		}

		// Update final map
		finalMap.put(targetLanguage, targetMap);

		return finalMap;
	}

	private static void createAFinalFile(Map<String, Map<String, String>> finalMap) throws Exception {

		System.out.println("Start");
		
		// Open file in append mode
		FileWriter pw = new FileWriter(new File(finalPath1));
		Set<String> keySet = finalMap.keySet();

		StringBuilder sb = new StringBuilder();
		sb.append("Key");

		for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
			sb.append(',');
			sb.append((String) iterator.next());
		}
		sb.append('\n');

		Set<String> enKeySet = finalMap.get("en").keySet();
		for (Iterator iterator = enKeySet.iterator(); iterator.hasNext();) {
			String enKey = (String) iterator.next();
			sb.append(enKey);

			try {
				for (Iterator iteratorInner = finalMap.keySet().iterator(); iterator.hasNext();) {
					sb.append(',');
					String lngKey = (String) iteratorInner.next();

					String lngVal = "";

					try {
						if (finalMap.get(lngKey).containsKey(enKey))
							lngVal = finalMap.get(lngKey).get(enKey);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						System.out.println("Error1 key = " + lngKey);
					}

					sb.append(lngVal);
				}
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("Error2 key = " + enKey);
			}

			sb.append('\n');
		}

		pw.write(sb.toString());
		pw.close();
		
		System.out.println("End");
	}
}
