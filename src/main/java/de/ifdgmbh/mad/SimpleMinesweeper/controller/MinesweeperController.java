/* 
 * Copyright (c) 2021 iFD GmbH Chemnitz http://www.ifd-gmbh.com
 */
package de.ifdgmbh.mad.SimpleMinesweeper.controller;

import java.security.SecureRandom;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for minesweeper
 * 
 * @author MAD
 * @author iFD
 */
public class MinesweeperController {
	/** Main container for all other elements */
	@FXML
	private AnchorPane backgroundPane;
	/** Container for all game field buttons */
	@FXML
	private AnchorPane buttonPane;
	/** Array of buttons used within the buttonPane */
	@FXML
	private Button[] buttons;
	/** Button to minimize the stage */
	@FXML
	private Button minButton;
	/** Button to close the whole application */
	@FXML
	private Button closeButton;
	/** Button to start or reset the game */
	@FXML
	private Button resetButton;
	/** Label presenting the elapsed time after the game started */
	@FXML
	private Label timeLabel;
	/** Label showing the bombs left */
	@FXML
	private Label bombsLabel;
	/**
	 * Label representing the topBar where the user has the ability to drag the
	 * window
	 */
	@FXML
	private Label topBar;

	/**
	 * int[11][11] array; zeroes and tens not used (they frame the gamefield array
	 * with -1)</br>
	 * no bomb = 0</br>
	 * x bombs = x </br>
	 * bomb = 9
	 */
	private int[][] game_field;

	/** Boolean representing whether the game is running or not */
	private boolean running = false;
	/** Value representing the number of bombs left */
	private int bombs = 10;
	/** Background to visually show a bomb */
	private Background bomb;
	/** Background to visually show a user marked field */
	private Background checked;
	/** Background to visually show a raw field */
	private Background unchecked;
	/** Value representing the number of fields/ buttons */
	private final int SIZE = 82;

	private Timer timer;

	public void initialize() {
		topBar.setStyle("-fx-background-color: #272727");
		// set all Buttons
		buttons = new Button[SIZE];
		/** x position of the button */
		int pos_x = 0;
		/** y position of the button */
		int pos_y = 0;
		/** Button index */
		int index = 1;
		for (int i = 1; i < 10; i++) {
			for (int t = 1; t < 10; t++) {
				buttons[index] = new Button();
				buttonPane.getChildren().add(buttons[index]);
				buttons[index].setLayoutX(pos_x);
				buttons[index].setLayoutY(pos_y);
				buttons[index].setPrefWidth(30);
				buttons[index].setPrefHeight(30);
				buttons[index].setPadding(new Insets(0));
				buttons[index].setStyle("-fx-font-size: 17px; -fx-font-weight: bold;");
				unchecked = new Background(new BackgroundFill(Color.web("#DEDEDE"), null, buttons[1].getInsets()));
				buttons[index].setBackground(checked);
				final int btnIndex = index;
				/** x position of current button in game_field array */
				final int btnFieldPos_x = t;
				/** y position of current button in game_field array */
				final int btnFieldPos_y = i;
				buttons[index].setOnMouseClicked(event -> {
					if (!running) {
						popUp(false, "Please start the game!");
						return;
					}
					if (event.getButton() == MouseButton.PRIMARY) {
						primaryButtonAction(btnIndex, btnFieldPos_x, btnFieldPos_y);
					} else if (event.getButton() == MouseButton.SECONDARY) {
						secondaryButtonAction(btnIndex);
					}
					bombsLabel.setText("Bombs: " + bombs);
					if (running)
						if (checkEnd()) {
							return;
						}
				});
				index++;
				pos_x += 30;
			}
			pos_x = 0;
			pos_y += 30;
		}
		bomb = new Background(new BackgroundFill(Color.web("#FA7D7D"), null, buttons[1].getInsets()));
		checked = new Background(new BackgroundFill(Color.web("#FFFFFF"), null, buttons[1].getInsets()));

		// draw game field lines
		for (int l = 0; l <= 270; l += 30) {
			Line line = new Line(0, 0, 0, 270);
			line.setLayoutX(l);
			buttonPane.getChildren().add(line);
			Line line2 = new Line(0, 0, 270, 0);
			buttonPane.getChildren().add(line2);
			line2.setLayoutY(l);
		}

	}

	/**
	 * Checks whether the game ended with the last choice or not
	 */
	public boolean checkEnd() {
		for (int i = 1; i < SIZE; i++) {
			if (buttons[i].getBackground() == unchecked) {
				return false;
			}
		}
		timer.cancel();
		running = false;
		popUp(true, "");
		return true;
	}

	/**
	 * Opens a new (pop-up) stage, which is used to preset information to the user
	 * 
	 * @param win  depending on the type of message --> winning statement or not
	 * @param info represents the message you want to show to the user, this is just
	 *             relevant if win = false
	 */
	public void popUp(boolean win, String info) {
		// create and configure a new stage
		Stage popUp = new Stage();
		popUp.initModality(Modality.APPLICATION_MODAL);
		popUp.setMinHeight(200);
		popUp.setMinWidth(300);
		popUp.getIcons().add(
				new Image(getClass().getResource("/de/ifdgmbh/mad/SimpleMinesweeper/images/bombred.png").toString()));

		// configuring a label to show some text
		Label label = new Label();
		label.setFont(new Font("Berlin Sans FB", 20));
		label.setTextAlignment(TextAlignment.CENTER);
		label.setStyle("-fx-text-fill: linear-gradient(to top, #ffcc00, #fbff02);");

		// create container to hold our elements and edit elements according to the
		// settings passed to the function
		VBox vBox = new VBox();
		if (win) {
			popUp.setTitle("Game Over!");
			label.setText("You won!\nThank you for playing, have a nice day!");
			vBox.getChildren().add(label);
		} else {
			vBox.getChildren().add(label);
			popUp.setTitle("Attention");
			label.setText("" + info);
		}
		vBox.setStyle(
				"-fx-background-color: radial-gradient(center 50.0% 50.0%, radius 100.0%, #242424, #434343, #898989);");
		vBox.setAlignment(Pos.CENTER);

		// apply our nodes (container) to the scene and the scene to our stage
		Scene scene = new Scene(vBox);
		popUp.setScene(scene);

		// show the stage and wait until the user showed his reaction
		popUp.showAndWait();
	}

	/**
	 * set game_field with 10 bombs and all values
	 */
	public void createField() {
		game_field = new int[11][11];
		// create 10 bombs
		SecureRandom rand = new SecureRandom();
		bombs = 10;
		for (int k = 1; k <= bombs; k++) {
			int var1;
			int var2;
			do {
				var1 = rand.nextInt(9) + 1;
				var2 = rand.nextInt(9) + 1;
			} while (game_field[var1][var2] == 9);
			game_field[var1][var2] = 9;
		}

		int bombCounter = 0;

		// loop through all fields
		for (int y = 1; y < 10; y++) {
			for (int x = 1; x < 10; x++) {
				// if field is a bomb (9) we can skip the field
				if (game_field[x][y] != 9) {
					// loop through [][] around the current field (around x and around y)
					for (int around_y = y - 1; around_y <= y + 1; around_y++) {
						for (int around_x = x - 1; around_x <= x + 1; around_x++) {
							// check if there is a bomb
							if (game_field[around_x][around_y] == 9) {
								bombCounter++;
							}
						}
					}
					// value of the field represents the number of bombs around the field
					game_field[x][y] = bombCounter;
				}
				bombCounter = 0;
			}
		}

		// print field && initialize the frame with -1 (0, 10)
		for (int g = 0; g < 11; g++) {
			for (int h = 0; h < 11; h++) {
				if (g == 0 || g == 10) {
					game_field[h][g] = -1;
				}
				if (h == 0 || h == 10) {
					game_field[h][g] = -1;
				}
			}
		}
	}

	/**
	 * Function to open up all the neighbor zero fields </br>
	 * This function is called whenever the player hits a zero field </br>
	 * 
	 * @param btnIndex representing the (array-) index of the given button
	 * @param x        representing the x value of the button in game_field array
	 * @param y        representing the y value of the button in game_field array
	 */
	public void openFields(int btnIndex, int x, int y) {
		// up
		if (game_field[x][y - 1] == 0) {
			game_field[x][y - 1] = -1;
			buttons[btnIndex - 9].setBackground(checked);
			openFields(btnIndex - 9, x, y - 1);
		}
		// right up
		if (game_field[x + 1][y - 1] == 0) {
			game_field[x + 1][y - 1] = -1;
			buttons[btnIndex - 9 + 1].setBackground(checked);
			openFields(btnIndex - 9 + 1, x + 1, y - 1);
		}
		// right
		if (game_field[x + 1][y] == 0) {
			game_field[x + 1][y] = -1;
			buttons[btnIndex + 1].setBackground(checked);
			openFields(btnIndex + 1, x + 1, y);
		}
		// right down
		if (game_field[x + 1][y + 1] == 0) {
			game_field[x + 1][y + 1] = -1;
			buttons[btnIndex + 9 + 1].setBackground(checked);
			openFields(btnIndex + 9 + 1, x + 1, y + 1);
		}
		// down
		if (game_field[x][y + 1] == 0) {
			game_field[x][y + 1] = -1;
			buttons[btnIndex + 9].setBackground(checked);
			openFields(btnIndex + 9, x, y + 1);
		}
		// left down
		if (game_field[x - 1][y + 1] == 0) {
			game_field[x - 1][y + 1] = -1;
			buttons[btnIndex + 9 - 1].setBackground(checked);
			openFields(btnIndex + 9 - 1, x - 1, y + 1);
		}
		// left
		if (game_field[x - 1][y] == 0) {
			game_field[x - 1][y] = -1;
			buttons[btnIndex - 1].setBackground(checked);
			openFields(btnIndex - 1, x - 1, y);
		}
		// left up
		if (game_field[x - 1][y - 1] == 0) {
			game_field[x - 1][y - 1] = -1;
			buttons[btnIndex - 9 - 1].setBackground(checked);
			openFields(btnIndex - 9 - 1, x - 1, y - 1);
		}

	}

	/**
	 * Shows the solution of the game, opens all fields</br>
	 * This function is called after the player hit a bomb! </br>
	 */
	public void showSolution() {
		int btnIndex = 1;
		for (int y = 1; y < 10; y++) {
			for (int x = 1; x < 10; x++) {
				// Value of the field, representing how many bombs are around the field or if it
				// is a bomb
				int fieldValue = game_field[x][y];

				// field contains a value, is nearby bomb
				if (fieldValue != -1 && fieldValue != 0 && fieldValue != 9) {
					// arrange fitting (value-related) color and value
					if (fieldValue == 1) {
						buttons[btnIndex].setTextFill(Color.rgb(56, 0, 254));
					} else if (fieldValue == 2) {
						buttons[btnIndex].setTextFill(Color.rgb(0, 107, 4));
					} else {
						buttons[btnIndex].setTextFill(Color.rgb(142, 11, 0));
					}
					buttons[btnIndex].setText(fieldValue + "");
					buttons[btnIndex].setBackground(checked);

					// field is a bomb
				} else if (fieldValue == 9) {
					buttons[btnIndex].setBackground(bomb);
					buttons[btnIndex].setGraphic(new ImageView(new Image(getClass()
							.getResource("/de/ifdgmbh/mad/SimpleMinesweeper/images/bombred.png").toString())));

					// field is a zero, has no bomb nearby
				} else {
					buttons[btnIndex].setBackground(checked);
				}
				btnIndex++;
			}
		}
	}

	/**
	 * Opens the field if not already open and shows the user the result of his
	 * action </br>
	 * Left clicked button with mouse </br>
	 * 
	 * @param btnIndex representing the (array-) index of the given button
	 * @param x        representing the x value of the button in game_field array
	 * @param y        representing the y value of the button in game_field array
	 */
	public void primaryButtonAction(int btnIndex, int x, int y) {
		Button currentBtn = buttons[btnIndex];
		// hit the user a bomb? --> end the game
		if (game_field[x][y] == 9) {
			currentBtn.setBackground(bomb);
			timer.cancel();
			running = false;
			popUp(false, "Game Over, you hit a bomb!");
			showSolution();
			return;
		}

		// field already opened?
		if (currentBtn.getBackground() == checked) {
			return;
		}

		// no bomb nearby --> user opened a zero field
		if (game_field[x][y] == 0) {
			// open all zero fields nearby
			currentBtn.setBackground(checked);
			openFields(btnIndex, x, y);
			return;
		}

		// bomb nearby --> user opened a game relevant field
		// open the clicked field && fit the right color to the value
		if (game_field[x][y] == 1) {
			currentBtn.setTextFill(Color.rgb(56, 0, 254));
		} else if (game_field[x][y] == 2) {
			currentBtn.setTextFill(Color.rgb(0, 107, 4));
		} else {
			currentBtn.setTextFill(Color.rgb(142, 11, 0));
		}
		currentBtn.setBackground(checked);
		currentBtn.setText(game_field[x][y] + "");
	}

	/**
	 * Visually checks or unchecks a field, depending on if it was already checked
	 * or not</br>
	 * Right clicked button with mouse </br>
	 * 
	 * @param btnIndex representing the (array-) index of the given button
	 */
	public void secondaryButtonAction(int btnIndex) {
		Button btn = buttons[btnIndex];
		if (btn.getBackground() == bomb) {
			btn.setBackground(unchecked);
			btn.setGraphic(null);
			bombs++;
		} else {
			btn.setBackground(bomb);
			btn.setGraphic(new ImageView(
					new Image(getClass().getResource("/de/ifdgmbh/mad/SimpleMinesweeper/images/flag.png").toString())));
			bombs--;
		}
	}

	/**
	 * Resets or starts the game depending on the game state
	 */
	public void resetButtonClick() {
		// visually reset all the fields
		for (int i = 1; i < SIZE; i++) {
			buttons[i].setBackground(unchecked);
			buttons[i].setGraphic(null);
			buttons[i].setText("");
		}

		// reset the number of bombs
		bombs = 10;
		bombsLabel.setText("Bombs: " + bombs);

		if (running) {
			running = false;
			timer.cancel();
			resetButton.setText("START GAME");
			return;
		}

		createField();
		running = true;
		resetButton.setText("RESTART GAME");
		startTimer();
	}

	/**
	 * Starts the timer counting from 0 to whenever the game ends
	 */
	public void startTimer() {
		timer = new Timer();

		// create a new task for the timer
		TimerTask timerTask = new TimerTask() {
			final int[] counter = { 0 };

			@Override
			public void run() {
				counter[0]++;
				Platform.runLater(() -> {
					timeLabel.setText("Time: " + counter[0]);
				});
			}
		};

		// set the task to the timer and schedule it
		timer.scheduleAtFixedRate(timerTask, 1000, 1000);
	}

	/**
	 * minimize the game
	 */
	public void minButtonClicked() {
		Stage tempStage = (Stage) backgroundPane.getScene().getWindow();
		tempStage.setIconified(true);
	}

	/**
	 * close the game
	 */
	public void closeButtonClicked() {
		Stage temp = (Stage) backgroundPane.getScene().getWindow();
		temp.close();
		System.exit(0);
	}

}