package view;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import controller.AntController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.InnerShadow;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UserView extends Scene {

	AntController controller;
	Group root;

	public UserView(Parent arg0, AntController controller)
			throws MalformedURLException, URISyntaxException {
		super(arg0, 600, 600);
		this.controller = controller;
		root = (Group) arg0;
		initScene();
	}

	private void initScene() throws MalformedURLException, URISyntaxException {

		// Set image background
		// URL path = new URL(getClass().getResource("\\img\\Fireants.jpg"),
		// null);
		// File file = new File(path.toURI().toURL().toString());
		// File file = new File(
		// "E:\\eclipse-SDK-3.7.2-win32-x86_64-efx-0.1.0\\workspace\\AngryAntsTest\\bin\\img\\Fireants.jpg");

		// Image image = new Image(file.toURI().toURL().toString());
		// ImageView iv = new ImageView();
		// iv.setImage(image);
		// iv.setFitHeight(this.getHeight());
		// iv.setFitWidth(this.getWidth());
		// root.getChildren().add(iv);

		Button tut = new Button("Tutorial");
		tut.setFont(Font.font("null", FontWeight.BOLD, 15));
		tut.setTranslateX(this.getWidth() / 2 - 55);
		tut.setTranslateY(this.getHeight() - 305);
		tut.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				controller.tutStart();
			}
		});

		// Set up buttons
//		Button mode = new Button("Pick Mode");
//		mode.setFont(Font.font("null", FontWeight.BOLD, 15));
//		mode.setTranslateX((this.getWidth() / 2) - 55);
//		mode.setTranslateY(this.getHeight() - 245);

		Button freetrace = new Button("Start Game");
		freetrace.setFont(Font.font("null", FontWeight.BOLD, 15));
		freetrace.setTranslateX((this.getWidth() / 2) - 55);
		freetrace.setTranslateY(this.getHeight() - 210);
		freetrace.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				controller.userStart();
			}
		});

		Button leaderboard = new Button("LeaderBoards");
		leaderboard.setFont(Font.font("null", FontWeight.BOLD, 15));
		leaderboard.setTranslateX((this.getWidth() / 2) - 70);
		leaderboard.setTranslateY(this.getHeight() - 140);

		InnerShadow is = new InnerShadow();

		freetrace.setEffect(is);
//		mode.setEffect(is);
		leaderboard.setEffect(is);

		root.getChildren().add(tut);
		root.getChildren().add(leaderboard);
//		root.getChildren().add(mode);
		root.getChildren().add(freetrace);
	}

}
