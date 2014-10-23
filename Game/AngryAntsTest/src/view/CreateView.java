package view;

import controller.AntController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CreateView extends Scene {

	private AntController controller;
	private Group root;

	public CreateView(Parent arg0, AntController controller) {
		super(arg0, 400, 400);
		this.controller = controller;
		root = (Group) arg0;
		initScene();
	}

	private void initScene() {
		setFill(Color.LIGHTGRAY);
		VBox vb1 = new VBox();
		vb1.setPadding(new Insets(10, 0, 0, 10));
		vb1.setSpacing(10);
		HBox hb1 = new HBox();
		hb1.setSpacing(10);
		hb1.setAlignment(Pos.CENTER_LEFT);

		VBox vb = new VBox();
		vb.setPadding(new Insets(10, 0, 0, 10));
		vb.setSpacing(10);
		HBox hb = new HBox();
		hb.setSpacing(10);
		hb.setAlignment(Pos.CENTER_LEFT);

		InnerShadow is = new InnerShadow();
		DropShadow ds = new DropShadow();
		ds.setOffsetY(5.0);
		ds.setOffsetX(-5.0);

		final Label message = new Label("");
		message.setFont(Font.font("null", FontWeight.BOLD, 10));
		message.setTextFill(Color.WHITE);

		Label user = new Label("Username: ");
		user.setFont(Font.font("null", FontWeight.BOLD, 15));
		user.setTextFill(Color.ANTIQUEWHITE);
		final TextField uTF = new TextField();
		uTF.setOpacity(0.5);
		uTF.setEffect(is);

		Label label = new Label("Password: ");
		label.setFont(Font.font("null", FontWeight.BOLD, 15));
		label.setTextFill(Color.ANTIQUEWHITE);
		final PasswordField pb = new PasswordField();
		pb.setEffect(is);
		pb.setOpacity(0.5);

		hb1.getChildren().addAll(user, uTF);
		hb.getChildren().addAll(label, pb);
		vb.getChildren().addAll(hb, message);
		vb.setTranslateX(50);
		vb.setTranslateY((this.getHeight() / 2));
		vb.setEffect(ds);
		vb1.getChildren().addAll(hb1, message);

		vb1.setTranslateX(50);
		vb1.setTranslateY((this.getHeight() / 2) - 50);
		vb1.setEffect(ds);

		root.getChildren().add(vb);
		root.getChildren().add(vb1);

		Button create = new Button("Create Account");
		create.setFont(Font.font("null", FontWeight.BOLD, 15));
		create.setTranslateX(this.getWidth() - (this.getWidth() - 30));
		create.setTranslateY(this.getHeight() - 45);
		create.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				controller.setUser("CREATE", uTF.getText(), pb.getText());

			}
		});

		Button goback = new Button("Go Back");
		goback.setFont(Font.font("null", FontWeight.BOLD, 15));
		goback.setTranslateX(this.getWidth() - 75);
		goback.setTranslateY(this.getHeight() - 75);
		goback.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				controller.gotoIntro();

			}
		});

		root.getChildren().addAll(create, goback);

	}

	public void popUp() {
		MessageBox mb = new MessageBox("User Name already exist");
		mb.setSize(200, 100);
		mb.show();

	}

}
