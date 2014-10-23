/**
 * @author Paul Shen
 * 
 * Displays video selection screen.  Creates the game component based on the mode and video
 * selected.
 */

package view;

import java.util.List;

import model.Video;

import controller.AntController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class VideoView extends Region {
	
	private AntController controller;
	private HBox gameSetup;
	private ScrollPane scroll;

	public VideoView(AntController controller, final List<Video> videos) {
		this.controller = controller;
		
		// Scrollbar appears when there are >4 videos
		scroll = new ScrollPane();
		scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		scroll.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scroll.setPrefSize(960, 596);
		
		GridPane btnGrid = new GridPane();
		btnGrid.setAlignment(Pos.CENTER);
		btnGrid.setHgap(24);
		btnGrid.setVgap(24);
		btnGrid.setPadding(new Insets(24, 24, 24, 24));
		
		// Add video selection buttons
		for (int i = 0; i < videos.size(); i++) {
			Video tempVid = videos.get(i);
			
			// Create image for button of size 300x240
			Image tempImg = new Image(getClass().getResourceAsStream("/vid/" + tempVid.getName() + 
				".jpg"), 416, 234, false, true);
			
//			Button tempBtn = new Button(tempVid.getFileName(), new ImageView(tempImg));
			Button tempBtn = new Button();
			tempBtn.setGraphic(new ImageView(tempImg));
			tempBtn.setTooltip(new Tooltip(tempVid.getName()));
			tempBtn.setPrefWidth(400);
			tempBtn.setPrefHeight(225);
			tempBtn.setOnAction(new VideoHandler());
			
			// 2 buttons per row
			btnGrid.add(tempBtn, i%2, i/2);
		}
		
		// Final setup of all containers
		Label topText = new Label("Select a video (hover over for name)");
		
		gameSetup = new HBox();
		gameSetup.getChildren().addAll(topText);
		HBox.setMargin(topText, new Insets(5, 0, 5, 5));
		
		BorderPane primary = new BorderPane();
		scroll.setContent(btnGrid);
		primary.setTop(gameSetup);
		primary.setCenter(scroll);
		
		getChildren().addAll(gameSetup, scroll);
	}
	
	@Override
	protected void layoutChildren() {
		gameSetup.resizeRelocate(0, 0, 960, 26);
		//scroll.resizeRelocate(0, 26, 960, 570);
		scroll.resizeRelocate(0, 26, 960, 629);  // ADDED
	}
	
	@Override
	protected double computePrefWidth(double width) {
		return 1024;
	}
	
	@Override
	protected double computePrefHeight(double height) {
		return 632;
	}
	
	/**
	 * Calls controller to start game, passing the video's filename and game type option.
	 */
	private class VideoHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			// Videos matched by tooltip
			String fileName = ((Button) event.getSource()).getTooltip().getText();
			controller.startGame(fileName);
		}
	}
}
