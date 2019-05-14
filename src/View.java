import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.scene.paint.Color;
import javafx.scene.text.FontPosture;
import javafx.event.EventTarget;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.Arrays;
import java.util.Comparator;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class View extends Application {
	public static Model model;
	public static Font editableFont;
	public static Font nonEditableFont;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		/*Setting up the Main Window*/
		int winHeight = 1000;
		int winWidth = 1500;

		primaryStage.setTitle("SudokuFX");
		primaryStage.setHeight(winHeight);
		primaryStage.setWidth(winWidth);

		/* Setting up the Game Scene */ 

		// Variables for the Sudoku Grid
		GridPane outerGrid = new GridPane();
		Pane[] outerCells = new Pane[9];
		GridPane[] innerGrids = new GridPane[9];
		StackPane[][] innerCells = new StackPane[9][9];

		// Variables for the right hand side controls
		VBox rightHolder = new VBox();
		rightHolder.setAlignment(Pos.CENTER);
		rightHolder.setPadding(new Insets(90, 90, 150, 90));

		// setting up the right hand side button grid
		GridPane buttonGrid = new GridPane();
		Button[] buttons = new Button[9];
		setUpButtonGrid(innerCells, buttonGrid, buttons);

		// setting up the right hand side option buttons
		HBox optionButtonArea = new HBox();
		optionButtonArea.setAlignment(Pos.CENTER);
		int optionAreaDimen = 3;
		int optionButtonWidth = 150;
		int optionButtonHeight = 50;
		int optionButtonMargin = 5;

		// Creating the clear button
		Button clearButton = new Button("Clear");
		clearButton.setPrefSize(optionButtonWidth, optionButtonHeight);
		HBox.setMargin(clearButton, new Insets(optionButtonMargin));

		clearButton.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					int row = model.getSelectedIndexTuple().getFirst();
					int col = model.getSelectedIndexTuple().getSecond();
					Alert alert = null;

					if (row == -1 || col == -1) {
						alert = new Alert(AlertType.ERROR, "You have to select a cell to clear");
						alert.showAndWait();
						return;
					}

					if (!model.getEditableCells()[row][col]) {
						alert = new Alert(AlertType.ERROR, "That cell is apart of the puzzle");
						alert.showAndWait();
						return;
					}

					model.clearCell(row, col);

					Text innerText = (Text) innerCells[row][col].getChildren().get(0);
					innerText.setText("");
				}
			});

		// Creating the Warnings Button
		Button warningsButton = new Button("Warnings: ON");
		warningsButton.setPrefSize(optionButtonWidth, optionButtonHeight);
		warningsButton.getStyleClass().add("warnings-on-btn");
		HBox.setMargin(warningsButton, new Insets(optionButtonMargin));

		warningsButton.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					model.toggleWarnings();

				if (model.getShouldWarn()) {
						warningsButton.getStyleClass().remove("warnings-off-btn");
						warningsButton.getStyleClass().add("warnings-on-btn");
						warningsButton.setText("Warnings: ON");
					} else {
						warningsButton.getStyleClass().remove("warnings-on-btn");
						warningsButton.getStyleClass().add("warnings-off-btn");
						warningsButton.setText("Warnings: OFF");
					}
				}
			});

		// Creating the Save Game Button
		Button newSaveButton = new Button("Save");
		newSaveButton.setPrefSize(optionButtonWidth, optionButtonHeight);
		HBox.setMargin(newSaveButton, new Insets(optionButtonMargin));

		newSaveButton.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Save Your Game");
					fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Game Files", "*.bin"));
					fileChooser.setInitialDirectory(new File("/home/bo/Documents/Projects/Java/Sudoku/Saved Games"));

					File selectedFile = fileChooser.showSaveDialog(primaryStage);

					if (selectedFile == null) {
						return;
					}

					model.saveGameState(selectedFile.toString());
				}
			});

		// Creating the Save Game Button
		Button newValidateButton = new Button("Validate");
		newValidateButton.setPrefSize(optionButtonWidth, optionButtonHeight);
		HBox.setMargin(newValidateButton, new Insets(optionButtonMargin));

		newValidateButton.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					Alert alert = null;
					boolean isGood = model.validatePuzzle();

					if (isGood) {
						alert = new Alert(AlertType.CONFIRMATION, "All Correct!");
					} else {
						alert = new Alert(AlertType.ERROR, "Something's wrong");
					}

					alert.showAndWait();
				}
			});

		optionButtonArea.getChildren().addAll(clearButton, warningsButton, newSaveButton, newValidateButton);

		rightHolder.getChildren().addAll(buttonGrid, optionButtonArea);

		// Outermost holder layout
		HBox holder = new HBox();
		holder.getChildren().addAll(outerGrid, rightHolder);

		// Creating the Game Scene and adding the holder to it
		Scene gameScene = new Scene(holder);
		gameScene.getStylesheets().add("Styles/layoutstyles.css");

		/* Craeating the homeScene */ 
		int homeButtonWidth = 300;
		int homeButtonHeight = 50;
		int homeButtonMargin = 5;

		VBox homeHolder = new VBox();
		homeHolder.setAlignment(Pos.CENTER);

		Text headerText = new Text("SudokuFX");
		headerText.setFont(Font.font("Arial", 150));

		Button homeNewGameButton = new Button("New Game");
		homeNewGameButton.setPrefSize(homeButtonWidth, homeButtonHeight);
		VBox.setMargin(homeNewGameButton, new Insets(homeButtonMargin));

		homeNewGameButton.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Choose A Puzzle to Start");
					fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Puzzle Files", "*.txt"));
					fileChooser.setInitialDirectory(new File("/home/bo/Documents/Projects/Java/Sudoku/Puzzles"));

					File selectedFile = fileChooser.showOpenDialog(primaryStage);

					if (selectedFile != null) {
						// Load the selected file as the new puzzle
						model = new Model();
						model.loadPuzzleFromFile(selectedFile.toString());
						if (!model.getShouldWarn()) {
							model.toggleWarnings();
						}
						setUpGrid(outerGrid, outerCells, innerGrids, innerCells, model);
						primaryStage.setScene(gameScene);
					}
				}
			});

		Button homeLoadGameButton = new Button("Load Game");
		homeLoadGameButton.setPrefSize(homeButtonWidth, homeButtonHeight);
		VBox.setMargin(homeLoadGameButton, new Insets(homeButtonMargin));

		homeLoadGameButton.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Choose A Save File to Load");
					fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Game Files", "*.bin"));
					fileChooser.setInitialDirectory(new File("/home/bo/Documents/Projects/Java/Sudoku/Saved Games"));
					fileChooser.setInitialFileName(".bin");

					File selectedFile = fileChooser.showOpenDialog(primaryStage);

					if (selectedFile != null) {
						// Load the selected file as the new puzzle
						try {
							FileInputStream fileIn = new FileInputStream(selectedFile);
							ObjectInputStream objectIn = new ObjectInputStream(fileIn);

							model = (Model) objectIn.readObject();

							objectIn.close();
							fileIn.close();

							setUpGrid(outerGrid, outerCells, innerGrids, innerCells, model);
							if (!model.getShouldWarn()) {
								model.toggleWarnings();
							}

							primaryStage.setScene(gameScene);

						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
			});

		Button homeContinueGameButton = new Button("Continue Game");
		homeContinueGameButton.setPrefSize(homeButtonWidth, homeButtonHeight);
		VBox.setMargin(homeContinueGameButton, new Insets(homeButtonMargin));

		homeContinueGameButton.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					File saveGameDir = new File("/home/bo/Documents/Projects/Java/Sudoku/Saved Games");

					if (saveGameDir != null) {
						try {
							// Get the most recently changed file in the dir
							if (saveGameDir.length() == 0) {
								return;
							}

							File[] gameFiles = saveGameDir.listFiles();

							Arrays.sort(gameFiles, new Comparator<File>() {
									public int compare(File file1, File file2) {
										Date first = new Date(file1.lastModified());
										Date second = new Date(file2.lastModified());

										return first.compareTo(second);
									}
								});


							FileInputStream fileIn = new FileInputStream(gameFiles[gameFiles.length - 1]);
							ObjectInputStream objectIn = new ObjectInputStream(fileIn);

							model = (Model) objectIn.readObject();

							objectIn.close();
							fileIn.close();

							setUpGrid(outerGrid, outerCells, innerGrids, innerCells, model);
							primaryStage.setScene(gameScene);

						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
			});

		homeHolder.getChildren().addAll(headerText, homeNewGameButton, homeLoadGameButton, homeContinueGameButton);

		Scene homeScene = new Scene(homeHolder);
		homeScene.getStylesheets().add("Styles/layoutstyles.css");

		primaryStage.setScene(homeScene);
		primaryStage.show();
	}

	// Grid Set up Methods
	public void setUpGrid(GridPane outerGrid, Pane[] outerCells, GridPane[] innerGrids, StackPane[][] innerCells, Model theModel) {
		int cellSize = 200;
		int innerCellSize = cellSize / 3;

		setupOuterGrid(outerGrid, cellSize);
		setupOuterCells(outerCells);
		setupInnerGrids(innerGrids, innerCellSize);
		setupInnerCells(innerCells, theModel);

		// adding the innerGrids to the outerCells
		for(int i = 0; i < outerCells.length; i++) {
			outerCells[i].getChildren().add(innerGrids[i]);
		}

		// adding the innerCells to the innerGrids
		int innerGridDimen = 3;
		int rowOffset = 0;
		int colOffset = 0;

		for(int i = 0; i < innerGrids.length; i++) {
			for(int j = 0; j < innerGridDimen; j++) {
				for(int k = 0; k < innerGridDimen; k++) {
					innerGrids[i].add(innerCells[j + rowOffset][k + colOffset], k, j);
				}
			}

			if (i == 2 || i == 5) {
				rowOffset += 3;
			}
			colOffset = (colOffset + 3) % 9;
		}

		// adding outerCells to the outerGrid
		int outerGridDimen = 3;
		int outerCellCount = 0;

		for(int i = 0; i < outerGridDimen; i++) {
			for(int j = 0; j < outerGridDimen; j++) {
				outerGrid.add(outerCells[outerCellCount++], j, i);
			}
		}
	}

	public void setupOuterGrid(GridPane outerGrid, int cellSize) {
		int outerGridDimen = 3;

		outerGrid.setPadding(new Insets(90));
		outerGrid.setAlignment(Pos.CENTER);

		for(int i = 0; i < outerGridDimen; i++) {
			outerGrid.getColumnConstraints().add(new ColumnConstraints(cellSize));
			outerGrid.getRowConstraints().add(new RowConstraints(cellSize));
		}
	}


	public void setupInnerGrids(GridPane[] innerGrids, int innerCellSize) {
		int innerGridDimen = 3;

		for(int i = 0; i < innerGrids.length; i++) {
			innerGrids[i] = new GridPane();

			for(int j = 0; j < innerGridDimen; j++) {
				innerGrids[i].getColumnConstraints().add(new ColumnConstraints(innerCellSize));
				innerGrids[i].getRowConstraints().add(new RowConstraints(innerCellSize));
			}
		}
	}

	public void setupOuterCells(Pane[] outerCells) {
		for(int i = 0; i < outerCells.length; i++) {
			outerCells[i] = new Pane();
			outerCells[i].getStyleClass().add("square-" + (i + 1));
		}
	}

	public void setupInnerCells(StackPane[][] innerCells, Model theModel) {
		// Get the Models Data
		int[][] thePuzzleGrid = theModel.getPuzzleGrid();
		boolean[][] theEditableCells = theModel.getEditableCells();

		for(int i = 0; i < innerCells.length; i++) {
			for(int j = 0; j < innerCells[0].length; j++) {
				innerCells[i][j] = new StackPane();
				Text cellText = new Text("");

				if (thePuzzleGrid[i][j] != 0) {
					cellText.setText(thePuzzleGrid[i][j] + "");
					if (theEditableCells[i][j]) {
						cellText.setFont(editableFont);
						cellText.setFill(Color.BLUE);
					} else {
						cellText.setFont(nonEditableFont);
					}
				} else {
					cellText.setFont(editableFont);
					cellText.setFill(Color.BLUE);
				}

				innerCells[i][j].getChildren().add(cellText);

				innerCells[i][j].addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
						public void handle(MouseEvent event) {
							StackPane theEventTarget = null;
							Text theTargetText = null;

							// Remove the selected class from the old selected cell
							int selectedRowIndex = model.getSelectedIndexTuple().getFirst();
							int selectedColIndex = model.getSelectedIndexTuple().getSecond();

							if (selectedRowIndex != -1 && selectedColIndex != -1) {
								innerCells[selectedRowIndex][selectedColIndex].getStyleClass().remove("selected-grid");
							}

							if (event.getTarget() instanceof Text) {
								theTargetText = (Text) event.getTarget();

								for(int i = 0; i < innerCells.length; i++) {
									for(int j = 0; j < innerCells[0].length; j++) {
										if (innerCells[i][j].getChildren().get(0).equals(theTargetText)) {
											model.setSelected(i, j);
											innerCells[i][j].getStyleClass().add("selected-grid");
										}

									}
								}
							} else {
								theEventTarget = (StackPane) event.getTarget();
								for (int i = 0; i < innerCells.length; i++) {
									for(int j = 0; j < innerCells[0].length; j++) {
										if (innerCells[i][j].equals(theEventTarget)) {
											model.setSelected(i, j);
											innerCells[i][j].getStyleClass().add("selected-grid");
										}
									}
								}
							}
						}
					});

				innerCells[i][j].getStyleClass().add("inner-grid");
			}
		}
	}

	// Button Grid Set up Methods
	public void setUpButtonGrid(StackPane[][] innerCells, GridPane buttonGrid, Button[] buttons) {
		int gridDimen = 3;
		int gridSize = 100;

		for(int i = 0; i < gridDimen; i++) {
			buttonGrid.getColumnConstraints().add(new ColumnConstraints(gridSize));
			buttonGrid.getRowConstraints().add(new RowConstraints(gridSize));
		}

		// Adding the Buttons
		setupButtons(innerCells, buttons, gridSize);

		int buttonIndex = 0;
		for(int i = 0; i < gridDimen; i++) {
			for(int j = 0; j < gridDimen; j++) {
				buttonGrid.add(buttons[buttonIndex++], j, i);
			}
		}

		buttonGrid.setPadding(new Insets(90));
		buttonGrid.setAlignment(Pos.CENTER);
	}

	public void setupButtons(StackPane[][] innerCells, Button[] buttons, int gridSize) {
		for(int i = 0; i < buttons.length; i++) {
			buttons[i] = new Button((i + 1) + "");
			buttons[i].setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent event) {
						int row = model.getSelectedIndexTuple().getFirst();
						int col = model.getSelectedIndexTuple().getSecond();

						if (row == -1 || col == -1) {
							return;
						}

						String num = ((Button) event.getTarget()).getText();
						Alert alert = null;

						if (!model.getEditableCells()[row][col]) {
							alert = new Alert(AlertType.ERROR, "That cell is not editable");
							alert.showAndWait();
							return;
						}

						if (!model.makeMove(row, col, Integer.parseInt(num))) {
							System.out.println("The number that was passed was not valid");
							return;
						}

						Text innerText = (Text) innerCells[row][col].getChildren().get(0);
						innerText.setText(num);
					}
				});

			buttons[i].setPrefSize(gridSize, gridSize);
		}
	}

	// methods for setting up option huttons

	public static void main(String[] args) {
		// Setting up the model before launching the application
		//model = new Model();
		//innerCells = new StackPane[9][9];
		//model.loadPuzzleFromFile("../Puzzles/test.txt");
		editableFont = Font.font("Arial", FontPosture.ITALIC, 20);
		nonEditableFont = new Font("Arial", 20);

		Application.launch(args);
	}
}
