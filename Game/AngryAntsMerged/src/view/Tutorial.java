package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
//import javafx.scene.Group;
//import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import controller.AntController;

//public class Tutorial extends Scene {

public class Tutorial extends Region {
	private HBox gameSetup = new HBox();
	private Button startgame = new Button("Start Game"), next = new Button(
			"Next"), previous = new Button("Previous");
	private GridPane btnGrid = new GridPane(), btnGrid2 = new GridPane(),
			btnGrid3 = new GridPane(), btnGrid4 = new GridPane(),
			btnGrid5 = new GridPane(), btnGrid6 = new GridPane();
	private ImageView tut1, tut2, tut3, tut4, tut5, tut6;
	private Text tutText1, tutText2,tutText3,tutText4,tutText5,tutText6;
	private BorderPane primary = new BorderPane();
	AntController controller;
	double width, height, tutscreen = 0;

	public Tutorial(AntController controller, double mWidth, double mHeight) {
		this.controller = controller;
		width = mWidth;
		height = mHeight;
		init();
	}

	public void init() {
		
		createGrids();

		next.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				tutscreen++;
				updateTutScreen();

			}

		});

		previous.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				tutscreen--;
				updateTutScreen();

			}
		});
		startgame.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				controller.userStart();

			}

		});

		updateTutScreen();

		primary.setTop(gameSetup);
		primary.setCenter(btnGrid);

		getChildren().addAll(primary);
	}

	private void createGrids() {
		btnGrid.setAlignment(Pos.CENTER);
		btnGrid.setHgap(25);
		btnGrid.setVgap(25);
		btnGrid.setPadding(new Insets(25, 25, 25, 25));
		btnGrid2.setAlignment(Pos.CENTER);
		btnGrid2.setHgap(25);
		btnGrid2.setVgap(25);
		btnGrid2.setPadding(new Insets(25, 25, 25, 25));
		btnGrid3.setAlignment(Pos.CENTER);
		btnGrid3.setHgap(25);
		btnGrid3.setVgap(25);
		btnGrid3.setPadding(new Insets(25, 25, 25, 25));
		btnGrid4.setAlignment(Pos.CENTER);
		btnGrid4.setHgap(25);
		btnGrid4.setVgap(25);
		btnGrid4.setPadding(new Insets(25, 25, 25, 25));		
		btnGrid5.setAlignment(Pos.CENTER);
		btnGrid5.setHgap(25);
		btnGrid5.setVgap(25);
		btnGrid5.setPadding(new Insets(25, 25, 25, 25));
		btnGrid6.setAlignment(Pos.CENTER);
		btnGrid6.setHgap(25);
		btnGrid6.setVgap(25);
		btnGrid6.setPadding(new Insets(25, 25, 25, 25));
		
		tut1 = new ImageView(new Image(getClass().getResource(
				"/img/1Tut.png").toString(), 300, 240, false, true));

		tutText1 = new Text();
		tutText1.setFill(Color.BLACK);
		tutText1.setFont(Font.font("null", FontWeight.BOLD, 12));
		tutText1.setText("The video will begin with a red circle indicating the ant you are to track.\n"
				+ "Click on the center of the ant to track the ant that has been selected for you.\n"
				+ "Once you click on the ant the video will progess to the next point in which\n"
				+ "the red circle will no longer be present. Continue to track the ant until the\n"
				+ "progress bar on the bottom of the screen is full.");

		btnGrid.add(tutText1, 1, 0);
		btnGrid.add(tut1, 0, 0);
		
		tut2 = new ImageView(new Image(getClass().getResource(
				"/img/controls.png").toString(), 900, 120, false, true));

		tutText2 = new Text();
		tutText2.setFill(Color.BLACK);
		tutText2.setFont(Font.font("null", FontWeight.BOLD, 12));
		tutText2.setText("This is the main control panel. It will contain the controls needed to\n"
				+ "move the video back and forth as well as the speed and sound.\n");

		btnGrid2.add(tutText2, 0, 1);
		btnGrid2.add(tut2, 0, 0);
		
		tut3 = new ImageView(new Image(getClass().getResource(
				"/img/arrows.png").toString(), 900, 120, false, true));

		tutText3 = new Text();
		tutText3.setFill(Color.BLACK);
		tutText3.setFont(Font.font("null", FontWeight.BOLD, 12));
		tutText3.setText("These are the arrows, they are used to move the video forward or backward.");

		btnGrid3.add(tutText3, 0, 1);
		btnGrid3.add(tut3, 0, 0);
		
		tut4 = new ImageView(new Image(getClass().getResource(
				"/img/speed.png").toString(), 900, 120, false, true));

		tutText4 = new Text();
		tutText4.setFill(Color.BLACK);
		tutText4.setFont(Font.font("null", FontWeight.BOLD, 12));
		tutText4.setText("This bar will set the speed in which the video will progress at.");

		btnGrid4.add(tutText4, 0, 1);
		btnGrid4.add(tut4, 0, 0);
		
		tut5 = new ImageView(new Image(getClass().getResource(
				"/img/pathbuttons.png").toString(), 900, 120, false, true));

		tutText5 = new Text();
		tutText5.setFill(Color.BLACK);
		tutText5.setFont(Font.font("null", FontWeight.BOLD, 12));
		tutText5.setText("These toggle whether the path you have drawn will be shown or not.");

		btnGrid5.add(tutText5, 0, 1);
		btnGrid5.add(tut5, 0, 0);
		
		tut6 = new ImageView(new Image(getClass().getResource(
				"/img/miscontrols.png").toString(), 900, 120, false, true));

		tutText6 = new Text();
		tutText6.setFill(Color.BLACK);
		tutText6.setFont(Font.font("null", FontWeight.BOLD, 12));
		tutText6.setText("The main menu button will return you to the main screen.\n"
				+ "The sound button will toggle the sound on and off.\n"
				+ "The numbers indicate the amount of points you have accumulated.");

		btnGrid6.add(tutText6, 0, 1);
		btnGrid6.add(tut6, 0, 0);
	}

	protected void updateTutScreen() {
		if (tutscreen == 0) {
			gameSetup.getChildren().removeAll(startgame, next, previous);
			gameSetup.getChildren().addAll(startgame, next);
			HBox.setMargin(startgame, new Insets(5, width / 2 - 100, 5,
					width / 2 - 50));
			HBox.setMargin(next, new Insets(5, 0, 5, 0));

			primary.setCenter(btnGrid);

		} else if (tutscreen == 1) {
			setTop();

			primary.setCenter(btnGrid2);
		} else if (tutscreen == 2) {
			setTop();

			primary.setCenter(btnGrid3);
		} else if (tutscreen == 3) {
			setTop();


			primary.setCenter(btnGrid4);
		} else if (tutscreen == 4) {
			setTop();

			primary.setCenter(btnGrid5);
		} else if (tutscreen == 5) {
			gameSetup.getChildren().removeAll(startgame, next, previous);
			gameSetup.getChildren().addAll(previous, startgame);
			HBox.setMargin(previous, new Insets(5, width / 2 - 125, 5, 5));
			HBox.setMargin(startgame, new Insets(5, 0, 5, 5));

			primary.setCenter(btnGrid6);
		}
	}

	private void setTop() {
		gameSetup.getChildren().removeAll(startgame, next, previous);
		gameSetup.getChildren().addAll(previous, startgame, next);
		HBox.setMargin(previous, new Insets(5, width / 2 - 120, 5, 5));
		HBox.setMargin(startgame, new Insets(5, width / 2 - 100, 5, 0));
		HBox.setMargin(next, new Insets(5, 0, 5, 0));

	}
}

// AntController controller;
// Group root;

// public Tutorial(Group root6, AntController antController) {
// super(root6, 800, 600);
// this.controller = antController;
// root = (Group) root6;
// initScene();
// }
//
// private void initScene() {
// TutRegion tr = new TutRegion();
// root.getChildren().add(tr);
// }

// }
