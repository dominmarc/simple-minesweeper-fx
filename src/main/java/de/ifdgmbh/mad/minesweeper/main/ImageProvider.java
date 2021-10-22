/* 
 * Copyright (c) 2021 iFD  Chemnitz http://www.ifd-.com
 */
package de.ifdgmbh.mad.minesweeper.main;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import de.ifdgmbh.mad.minesweeper.logger.MinesweeperLogger;
import javafx.scene.image.Image;

/**
 * Class for loading all the necessary images.</br>
 * Please make use of the class' getters to use the images.
 * 
 * @author MAD
 */
public class ImageProvider {
	/** bomb image */
	private static Path bombRed;
	/** flag image */
	private static Path flag;
	/** help image */
	private static Path help;
	/** back image */
	private static Path back;

	/** represents the status of the image provider */
	private static boolean loaded;

	static final MinesweeperLogger LOGGER = MinesweeperLogger.getLogger(ImageProvider.class);

	/**
	 * Constructor</br>
	 * Loads all the necessary files.
	 */
	private ImageProvider() {
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
		LOGGER.info("Loading game image files...");

		// reference all files here:
		bombRed = Paths.get(ImageProvider.class.getResource("/de/ifdgmbh/mad/minesweeper/images/bombeRed.png").toURI());
		flag = Paths.get(ImageProvider.class.getResource("/de/ifdgmbh/mad/minesweeper/images/flag.png").toURI());
		help = Paths.get(ImageProvider.class.getResource("/de/ifdgmbh/mad/minesweeper/images/help.png").toURI());
		back = Paths.get(ImageProvider.class.getResource("/de/ifdgmbh/mad/minesweeper/images/back.png").toURI());

		// add all files here:
		ArrayList<Path> files = new ArrayList<>();
		files.add(bombRed);
		files.add(flag);
		files.add(help);
		files.add(back);

		for (Path p : files)
			if (!load(p)) {
				loaded = false;
				throw new FileLoadingException(p.getFileName().toString());
			}

		LOGGER.info("Successfully loaded game image files!");
		loaded = true;
	}

	/**
	 * Method to load the files</br>
	 * 
	 * @return
	 */
	private static boolean load(Path path) {
		return Files.exists(path);
	}

	public static Image getBombRedIMG() {
		try {
			return new Image(bombRed.toUri().toURL().toString());
		} catch (MalformedURLException e) {
			LOGGER.error("", e);
			return null;
		}
	}

	public static Image getFlagIMG() {
		try {
			return new Image(flag.toUri().toURL().toString());
		} catch (MalformedURLException e) {
			LOGGER.error("", e);
			return null;
		}
	}

	public static Image getHelpIMG() {
		try {
			return new Image(help.toUri().toURL().toString());
		} catch (MalformedURLException e) {
			LOGGER.error("", e);
			return null;
		}
	}

	public static Image getBackIMG() {
		try {
			return new Image(back.toUri().toURL().toString());
		} catch (MalformedURLException e) {
			LOGGER.error("", e);
			return null;
		}
	}
}
