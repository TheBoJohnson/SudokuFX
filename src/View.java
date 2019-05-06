import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class View extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		int numCols = 9;
		int numRows = 9;
		int cellSize = 40;

		primaryStage.setTitle("My First JavaFX App");
		primaryStage.setHeight(500);
		primaryStage.setWidth(500);


		GridPane gridPane = new GridPane();
		gridPane.setHgap(1);
		gridPane.setVgap(1);
		gridPane.setPadding(new Insets(10, 10, 10, 10));
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setGridLinesVisible(true);
		
		//gridPane.setHgap(25);

		// Setitng up the grid for Sudoku
		for(int i = 0; i < numCols; i++) {
			gridPane.getColumnConstraints().add(new ColumnConstraints(cellSize));
		}

		for(int i = 0; i < numRows; i++) {
			gridPane.getRowConstraints().add(new RowConstraints(cellSize));
		}

		for(int i = 0; i < numCols; i++) {
			for(int j = 0; j < numRows; j++) {
				/*
				Label label = new Label("1");
				GridPane.setConstraints(label, i, j);
				gridPane.getChildren().add(label);
				*/
				gridPane.add(new Label("1"), i, j);
			}
		}

		Scene scene = new Scene(gridPane);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
