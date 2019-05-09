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



public class View extends Application {
	public static Model model;
	public static Font editableFont;
	public static Font nonEditableFont;
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Setting up the Main Window
		int winHeight = 1000;
		int winWidth = 1500;

		primaryStage.setTitle("SudokuFX");
		primaryStage.setHeight(winHeight);
		primaryStage.setWidth(winWidth);

		// Setting up the Sudoku Grid
		GridPane outerGrid = new GridPane();
		Pane[] outerCells = new Pane[9];
		GridPane[] innerGrids = new GridPane[9];
		StackPane[][] innerCells = new StackPane[9][9];

		setUpGrid(outerGrid, outerCells, innerGrids, innerCells, model);

		// Setting up the right hand side controls
		GridPane buttonGrid = new GridPane();
		Button[] buttons = new Button[9];
		setUpButtonGrid(buttonGrid, buttons);

		// Outermost holder layout
		HBox holder = new HBox();
		holder.getChildren().addAll(outerGrid, buttonGrid);

		Scene scene = new Scene(holder);
		scene.getStylesheets().add("Styles/layoutstyles.css");
		primaryStage.setScene(scene);
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
					cellText.setFont(nonEditableFont);
				} else {
					cellText.setFont(editableFont);
					cellText.setFill(Color.BLUE);
				}

				innerCells[i][j].getChildren().add(cellText);

				//innerCells[i][j].addEventFilter(MouseEvent.MOUSE_CLICKED, new Controller.CellClickHandler());
				innerCells[i][j].addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
						public void handle(MouseEvent event) {
							StackPane theEventTarget = null;
							Text theTargetText = null;

							// Remove the selected class from the old selected cell
							Tuple oldSelected = model.getSelectedIndexTuple();
							int selectedRowIndex = oldSelected.getFirst();
							int selectedColIndex = oldSelected.getSecond();

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
	public void setUpButtonGrid(GridPane buttonGrid, Button[] buttons) {
		int gridDimen = 3;
		int gridSize = 125;

		for(int i = 0; i < gridDimen; i++) {
			buttonGrid.getColumnConstraints().add(new ColumnConstraints(gridSize));
			buttonGrid.getRowConstraints().add(new RowConstraints(gridSize));
		}

		// Adding the Buttons
		setupButtons(buttons, gridSize);

		int buttonIndex = 0;
		for(int i = 0; i < gridDimen; i++) {
			for(int j = 0; j < gridDimen; j++) {
				buttonGrid.add(buttons[buttonIndex++], j, i);
			}
		}

		buttonGrid.setPadding(new Insets(90));
		buttonGrid.setAlignment(Pos.CENTER);
	}

	public void setupButtons(Button[] buttons, int gridSize) {
		for(int i = 0; i < buttons.length; i++) {
			buttons[i] = new Button((i + 1) + "");
			buttons[i].setPrefSize(gridSize, gridSize);
		}
	}

	public static void main(String[] args) {
		// Setting up the model before launching the application
		model = new Model();
		model.loadPuzzleFromFile("test.txt");
		editableFont = Font.font("Arial", FontPosture.ITALIC, 20);
		nonEditableFont = new Font("Arial", 20);

		Application.launch(args);
	}
}
