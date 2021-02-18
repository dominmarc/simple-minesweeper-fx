package de.ifdgmbh.mad.SimpleMinesweeper.controller;

import java.security.SecureRandom;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class MyController {
	@FXML
	AnchorPane mypane, buttonPane;
	@FXML
	Button[] buttons;
	@FXML
	Button minButton, closeButton;
	@FXML
	Button resetButton;
	@FXML
	Label timeLabel, bombsLabel;
	// no bomb --> 0
	// x bombs --> x
	// bomb --> 9
	int[][] game_field; // = new int[11][11]; zeroes and tens not used (will be the frame) -->
						// initialized with -1

	boolean running = false;
	int bombs = 10;
	Background bomb;
	Background checked;
	Background unchecked;

	Alert myAlert;

	public void initialize() {
		// set all Buttons
		buttons = new Button[82];
		int x = 0;
		int y = 0;
		int z = 1;
		for (int i = 1; i < 10; i++) {
			for (int t = 1; t < 10; t++) {
				buttons[z] = new Button();
				buttonPane.getChildren().add(buttons[z]);
				buttons[z].setLayoutX(x);
				buttons[z].setLayoutY(y);
				buttons[z].setPrefWidth(30);
				buttons[z].setPrefHeight(30);
				buttons[z].setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
				unchecked = new Background(new BackgroundFill(Color.web("#DEDEDE"), null, buttons[1].getInsets()));
				buttons[z].setBackground(checked);
				final int var = z;
				final int var_x = t;
				final int var_y = i;
				buttons[z].setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						if (!running) {
							myAlert = new Alert(Alert.AlertType.WARNING, "Please start the game!");
							myAlert.show();
							return;
						}
						if (event.getButton() == MouseButton.PRIMARY) {
							primaryButtonAction(var, var_x, var_y);
						} else if (event.getButton() == MouseButton.SECONDARY) {
							secondaryButtonAction(var);
						}
						bombsLabel.setText("Bombs: "+bombs);
						if (running)
							if (checkEnd()) {
								return;
							}
					}
				});
				z++;
				x += 30;
			}
			x = 0;
			y += 30;
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
		for (int i = 1; i < 82; i++) {
			if (buttons[i].getBackground() == unchecked) {
				return false;
			}
		}
		myAlert = new Alert(Alert.AlertType.INFORMATION, "Congratulation, you won the game!");
		running = false;
		myAlert.show();
		return true;
	}

	/**
	 * set game_field with 10 bombs and all values
	 */
	public void createField() {
		game_field = new int[11][11];
		// 10 bombs
		SecureRandom rand = new SecureRandom();
		for (int k = 1; k < 11; k++) {
			int var1;
			int var2;
			do {
				var1 = rand.nextInt(9) + 1;
				var2 = rand.nextInt(9) + 1;
			} while (game_field[var1][var2] == 9);
			game_field[var1][var2] = 9;
		}

		int bombs = 0;

		// loop through all fields
		for (int i = 1; i < 10; i++) {
			for (int t = 1; t < 10; t++) {
				// check if there is bombs around them and assign the value of the field
				// according to the number of bombs
				// loop through [][] around the current field
				if (game_field[t][i] != 9) {
					for (int yy = i - 1; yy <= i + 1; yy++) {
						for (int xx = t - 1; xx <= t + 1; xx++) {
							if (game_field[xx][yy] == 9) {
								bombs++;
							}
						}
					}
					game_field[t][i] = bombs;
				}
				bombs = 0;
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
				System.out.print(game_field[h][g] + "\t");
			}
			System.out.println();
		}
	}

	/**
	 * Function to open up all the neighbor zero fields </p>
	 * This function is called whenever the player hits a zero field </p> 
	 * @param fieldVar
	 * @param x
	 * @param y
	 */
	public void openFields(int fieldVar, int x, int y) {
		// up
		if (game_field[x][y - 1] == 0) {
			game_field[x][y - 1] = -1;
			buttons[fieldVar - 9].setBackground(checked);
			openFields(fieldVar - 9, x, y - 1);
		}
		// right up
		if (game_field[x + 1][y - 1] == 0) {
			game_field[x + 1][y - 1] = -1;
			buttons[fieldVar - 9 + 1].setBackground(checked);
			openFields(fieldVar - 9 + 1, x + 1, y - 1);
		}
		// right
		if (game_field[x + 1][y] == 0) {
			game_field[x + 1][y] = -1;
			buttons[fieldVar + 1].setBackground(checked);
			openFields(fieldVar + 1, x + 1, y);
		}
		// right down
		if (game_field[x + 1][y + 1] == 0) {
			game_field[x + 1][y + 1] = -1;
			buttons[fieldVar + 9 + 1].setBackground(checked);
			openFields(fieldVar + 9 + 1, x + 1, y + 1);
		}
		// down
		if (game_field[x][y + 1] == 0) {
			game_field[x][y + 1] = -1;
			buttons[fieldVar + 9].setBackground(checked);
			openFields(fieldVar + 9, x, y + 1);
		}
		// left down
		if (game_field[x - 1][y + 1] == 0) {
			game_field[x - 1][y + 1] = -1;
			buttons[fieldVar + 9 - 1].setBackground(checked);
			openFields(fieldVar + 9 - 1, x - 1, y + 1);
		}
		// left
		if (game_field[x - 1][y] == 0) {
			game_field[x - 1][y] = -1;
			buttons[fieldVar - 1].setBackground(checked);
			openFields(fieldVar - 1, x - 1, y);
		}
		// left up
		if (game_field[x - 1][y - 1] == 0) {
			game_field[x - 1][y - 1] = -1;
			buttons[fieldVar - 9 - 1].setBackground(checked);
			openFields(fieldVar - 9 - 1, x - 1, y - 1);
		}

	}

	/**
	 * Shows the solution of the game </p>
	 * This function is called after the player hit a bomb!</p>
	 */
	public void showSolution() {
		int z = 1;
		for (int y = 1; y < 10; y++) {
			for (int x = 1; x < 10; x++) {
				if (game_field[x][y] != -1 && game_field[x][y] != 0 && game_field[x][y] != 9) {
					buttons[z].setText(game_field[x][y] + "");
					buttons[z].setBackground(checked);
				} else if (game_field[x][y] == 9) {
					buttons[z].setBackground(bomb);
				} else {
					buttons[z].setBackground(checked);
				}
				z++;
			}
		}
	}

	/**
	 * Does some action according to the button pressed
	 * </p>
	 * Left clicked button with mouse
	 * </p>
	 * 
	 * @param var Index of pressed Button
	 */
	public void primaryButtonAction(int var, int x, int y) {
		// checklist: , isZero?
		// isBomb?
		if (game_field[x][y] == 9) {
			buttons[var].setBackground(bomb);
			myAlert = new Alert(Alert.AlertType.INFORMATION, "Game Over, you hit a bomb!");
			running = false;
			myAlert.show();
			showSolution();
			return;
		}

		// already clicked
		if (buttons[var].getBackground() == checked) {
			return;
		}

		// no bomb nearby
		if (game_field[x][y] == 0) {
			// open all zero fields nearby
			openFields(var, x, y);
		} else if (game_field[x][y] > 0 && game_field[x][y] < 9) {
			// open the clicked field
			buttons[var].setBackground(checked);
			buttons[var].setText(game_field[x][y] + "");
		}
	}

	/**
	 * Does some action according to the button pressed
	 * </p>
	 * Right clicked button with mouse
	 * </p>
	 * 
	 * @param var Index of pressed Button
	 */
	public void secondaryButtonAction(int var) {
		if (buttons[var].getBackground() == bomb) {
			buttons[var].setBackground(unchecked);
			bombs++;
		} else {
			buttons[var].setBackground(bomb);
			bombs--;
		}
	}

	/**
	 * Resets or starts the game depending on the game state
	 */
	public void resetButtonClick() {
		if (running) {
			running = false;
			resetButton.setText("START GAME");
			for (int i = 1; i < 82; i++) {
				buttons[i].setBackground(unchecked);
				buttons[i].setText("");
			}
			bombs=10;
			bombsLabel.setText("Bombs: "+bombs);
		} else if (!running) {
			for (int i = 1; i < 82; i++) {
				buttons[i].setBackground(unchecked);
				buttons[i].setText("");
			}
			bombs = 10;
			bombsLabel.setText("Bombs: "+bombs);
			createField();
			running = true;
			resetButton.setText("RESTART GAME");
			startTimer();
		}

	}
	
	/**
	 * Starts the timer counting from 0 to whenever the game ends
	 */
	public void startTimer() {
		Thread timer = new Thread(new Runnable() {
			@Override
			public void run() {
				final int[] counter = {0};
				while (running) {
					counter[0]++;
					Platform.runLater(()->{timeLabel.setText("Time: "+counter[0]);});
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}});
		
		timer.start();
	}
	
	public void minButtonClicked() {
		Stage tempStage = (Stage) mypane.getScene().getWindow();
		tempStage.setIconified(true);
	}

	public void closeButtonClicked() {
		Stage temp = (Stage) mypane.getScene().getWindow();
		temp.close();
		System.exit(0);
	}

}