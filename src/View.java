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
		int winHeight = 500;
		int winWidth = 1000;

		int numCols = 9;
		int numRows = 9;
		int cellSize = 120;

		primaryStage.setTitle("SudokuFX");
		primaryStage.setHeight(winHeight);
		primaryStage.setWidth(winWidth);

		// The outer grid of the Sudoku grid that will be 3x3
		int outerGridCols = 3;
		int outerGridRows = outerGridCols;
		
		GridPane outerGrid = new GridPane();
		outerGrid.setPadding(new Insets(10, 10, 10, 10));
		outerGrid.setAlignment(Pos.CENTER);

		for(int i = 0; i < outerGridCols; i++) {
			outerGrid.getColumnConstraints().add(new ColumnConstraints(cellSize));
		}

		for(int i = 0; i < outerGridRows; i++) {
			outerGrid.getRowConstraints().add(new RowConstraints(cellSize));
		}

		// Adding the empty panes
		Pane[] squares = new Pane[9];

		for(int i = 0; i < squares.length; i++) {
			squares[i] = new Pane(new Label((i + 1) + ""));
			squares[i].getStyleClass().add("square-" + (i + 1));
		}


		int count = 0;

		for(int i = 0; i < outerGridCols; i++) {
			for(int j = 0; j < outerGridRows; j++) {
				outerGrid.add(squares[count++], j, i);
			}
		}

		// Outermost holder layout
		HBox holder = new HBox();
		holder.getChildren().addAll(outerGrid);

		Scene scene = new Scene(holder);
		scene.getStylesheets().add("Styles/layoutstyles.css");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
