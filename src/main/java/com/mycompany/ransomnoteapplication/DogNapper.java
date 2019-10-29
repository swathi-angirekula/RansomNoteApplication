package com.mycompany.ransomnoteapplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * This program can verify if a newspaper/magazine has all the words one need to
 * write the ransom note. \"Path to Magazine\" \"Path to RansomNote\" are the
 * two input arguments for this program to run.
 *
 * @author swathiangirekula
 */
public class DogNapper {

	private static final Logger LOGGER = Logger.getLogger(DogNapper.class.getName());

	/**
	 * Main method
	 * 
	 * @param args "Path to Magazine" and "Path to RansomNote" are the two mandatory
	 *             arguments for this program to run.
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			LOGGER.log(Level.SEVERE,
					"Usage : java -jar RansomNoteApplication-1.0.jar \"Path to Magazine\" \"Path to RansomNote\"");
			System.exit(1);
		}

		if (args.length == 3 && args[2].equalsIgnoreCase("-D")) {
			setLoggingLevelToFine();
		}
		DogNapper dogNapper = new DogNapper();

		try {
			// Load magazine and ransom note contents into the corresponding maps.
			dogNapper.loadMagazine(args[0]);
			dogNapper.loadRansomNote(args[1]);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to load files");
			System.exit(1);
		}

		// parse magazine map to check if ransom note can be created.
		Boolean value = dogNapper.parseMagazineToContainRansomNote();
		if (value) {
			LOGGER.info("YES! Ransom note can be prepared from the words in this magazine: " + args[0]);

		} else {
			LOGGER.info("NO! Ransom note cannot be prepared from the words in this magazine: " + args[0]);
		}

	}

	private final Map<String, Integer> magazineMap = new HashMap<>();
	private final Map<String, Integer> ransomNoteMap = new HashMap<>();

	/**
	 * This method initiates the process of loading magazine
	 * 
	 * @throws Exception
	 */
	private void loadMagazine(String magazinePath) throws Exception {
		LOGGER.fine("Loading Magazine : " + magazinePath);
		boolean isMagazine = true;
		parseFile(magazinePath, isMagazine);
	}

	/**
	 * This method initiates the process of loading ransom note
	 * 
	 * @throws Exception
	 */
	private void loadRansomNote(String ransomNotePath) throws Exception {
		LOGGER.fine("Loading RansomNote : " + ransomNotePath);
		boolean isMagazine = false;
		parseFile(ransomNotePath, isMagazine);
	}

	/**
	 * This method is used to parse the input files and prepare a word list to be
	 * passed onto create a word map
	 * 
	 * @param fileName
	 * @param isMagazine
	 * @throws Exception
	 */
	private void parseFile(String fileName, boolean isMagazine) throws Exception {

		File file = new File(fileName);
		if (!(file.exists() && file.isFile() && file.canRead())) {
			LOGGER.log(Level.SEVERE, "Invalid file : " + fileName);
			throw new Exception("Invalid file : " + fileName);
		}
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String line = null;
		List<String> wordsList = new ArrayList<String>();
		while ((line = bufferedReader.readLine()) != null) {
			if (!line.isEmpty()) {
				line = line.replaceAll("[\\.|,|;|:'\"*!()]", " ");
				String[] wordsArray = line.split(" ");
				wordsList.addAll(Arrays.asList(wordsArray));
			}
		}
		bufferedReader.close();
		updateMap(wordsList, isMagazine);
	}

	/**
	 * Route the request to update the right map.
	 * 
	 * @param wordsList
	 * @param isMagazine
	 */
	private void updateMap(List<String> wordsList, boolean isMagazine) {
		if (isMagazine) {
			updateMap(wordsList, magazineMap);
		} else {
			updateMap(wordsList, ransomNoteMap);
		}
	}

	/**
	 * This method is used to update the map with the words present in the
	 * wordsList. The map will contain the Keys: Words and values: occurrences of
	 * the words.
	 * 
	 * @param wordsList
	 */
	private void updateMap(List<String> wordsList, Map<String, Integer> map) {
		for (String s : wordsList) {
			if (!map.containsKey(s)) {
				map.put(s, 1);
			} else {
				map.put(s, map.get(s) + 1);
			}
		}
	}

	/**
	 * This method is used to compare the maps to identify if the magazine contains
	 * all the words needed for the ransom note.
	 * 
	 * @return
	 */
	private boolean parseMagazineToContainRansomNote() {
		if (magazineMap.size() < ransomNoteMap.size()) {
			// This means that the magazine does not contain all the unique words needed for
			// the ransom note.
			// Hence Fail immediately
			LOGGER.info("Ransom note map is larger than magazine");
			return false;
		}

		for (String ransomNoteMapKey : ransomNoteMap.keySet()) {
			Integer magazineMapValue = magazineMap.get(ransomNoteMapKey);
			if (magazineMapValue != null && magazineMapValue >= ransomNoteMap.get(ransomNoteMapKey)) {
				LOGGER.fine("Word from RansomNote found in magazine is :" + ransomNoteMapKey);
			} else {
				LOGGER.info("Word from RansomNote not found in magazine is :" + ransomNoteMapKey);
				return false;
			}
		}
		return true;
	}

	/**
	 * Sets the logging level to fine.
	 * 
	 */
	private static void setLoggingLevelToFine() {
		LOGGER.info("Setting Logging level. ");
		LOGGER.setLevel(Level.FINE);
		Arrays.stream(LogManager.getLogManager().getLogger("").getHandlers()).forEach(h -> h.setLevel(Level.ALL));
	}
}
