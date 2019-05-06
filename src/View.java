import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class View extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("My First JavaFX App");

		Label label = new Label("Hello World");
		Scene scene = new Scene(label, 400, 200);
		primaryStage.setScene(scene);

		primaryStage.show();

		/*
		// Making and showing a second stage from scratch
		Label label2 = new Label("This is the second stage");
		Scene scene2 = new Scene(label2, 400, 200);
		Stage stage2 = new Stage();
		stage2.setTitle("This is the second stage");
		stage2.setScene(scene2);
		stage2.show();
		*/
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
