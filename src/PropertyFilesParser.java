import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class PropertyFilesParser {

	/**
	 * Holds all property files keys and en-values
	 */
	public HashMap<String, String> propertiesMap = new HashMap<String, String>();

	/**
	 * Builds Key map with the folder files
	 * 
	 * @param folder
	 */
	public void buildKeyMapFromPropertyFiles(final File folder) {

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				buildKeyMapFromPropertyFiles(fileEntry);
			} else {
				try {
					readFileAndAddKeysToMap(fileEntry);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * Retunrs generated en Key/Map with prop filies
	 * 
	 * @param fileEntry
	 * @throws IOException
	 */
	private void readFileAndAddKeysToMap(File fileEntry) throws IOException {
		String fileName = fileEntry.getName();
		System.out.println("fileName = " + fileName);

		// Input file which needs to be parsed
		BufferedReader fileReader = null;
		String DELIMITER = "=";
		try {
			String line = "";
			// Create the file reader
			fileReader = new BufferedReader(new FileReader(fileEntry));

			// Read the file line by line
			while ((line = fileReader.readLine()) != null) {
				if(StringUtils.isBlank(line))
					continue;
				
				if (line.endsWith("="))
					line += " ";

				// Get all tokens available in line
				String[] tokens = line.split(DELIMITER);
				String key = tokens[0].trim(), val = "";

				// Value
				for (int j = 1; j < tokens.length; j++) {
					val += tokens[j] + "=";
				}
				val = val.trim();
				val = (val.endsWith("=")) ? val.substring(0, val.lastIndexOf("=")) : val;

				// System.out.println(key + " : " + val);
				String filenameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));

				// Add to map like (filename.key = val) type
				propertiesMap.put(filenameWithoutExt + "." + key, val);

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

	}

	/**
	 * Rewrite process before templatize with supported languages
	 * 
	 * @param csvFinalPath
	 */
	public void preProcessBeforeLocalize(String csvFinalPath) {
		// Read all property files
		String propertyFilesFolder = Agilei18Parser.csvLocalPath + "/keys";
		new File(propertyFilesFolder).mkdir();

		buildKeyMapFromPropertyFiles(new File(propertyFilesFolder));
		System.out.println("propertiesMap = " + propertiesMap.size());

		// Read existing csv
		String csvPath = Agilei18Parser.csvLocalPath + "/i18n.csv";
		Map<String, String> map = Csv2Json.readCSV(csvPath, "en");
		for (Iterator iterator = propertiesMap.keySet().iterator(); iterator.hasNext();) {
			String type = (String) iterator.next();
			if (!map.containsKey(type))
				map.put(type, propertiesMap.get(type));
		}

		// Get original file content
		File originalFile = new File(csvFinalPath);
		if (!originalFile.exists()) {
			try {
				FileUtils.copyFile(new File(csvPath), originalFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Map<String, String> csvFinalmap = Csv2Json.readCSV(csvFinalPath, "en");

		// Filter new ones
		LinkedHashMap<String, String> newMap = new LinkedHashMap<String, String>();
		for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
			String type = (String) iterator.next();
			if (!csvFinalmap.containsKey(type))
				newMap.put(type, map.get(type));
		}

		// Add new keys to final one
		JsonToCsv.addMapToCSV(csvFinalPath, newMap);
	}

	/**
	 * Get new instance
	 * 
	 * @return
	 */
	public static PropertyFilesParser getInstance() {
		return new PropertyFilesParser();
	}
}
