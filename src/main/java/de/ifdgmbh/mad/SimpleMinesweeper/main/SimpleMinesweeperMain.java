/* 
 * Copyright (c) 2021 iFD GmbH Chemnitz http://www.ifd-gmbh.com
 */
package de.ifdgmbh.mad.SimpleMinesweeper.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 
 * Main class for our minesweeper project
 *
 * @author MAD
 * @author iFD
 */
public class SimpleMinesweeperMain extends Application {
	/** x value of the upper left scene corner */
	private double xOffset = 0;
	/** y value of the upper left scene corner */
	private double yOffset = 0;

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Loads the in fxml file defined scene and applies it to the stage that is about to open 
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		//setting the scene based on a fxml file
		Parent root = FXMLLoader.load(getClass().getResource("Minesweeper.fxml"));
		Scene myScene = new Scene(root);
		//set transparent background
		myScene.setFill(Color.TRANSPARENT);

		//save x and y coordinates of scene
		root.setOnMousePressed(event -> {
			xOffset = event.getSceneX();
			yOffset = event.getSceneY();
		});

		//move the stage, if the user drags the topBar-Label (height 25) of the scene
		root.setOnMouseDragged(event -> {
			if (yOffset < 25) {
				primaryStage.setX(event.getScreenX() - xOffset);
				primaryStage.setY(event.getScreenY() - yOffset);
			}
		});

		//show the stage, apply transparent style, icon and a style-sheet 
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setScene(myScene);
		primaryStage.getScene().getStylesheets().add(getClass().getResource("StyleFile.css").toString());
		primaryStage.getIcons().add(
				new Image(getClass().getResource("/de/ifdgmbh/mad/SimpleMinesweeper/images/bombred.png").toString()));
		primaryStage.setResizable(false);
		primaryStage.show();
	}
}
