package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
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

public class Tutorial extends Scene {

	private class TutRegion extends Region {
		private ScrollPane scroll;
		private HBox gameSetup;

		public TutRegion() {
			init();
		}

		public void init() {
			scroll = new ScrollPane();
			scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
			scroll.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
			scroll.setPrefSize(800, 600);

			GridPane btnGrid = new GridPane();
			btnGrid.setAlignment(Pos.CENTER);
			btnGrid.setHgap(25);
			btnGrid.setVgap(25);
			btnGrid.setPadding(new Insets(25, 25, 25, 25));

			ImageView tut1 = new ImageView(new Image(getClass()
					.getResourceAsStream("/img/1Tut.png"), 300, 240, false,
					true));

			Text tutText1 = new Text();
			tutText1.setFill(Color.BLACK);
			tutText1.setFont(Font.font("null", FontWeight.BOLD, 12));
			tutText1.setText("The video will begin with a red circle indicating the ant you are to track.\n"
					+ "Click on the center of the ant to track the ant that has been selected for you.\n"
					+ "Once you click on the ant the video will progess to the next point in which\n"
					+ "the red circle will no longer be present. Continue to track the ant until the\n"
					+ "progress bar on the bottom of the screen is full.");

			ImageView arrow_r = new ImageView(new Image(getClass()
					.getResourceAsStream("/img/arrow_right.png"), 300, 240,
					false, true));
			ImageView arrow_l = new ImageView(new Image(getClass()
					.getResourceAsStream("/img/arrow_left.png"), 300, 240,
					false, true));
			Text aRtext = new Text();
			aRtext.setFill(Color.BLACK);
			aRtext.setFont(Font.font("null", FontWeight.BOLD, 12));
			aRtext.setText("This arrow will progress the video forward one time point at a time until the\n"
					+ "furthest in the video you have tracked");

			Text aLtext = new Text();
			aLtext.setFill(Color.BLACK);
			aLtext.setFont(Font.font("null", FontWeight.BOLD, 12));
			aLtext.setText("This arrow will degress the video backward one time point at a time until the\n"
					+ "beginning of the video as well as highlighting the previous location you have\n"
					+ "clicked");
			btnGrid.add(tutText1, 1, 0);
			btnGrid.add(tut1, 0, 0);
			btnGrid.add(arrow_r, 0, 2);
			btnGrid.add(arrow_l, 0, 3);
			btnGrid.add(aRtext, 1, 2);
			btnGrid.add(aLtext, 1, 3);

			Button startGame = new Button("Start Game");
			startGame.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {
					controller.userStart();

				}

			});

			gameSetup = new HBox();
			gameSetup.getChildren().addAll(startGame);
			HBox.setMargin(startGame, new Insets(5, 0, 5, 5));

			BorderPane primary = new BorderPane();
			scroll.setContent(btnGrid);
			primary.setTop(gameSetup);
			primary.setCenter(scroll);

			getChildren().addAll(primary);
		}
	}

	AntController controller;
	Group root;

	public Tutorial(Group root6, AntController antController) {
		super(root6, 800, 600);
		this.controller = antController;
		root = (Group) root6;
		initScene();
	}

	private void initScene() {
		TutRegion tr = new TutRegion();
		root.getChildren().add(tr);
	}

}
