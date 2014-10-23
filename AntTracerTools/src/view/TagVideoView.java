/**
 * @author Paul Shen
 * Provides the GUI for opening a video file and tagging it with a group id, name, original resolution/framerate, total duration, and starting ant locations.
 * The information automatically gathered or inputed will be appended to the master video information text file. 
 */

package view;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import controller.ToolController;
import model.Click;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class TagVideoView extends Region {
	
	private ToolController controller;
	
	private MediaPlayer player;
	private MediaView mediaView;
	
	// Video metadata to be collected
	private int groupId;			// derived from last group id recorded in master video info txt file
	private String vName;			// user input
	private String fileName;		// user input
	private int alphaWidth;			// user input
	private int alphaHeight;		// user input
	private float alphaFrameRate;	// user input
	private int maxFrames;			// derived from total duration of video

	// Navigation bar config
	private final int navHeight = 56;
	private Button openVideo;		
	private Label antCountLabel;	// displays number of ants recorded
	private Button undo;			// undo last click(s)
	private Button save;			// once done, save video info and load next video file in queue if there is one or exits
	private Label videoFName;		// displays current video's fileName, also used as default value for video name
	private HBox nav;

	private AnchorPane mediaPlaceholder;	// Placeholder for the media view until the video is opened
	private Text dragHere;
	
	private EventHandler<MouseEvent> clickHandler;
	
	// objects, values used by the game engine
	private Queue<String> videoFiles;
	private int antCount;			// allows user to keep track of number of ants he's traced
	private LinkedList<Click> clicks;
	private LinkedList<Circle> circles;		// circles that correspond to the clicks

	public TagVideoView(ToolController controller) {
		this.controller = controller;
		antCount = 0;
		clicks = new LinkedList<Click>();
		circles = new LinkedList<Circle>();
		videoFiles = new LinkedList<String>(); 
		
		// Media Placeholder is a temporary node that is replaced by the MediaView video display 
		// once a video file is dragged here or opened
		mediaPlaceholder = new AnchorPane();
		dragHere = new Text("Drag Video File(s) Here");
		dragHere.setFont(new Font(24));
		AnchorPane.setTopAnchor(dragHere, 270.0);
		AnchorPane.setLeftAnchor(dragHere, 480.0);
		mediaPlaceholder.getChildren().add(dragHere);
		
		mediaPlaceholder.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
			}
		});
		
		mediaPlaceholder.setOnDragDropped(new EventHandler<DragEvent>(){
			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
	            boolean success = false;
	            if (db.hasFiles()) {
	                success = true;
	                for (File file:db.getFiles()) {
	                	videoFiles.add(file.getAbsolutePath());
//	                	System.out.println(file.getAbsolutePath());
//	                    openVideo(file.getAbsolutePath());
	                }
	            }
	            event.setDropCompleted(success);
	            event.consume();
	            
	            openVideo(videoFiles.remove());
			}
		});
		
		// Navigation bar
		openVideo = new Button("Open Video");
		openVideo.setFocusTraversable(false);
		openVideo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
//				FileChooser fileChooser = new FileChooser();
//				fileChooser.setTitle("Open Video");
////				fileChooser.setInitialDirectory(new File("."));	// doesn't work with windows 7 64-bit
//					 
//				File tmp = fileChooser.showOpenDialog(null);
//				if (tmp != null) openVideo(tmp.getAbsolutePath());
				openVideo("");
			}
		});
		
		antCountLabel = new Label("Ant Count: " + antCount);
		antCountLabel.setMinWidth(50);
		
		undo = new Button("Undo (Ctrl-Z)");
		undo.setFocusTraversable(false);
		undo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				undo();
			}
		});
		
		save = new Button("save");
		save.setFocusTraversable(false);
		save.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				save();
			}
		});
		
		videoFName = new Label("Current video: ");
		videoFName.setFocusTraversable(false);
		
		// Assemble navigation bar
		nav = new HBox();
		nav.getChildren().addAll(openVideo, antCountLabel, undo, save, videoFName);
		HBox.setMargin(openVideo, new Insets(10, 0, 0, 5));
		HBox.setMargin(antCountLabel, new Insets(10, 0, 0, 5));
		HBox.setMargin(undo, new Insets(10, 0, 0, 5));
		HBox.setMargin(save, new Insets(10, 0, 0, 5));
		HBox.setMargin(videoFName, new Insets(10, 0, 0, 5));
		
		getChildren().add(mediaPlaceholder);
		getChildren().add(nav);
		
		clickHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() == MouseButton.PRIMARY) {
					double x = event.getX();
					double y = event.getY();
					
					Circle circle = new Circle(x, y, 25, Color.web("white", 0.05));
					circle.setStrokeType(StrokeType.OUTSIDE);
					circle.setStroke(Color.web("blue", 0.5));
					circle.setStrokeWidth(2);
					circle.setOnMouseClicked(clickHandler);
					TagVideoView.this.getChildren().add(circle);
					
					antCount++;
					antCountLabel.setText("Ant Count: " + antCount);
					clicks.add(new Click(1, x, y));
					circles.add(circle);
				}
				else if (event.getButton() == MouseButton.SECONDARY) {	// allows for deletion of previous clicks
				
				}
			}
		};
		
		
		// Create and add keyboard handler to the whole scene	
		this.setFocusTraversable(true);
		this.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if ((event.getCode().equals(KeyCode.Z)) && event.isControlDown()) {		// Ctrl-Z
                    undo();
                }
			}
			
		});
	}
	
	@Override
	protected void layoutChildren() {
		mediaPlaceholder.resizeRelocate(0, 0, 960, 540);
		nav.resizeRelocate(0, 540, 960, navHeight);
	}
	
	@Override
	protected double computePrefWidth(double width) {
		return 960;
	}
	
	@Override
	protected double computePrefHeight(double height) {
		return 540 + navHeight;
	}
	
	public void play() {
		Status status = player.getStatus();
		if (status == Status.UNKNOWN || status == Status.HALTED) {
			return;
		}
		if (status == Status.PAUSED || status == Status.STOPPED || status == Status.READY) {
			player.play();
		}
	}
	
	/**
	 * Opens file explorer for user to open the video in this GUI
	 */
	public void openVideo(String path) {
		// replace back slashes in Windows file path with forward slashes to conform to RFC 2396 naming scheme
		path = "file:/" + path.replace('\\', '/');
		
		// strip file name from path
		fileName = path.substring(path.lastIndexOf('/') + 1);
		videoFName.setText("Current video: " + fileName);
		
		// replace spaces with URL encoded %20 to conform to RFC 2396
		path = path.replace(" ", "%20");
		
		// Create the media view and replace placeholder
		player = new MediaPlayer(new Media(path));
//		player.setAutoPlay(false);
		player.pause();
		mediaView = new MediaView(player);
		getChildren().remove(mediaPlaceholder);
		getChildren().add(mediaView);
		mediaView.resizeRelocate(0, 0, 960, 540);
		
		player.setOnReady(new Runnable() {
			@Override
			public void run() {
				maxFrames = (int) player.getTotalDuration().toSeconds() + 1;	// click on the first frame + click for every second
			}
		});
		
		// Setup the media view
		mediaView.setOnMouseClicked(clickHandler);	
	}
	
	/**
	 * Saves video information by appending to the end of the master video information text file.
	 */
	public void save() {
		// Saves video information to text file
		for (Click c: clicks) {
			if (clicks.indexOf(c)%10 == 0) {	// print new-line every 10 clicks
				System.out.print("\n");
			}
			System.out.print(((int) c.getX()) + " " + ((int) c.getY()) + ",");
		}
		if (clicks.size()%10 == 0) System.out.print("\n");
		System.out.print("$");
		
		// Determines if there are more files in the queue
		if (videoFiles.peek() != null) {
			antCount = 0;
			antCountLabel.setText("Ant Count: " + antCount);
			clicks = new LinkedList<Click>();
			circles = new LinkedList<Circle>();
			openVideo(videoFiles.remove());
		}
		// Or Exits
	}
	
	public void undo() {
		if (clicks.isEmpty() || circles.isEmpty()) return;
		clicks.removeLast();
		getChildren().remove(circles.getLast());
		circles.removeLast();
		
		antCount--;
		antCountLabel.setText("Ant Count: " + antCount);
	}
	
	private class InfoPrompt extends Stage {
		public InfoPrompt() {
			initModality(Modality.WINDOW_MODAL);
			
			// Setup the response buttons
			VBox inputLabels = VBoxBuilder.create().children(new Label("Video Name:"), new Label("File Name/URL:")).
					alignment(Pos.CENTER_RIGHT).spacing(5).build();
			
			final TextField vNameInput = new TextField();
			final TextField fNameInput = new TextField();
			VBox inputFields = VBoxBuilder.create().children(vNameInput, fNameInput).alignment(Pos.CENTER_LEFT).spacing(5).build();
			
			setScene(new Scene(HBoxBuilder.create().children(inputLabels, inputFields).
			    alignment(Pos.CENTER).padding(new Insets(5)).build()));
			setOnCloseRequest(new EventHandler<WindowEvent>() {		// closing dialog taken as a 'no'
				@Override
				public void handle(WindowEvent event) {
					vName = vNameInput.getText();
					fileName = fNameInput.getText();
				}
			});
		}
	}

}
