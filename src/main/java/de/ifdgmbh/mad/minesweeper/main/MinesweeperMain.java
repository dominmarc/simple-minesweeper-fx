/*
 * Copyright (c) 2021 iFD GmbH Chemnitz http://www.ifd-gmbh.com
 */
package de.ifdgmbh.mad.minesweeper.main;

import de.ifdgmbh.mad.minesweeper.util.ImageUtils;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 *
 * Main class for our minesweeper project
 *
 * @author MAD
 * @author iFD
 */
public class MinesweeperMain extends Application {

	private Parent root;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void init() throws Exception {

		//do some preflight checks

		final var resourceName = "minesweeper.fxml";
		final var resource = getClass().getResource(resourceName);
		if (resource == null){
			throw new IllegalStateException("Unable to find "+resourceName+"!");
		}
		try {
			root = FXMLLoader.load(resource);
		} catch (IOException e) {
			throw new IllegalStateException("Unable to construct main view!", e);
		}
	}

	/**
	 * Loads the in fxml file defined scene and applies it to the stage that is about to open
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		final var styleURL = getClass().getResource("style.css");
		if (styleURL == null){
			throw new IllegalStateException("Unable to find style");
		}
		final var iconImage = ImageUtils.getIconImage();


		//setting the scene based on a fxml file

		Scene myScene = new Scene(root);
		//set transparent background
		myScene.setFill(Color.TRANSPARENT);

		//x value of the upper left scene corner
		DoubleProperty xOffset = new SimpleDoubleProperty(0);
		//y value of the upper left scene corner
		DoubleProperty yOffset = new SimpleDoubleProperty(0);

		//save x and y coordinates of scene
		root.setOnMousePressed(event -> {
			xOffset.set(event.getSceneX());
			yOffset.set(event.getSceneY());
		});

		//move the stage, if the user drags the topBar-Label (height 25) of the scene
		root.setOnMouseDragged(event -> {
			if (yOffset.lessThan(25).get()) {
				primaryStage.setX(event.getScreenX() - xOffset.get());
				primaryStage.setY(event.getScreenY() - yOffset.get());
			}
		});

		//show the stage, apply transparent style, icon and a style-sheet
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setScene(myScene);
		primaryStage.getScene().getStylesheets().add(styleURL.toString());
		primaryStage.getIcons().add(iconImage);
		primaryStage.setResizable(false);
		primaryStage.show();
	}
}
