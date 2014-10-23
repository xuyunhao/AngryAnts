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

public class GuestView extends Scene {

	AntController controller;
	Group root;

	public GuestView(Parent arg0, AntController controller)
			throws MalformedURLException, URISyntaxException {
		super(arg0, 600, 600);
		this.controller = controller;
		root = (Group) arg0;
		initScene();
	}

	private void initScene() {

		Button tut = new Button("Tutorial");
		tut.setFont(Font.font("null", FontWeight.BOLD, 15));
		tut.setTranslateX(this.getWidth() / 2 - 175);
		tut.setTranslateY(this.getHeight() - 150);
		tut.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				controller.tutStart();

			}

		});

		Button start = new Button("Start Game");
		start.setFont(Font.font("null", FontWeight.BOLD, 15));
		start.setTranslateX((this.getWidth() / 2) + 55);
		start.setTranslateY(this.getHeight() - 150);
		start.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				controller.userStart();
			}
		});

		InnerShadow is = new InnerShadow();

		start.setEffect(is);
		tut.setEffect(is);

		root.getChildren().add(tut);
		root.getChildren().add(start);

	}

}
