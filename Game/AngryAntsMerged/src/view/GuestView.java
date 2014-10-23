package view;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import controller.AntController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//import javafx.scene.Group;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GuestView extends Region {

	AntController controller;
	double width, height;
	//Group this;

	public GuestView(AntController controller, double mWidth, double mHeight)
			throws MalformedURLException, URISyntaxException {
		//super(arg0, 600, 600);
		this.controller = controller;
		width = mWidth;
		height = mHeight;
		//this = (Group) arg0;
		initScene();
	}

	private void initScene() {

		Button tut = new Button("Tutorial");
		tut.setFont(Font.font("null", FontWeight.BOLD, 15));
		tut.setTranslateX(175);
		tut.setTranslateY(150);
		tut.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				controller.tutStart();

			}

		});

		Button start = new Button("Start Game");
		start.setFont(Font.font("null", FontWeight.BOLD, 15));
		start.setTranslateX(55);
		start.setTranslateY(150);
		start.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				controller.userStart();
			}
		});

		InnerShadow is = new InnerShadow();

		start.setEffect(is);
		tut.setEffect(is);

		this.getChildren().add(tut);
		this.getChildren().add(start);

	}

}
