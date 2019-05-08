import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.HPos;

public class View extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		// Setting up the Main Window
		int winHeight = 1000;
		int winWidth = 1000;

		primaryStage.setTitle("SudokuFX");
		primaryStage.setHeight(winHeight);
		primaryStage.setWidth(winWidth);

		GridPane outerGrid = new GridPane();
		Pane[] outerCells = new Pane[9];
		GridPane[] innerGrids = new GridPane[9];
		Pane[][] innerCells = new Pane[9][9];

		setUpGrid(outerGrid, outerCells, innerGrids, innerCells);

		// Outermost holder layout
		HBox holder = new HBox();
		holder.getChildren().addAll(outerGrid);

		Scene scene = new Scene(holder);
		scene.getStylesheets().add("Styles/layoutstyles.css");
		primaryStage.setScene(scene);
		primaryStage.show();

	}
	public void setUpGrid(GridPane outerGrid, Pane[] outerCells, GridPane[] innerGrids, Pane[][] innerCells) {
		int cellSize = 200;
		int innerCellSize = cellSize / 3;

		setupOuterGrid(outerGrid, cellSize);
		setupOuterCells(outerCells);
		setupInnerGrids(innerGrids, innerCellSize);
		setupInnerCells(innerCells);

		// adding the innerGrids to the outerCells
		for(int i = 0; i < outerCells.length; i++) {
			outerCells[i].getChildren().addAll(innerGrids[i]);
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

		outerGrid.setPadding(new Insets(10, 10, 10, 10));
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

	public void setupInnerCells(Pane[][] innerCells) {
		for(int i = 0; i < innerCells.length; i++) {
			for(int j = 0; j < innerCells[0].length; j++) {
				innerCells[i][j] = new Pane();;
				innerCells[i][j].getStyleClass().add("inner-grid");
			}
		}
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
