package view;

import java.io.File;
import java.net.MalformedURLException;
import javax.swing.JFrame;
import controller.AntController;

import model.Profile;

import javafx.embed.swing.JFXPanel;
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
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class IntroScreen extends Region {

	static JFrame frame;
	static JFXPanel fxPanel;
	static Stage stage;
	// static Group this;
	static Profile currentUser;
	static String imageView;
	private AntController controller;
	double width, height;

	public IntroScreen(AntController controller, double mWidth, double mHeight)
			throws MalformedURLException {
		// super(arg0, 600, 600);
		this.controller = controller;
		width = mWidth;
		height = mHeight;
		// this = (Group) arg0;
		initScene();
	}

	private void initScene() throws MalformedURLException {
		// setFill(Color.LIGHTGRAY);
		File file = new File(System.getProperty("user.dir")
				+ "/bin/img/AngryAnts.png");
		Image image = new Image(file.toURI().toURL().toString());
		ImageView iv = new ImageView();
		iv.setImage(image);
		iv.setFitHeight(this.height);
		iv.setFitWidth(this.width);
		this.getChildren().add(iv);

		// Set Title Text
		Text text = new Text();
		text.setX((this.width / 2) - 100);
		text.setY((this.height / 2) - 200);
		text.setFill(Color.CHOCOLATE);
		text.setFont(Font.font("null", FontWeight.BOLD, 36));
		DropShadow ds = new DropShadow();
		ds.setOffsetY(5.0);
		ds.setOffsetX(-5.0);
		text.setEffect(ds);

		Bloom bloom = new Bloom();
		bloom.setThreshold(0.9);
		this.setEffect(bloom);
		text.setText("Angry Ants");
		this.getChildren().add(text);

		// Set User and Password Text Fields
		final Label message = new Label("");
		message.setFont(Font.font("null", FontWeight.BOLD, 10));
		message.setTextFill(Color.WHITE);

		VBox vb1 = new VBox();
		vb1.setPadding(new Insets(10, 0, 0, 10));
		vb1.setSpacing(10);
		HBox hb1 = new HBox();
		hb1.setSpacing(10);
		hb1.setAlignment(Pos.CENTER_LEFT);

		InnerShadow is = new InnerShadow();

		Label user = new Label("Username: ");
		user.setFont(Font.font("null", FontWeight.BOLD, 15));
		user.setTextFill(Color.ANTIQUEWHITE);
		final TextField uTF = new TextField();
		uTF.setOpacity(0.5);
		uTF.setEffect(is);

		hb1.getChildren().addAll(user, uTF);
		vb1.getChildren().addAll(hb1, message);

		vb1.setTranslateX(50);
		vb1.setTranslateY((this.height / 2) - 50);
		vb1.setEffect(ds);

		final Label message2 = new Label("");

		VBox vb = new VBox();
		vb.setPadding(new Insets(10, 0, 0, 10));
		vb.setSpacing(10);
		HBox hb = new HBox();
		hb.setSpacing(10);
		hb.setAlignment(Pos.CENTER_LEFT);

		Label label = new Label("Password: ");
		label.setFont(Font.font("null", FontWeight.BOLD, 15));
		label.setTextFill(Color.ANTIQUEWHITE);
		final PasswordField pb = new PasswordField();
		pb.setEffect(is);
		pb.setOpacity(0.5);

		pb.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				// if (containProfile(uTF.getText())
				// && !checkPass(uTF.getText(), pb.getText())) {
				// message2.setText("Your password is incorrect!");
				// message2.setFont(Font.font("null", FontWeight.BOLD, 10));
				// message2.setTextFill(Color.BLACK);
				// } else if (!containProfile(uTF.getText())) {
				// message2.setText("User name does not exist!");
				// message2.setFont(Font.font("null", FontWeight.BOLD, 10));
				// message2.setTextFill(Color.BLACK);
				// }
				//
				// else {
				// message2.setText("Your password has been confirmed welcome "
				// + currentUser.getName());
				// message2.setFont(Font.font("null", FontWeight.BOLD, 10));
				// message2.setTextFill(Color.BLACK);
				// }
				// pb.clear();

			}

		});
		hb.getChildren().addAll(label, pb);
		vb.getChildren().addAll(hb, message2);
		vb.setTranslateX(50);
		vb.setTranslateY((this.height / 2));
		vb.setEffect(ds);

		this.getChildren().add(vb1);
		this.getChildren().add(vb);

		// Set up buttons
		Button freetrace = new Button("Guest");
		freetrace.setFont(Font.font("null", FontWeight.BOLD, 15));
		freetrace.setTranslateX((this.width / 2) + 80);
		freetrace.setTranslateY(this.height - 330);
		freetrace.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				controller.sendToServer("LOGIN", "Guest", "");
			}
		});

		Button login = new Button("Log in");
		login.setFont(Font.font("null", FontWeight.BOLD, 15));
		login.setTranslateX((this.width / 2) - 160);
		login.setTranslateY(this.height - 230);
		login.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				controller.sendToServer("LOGIN", uTF.getText(), pb.getText());
				// controller.userScreen();

			}

		});

		Button create = new Button("Create Account");
		create.setFont(Font.font("null", FontWeight.BOLD, 15));
		create.setTranslateX(this.width - (this.width - 25));
		create.setTranslateY(this.height - 45);
		create.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				controller.createUser();
				// if (pb.getText() == null) {
				// message2.setText("Please input a password in text field");
				// message2.setFont(Font.font("null", FontWeight.BOLD, 10));
				// message2.setTextFill(Color.BLACK);
				// } else if (uTF.getText() == uTF.getPromptText()) {
				// message2.setText("Please input a user name in text field");
				// message2.setFont(Font.font("null", FontWeight.BOLD, 10));
				// message2.setTextFill(Color.BLACK);
				// } else if (containProfile(uTF.getText())) {
				// message2.setText("User name already in use");
				// message2.setFont(Font.font("null", FontWeight.BOLD, 10));
				// message2.setTextFill(Color.BLACK);
				// } else {
				// users.add(new Profile(uTF.getText(), pb.getText()));
				// }
			}
		});

		create.setEffect(is);
		freetrace.setEffect(is);
		login.setEffect(is);
		// File image = new
		// File("E:\\eclipse-SDK-3.7.2-win32-x86_64-efx-0.1.0\\workspace\\AngryAntsTest\\bin\\img\\ant2.png");
		Image soundI = new Image(getClass().getResourceAsStream(
				"/img/sound.png"), 36, 36, false, true);
		Image nsoundI = new Image(getClass().getResourceAsStream(
				"/img/Nosound.png"), 36, 36, false, true);
		final Button button5 = new Button();
		final ImageView im1 = new ImageView(soundI);
		final ImageView im2 = new ImageView(nsoundI);
		button5.setGraphic(im1);
		imageView = "im1";
		button5.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (imageView.equals("im1")) {
					button5.setGraphic(im2);
					imageView = "im2";
				} else {
					button5.setGraphic(im1);
					imageView = "im1";
				}
				controller.toggleSound();

			}
		});
		button5.setTranslateX(this.width - 75);
		button5.setTranslateY(15);

		this.getChildren().add(button5);
		this.getChildren().add(login);
		this.getChildren().add(create);
		this.getChildren().add(freetrace);

	}

	public void popUp() {
		MessageBox mb = new MessageBox(
				"Invalid User Information!\nPassword incorrect or Username does not exist");
		mb.setSize(400, 200);
		mb.show();

	}

}
