package de.ifdgmbh.mad.minesweeper.main;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import de.ifdgmbh.mad.minesweeper.logger.MinesweeperLogger;

/**
 * Class for loading all the necessary game files, such as fxml documents or css
 * stylesheets.</br>
 * Please make use of the class' getters in order to use the files.
 * 
 * @author MAD
 */
public final class FileProvider {
	/** game window */
	private static Path localGame;
	/** Style file for game */
	private static Path localGameStyle;
	/** start window */
	private static Path startForm;
	/** Style file for start window */
	private static Path startStyle;

	/** represents the status of the file provider */
	private static boolean loaded;

	static final MinesweeperLogger LOGGER = MinesweeperLogger.getLogger(FileProvider.class);

	/**
	 * Constructor</br>
	 * Loads all the necessary files.
	 */
	private FileProvider() {
	}

	/**
	 * Indicates weather all files are loaded or not.
	 * 
	 * @return true, if files are loaded and false, if not
	 */
	public static boolean isLoaded() {
		return loaded;
	}

	/**
	 * Tries to load all the given files.</br>
	 * (Checks for their existence.)
	 */
	public static void loadFiles() throws FileLoadingException, URISyntaxException {
		LOGGER.info("Loading game files...");

		// reference all files here:
		localGame = Paths
				.get(FileProvider.class.getResource("/de/ifdgmbh/mad/minesweeper/main/minesweeper.fxml").toURI());
		localGameStyle = Paths
				.get(FileProvider.class.getResource("/de/ifdgmbh/mad/SimpleDraughts/main/style.css").toURI());

		startForm = Paths.get(FileProvider.class.getResource("/de/ifdgmbh/mad/SimpleDraughts/main/start.fxml").toURI());
		startStyle = Paths
				.get(FileProvider.class.getResource("/de/ifdgmbh/mad/SimpleDraughts/main/startStyle.css").toURI());

		// add all files here:
		ArrayList<Path> files = new ArrayList<>();
		files.add(localGame);
		files.add(localGameStyle);
		files.add(startForm);
		files.add(startStyle);

		for (Path p : files)
			if (!load(p)) {
				loaded = false;
				throw new FileLoadingException(p.getFileName().toString());
			}

		LOGGER.info("Successfully loaded game files!");
		loaded = true;
	}

	/**
	 * Method to load the files.</br>
	 * 
	 * @return
	 */
	private static boolean load(Path path) {
		return Files.exists(path);
	}

	public static Path getGameFile() {
		return localGame;
	}

	public static Path getGameStyleFile() {
		return localGameStyle;
	}

	public static Path getStartFile() {
		return startForm;
	}

	public static Path getStartStyle() {
		return startStyle;
	}

	public static URL getGameFileURL() {
		try {
			return localGame.toUri().toURL();
		} catch (MalformedURLException e) {
			LOGGER.error("", e);
			return null;
		}
	}

	public static URL getGameStyleFileURL() {
		try {
			return localGameStyle.toUri().toURL();
		} catch (MalformedURLException e) {
			LOGGER.error("", e);
			return null;
		}
	}

	public static URL getStartFileURL() {
		try {
			return startForm.toUri().toURL();
		} catch (MalformedURLException e) {
			LOGGER.error("", e);
			return null;
		}
	}

	public static URL getStartStyleURL() {
		try {
			return startStyle.toUri().toURL();
		} catch (MalformedURLException e) {
			LOGGER.error("", e);
			return null;
		}
	}
}
