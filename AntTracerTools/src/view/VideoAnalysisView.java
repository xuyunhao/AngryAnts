/**
 * @author Paul Shen
 * 
 * Displays and handles user input in the game component for normal play mode.
 * 
 * Notes (for unorthodox design and workarounds): 
 * - currFrameNum starts at 1 and the video starts at 0 seconds.
 * - pauses after each second in the video are handled by manually setting the start and stop time of the video
 *   of the 1 second segment of video, so the video technically stops and then restarts at the next start time  
 * - pausing when seeking in the video is handled in the same fashion but the start and stop time are the same
 */

package view;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import model.Ant;
import model.Click;
import model.Path;
import model.Video;
import model.Behavior;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SeparatorMenuItemBuilder;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.util.StringConverter;
import controller.ToolController;

public class VideoAnalysisView extends Region {

	private static ToolController controller;
	
	// video information, both inherent and derived
	private final Video video;
	private final int hOffset;
	private final int maxFrames;
	
	private MediaPlayer player;
	private MediaView mediaView;

	private final int navHeight = 56;
	private Button vidSelect;
	private Button prev;
	private Button next;
	private Button restart;
	private TextField frameNum;
	private Slider speedSlider;
	private VBox slider;
	private ComboBox<Integer> antSelect;
	private TextField opacityField;
	private TextField lineThicknessField;
	private HBox nav;
	
	private final EventHandler<KeyEvent> keyHandler;
	private Runnable pauseHandler;
	
	// objects, values used by the game engine
	private Queue<String> videoFiles;
	private LinkedList<Path> paths;
	private final int frameRate = 50;
	private Ant currAnt = null;
	private double opacity;
	private double lineThickness;
	
	private Path currPath;
	private int currFrameNum = 1;
	private Line line = null;				// drawn from dragging
	private Circle circle = null;			// circle drawn around clicks
	private boolean isPaused = true;		// pause status independent of JavaFX STATUS
	private boolean isSeeking = false;		// indicates seeking to a previously used frame
	private boolean isSliderAdjusted = false;	// indicates slider was used while playing
	private boolean isInstantReplay = false;	// indicates instant replay has started
	private boolean isAdvanced = false;		// indicates game mode is basic (click) or advanced (drag, behavior menu)
	
	@SuppressWarnings("static-access")
	public VideoAnalysisView(ToolController controller, Video video) {
		this.controller = controller;
		this.video = video;
		
		// video.getMaxFrames() = number of frames to be DISPLAYED in game
		// (number of seconds in video)*(frames per second) = total frames in video
		maxFrames = (video.getMaxFrames() - 1)*frameRate + 1;
		
		// the offset from the left in which the video pane lies in the parent Region using the video's dimensions scaled to a height of 540
		hOffset = 480 - video.getAlphaWidth()*540/video.getAlphaHeight()/2;	
		paths = new LinkedList<Path>();

		// Setup mediaView, the node in which the video is played
		// Read local video file from jar or 'bin/' folder where .class files are
		Media media;
		if (getClass().getResource("/vid/" + video.getFileName()) != null) {
			media = new Media(getClass().getResource("/vid/" + video.getFileName()).toString());
		}
		else {
			media = new Media(video.getFileName());
		}
		player = new MediaPlayer(media);
		player.setAutoPlay(false);
		mediaView = new MediaView(player);

		// Wait for streaming video to load to draw the starting circle
		player.setOnReady(new Runnable() {
			@Override
			public void run() {

			}
		});
		
		// Handle pauses in video
		pauseHandler = new Runnable() {
			@Override
			public void run() {
				opacity = Double.parseDouble(opacityField.getText());
				lineThickness = Double.parseDouble(lineThicknessField.getText());
				
				Color[] colors = {Color.RED, Color.BLUE, Color.ORANGE, Color.GREEN, Color.BLACK, Color.PINK, Color.TEAL, Color.YELLOW, 
						Color.POWDERBLUE}; // !!! http://docs.oracle.com/javafx/2/api/javafx/scene/paint/Color.html
				
				// Generate or regenerate each existing path for the currently selected ant up to and including the current frame
				if (currAnt != null) {
					// Remove previous path renderings
					List<Node> toRemove = new LinkedList<Node>();
					for (Node child : VideoAnalysisView.this.getChildren()) {
						if (child instanceof javafx.scene.shape.Path) {
							toRemove.add(child);
						}
					}
					for (Node dispose : toRemove) {
						VideoAnalysisView.this.getChildren().remove(dispose);
					}
					
					int i = 0;
					double x1, y1, x2, y2;
					
					for (Path p : currAnt.getCompleted()) {
						Color currColor = colors[i];
						javafx.scene.shape.Path path = new javafx.scene.shape.Path();
						path.setStroke(currColor);
						path.setOpacity(opacity);
						path.setStrokeWidth(lineThickness);
						path.setSmooth(true);
						path.getElements().add(new MoveTo(p.getPath().get(0).getX(), p.getPath().get(0).getX()));
						
						for (int j = 1; j < p.size(); j++) {
							if (p.getPath().get(j).getFrameNum() > currFrameNum) break;	// only generate path up to current frame
							
							x1 = p.getPath().get(j-1).getX();
							y1 = p.getPath().get(j-1).getY();
							x2 = p.getPath().get(j).getX();
							y2 = p.getPath().get(j).getY();
							path.getElements().add(new MoveTo(x1, y1));
							path.getElements().add(new LineTo(x2, y2));
						}
						
						path.getElements().add(new ClosePath());
						VideoAnalysisView.this.getChildren().add(path);
						i++;
					}
				}
			}
		};
		player.setOnEndOfMedia(pauseHandler);
		
		// Setup navigation buttons
		// Back to video selection button
		vidSelect = new Button("Reselect Video");
		vidSelect.setPrefSize(100, 36);
		vidSelect.setFocusTraversable(false);	// keyboard shortcuts only work if nothing has focus
		vidSelect.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				VideoAnalysisView.controller.reselectVideo(VideoAnalysisView.this);
			}	
		});

		Label seekInstructions = new Label("Input Frame Number and Press Enter ->");
		seekInstructions.setPrefHeight(36);		
		
		// Current and total frame count display
		frameNum = new TextField(currFrameNum + "/" + maxFrames);
		frameNum.setPrefColumnCount(7);		// <3-digit space>/<3-digit space>
		frameNum.setAlignment(Pos.CENTER);
//		frameNum.setPrefSize(80, 35);
		frameNum.setPrefSize(70, 36);
		frameNum.setFocusTraversable(false);
//		frameNum.setDisable(true);		// disable interaction, display only
//		frameNum.setOpacity(1);			// but should still be fully visible
		frameNum.setOnAction(new EventHandler<ActionEvent>() {			// On press of "Enter" key
			@Override
			public void handle(ActionEvent arg0) {
				seek();
			}
		});
		frameNum.setOnMouseClicked(new EventHandler<MouseEvent>() {		// Select all text on click for quicker input
			@Override
			public void handle(MouseEvent event) {
				frameNum.selectAll();
			}	
		});
		
		Label opacityLabel = new Label("Opacity (0.0-1.0):");
		opacityLabel.setPrefHeight(36);
		
		opacity = 0.6;
		opacityField = new TextField("0.6");
		opacityField.setPrefColumnCount(3);
		opacityField.setAlignment(Pos.CENTER);
		opacityField.setPrefHeight(36);
		opacityField.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					opacity = Double.parseDouble(opacityField.getText());
				} catch (NumberFormatException nfe) {
					
				}
			}	
		});
		opacityField.setOnMouseClicked(new EventHandler<MouseEvent>() {		// Select all text on click for quicker input
			@Override
			public void handle(MouseEvent event) {
				opacityField.selectAll();
			}	
		});
		
		Label lineThicknessLabel = new Label("Line Thickness:");
		lineThicknessLabel.setPrefHeight(36);
		
		lineThickness = 6;
		lineThicknessField = new TextField("6.0");
		lineThicknessField.setPrefColumnCount(4);
		lineThicknessField.setAlignment(Pos.CENTER);
		lineThicknessField.setPrefHeight(36);
		lineThicknessField.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					lineThickness = Double.parseDouble(lineThicknessField.getText());
				} catch (NumberFormatException nfe) {
					
				}
			}	
		});
		lineThicknessField.setOnMouseClicked(new EventHandler<MouseEvent>() {		// Select all text on click for quicker input
			@Override
			public void handle(MouseEvent event) {
				lineThicknessField.selectAll();
			}	
		});
		
		Label antSelectLabel = new Label("Current ant id:");
		antSelectLabel.setPrefHeight(36);
		
		antSelect = new ComboBox<Integer>();
		for (Ant a : video.getAnts()) {
//			antSelect.getItems().add(Integer.toString(a.getId()));
			antSelect.getItems().add(a.getId());
		}
		antSelect.setPrefHeight(36);
		antSelect.valueProperty().addListener(new ChangeListener<Integer>() {

			@Override
			public void changed(ObservableValue<? extends Integer> ov,
					Integer t, Integer t1) {
				for (Ant a : VideoAnalysisView.this.video.getAnts()) {
					if (a.getId() == t1) {
						currAnt = a;
						break;
					}
				}
			}
			
		});
		
		// !!! TO ADD
		// Add color picker to accumulate colors for paths
		
		// Assemble navigation bar
		nav = new HBox();
		nav.getChildren().addAll(vidSelect, antSelectLabel, antSelect, seekInstructions, frameNum, opacityLabel, opacityField,
				lineThicknessLabel, lineThicknessField);
		HBox.setMargin(vidSelect, new Insets(10, 0, 10, 5));
//		nav.setMargin(prev, new Insets(8, 0, 0, 265));
//		nav.setMargin(prev, new Insets(8, 0, 0, 165));
		HBox.setMargin(antSelectLabel, new Insets(10, 0, 0, 5));
		HBox.setMargin(antSelect, new Insets(10, 0, 0, 5));
		HBox.setMargin(seekInstructions, new Insets(10, 0, 0, 5));
		HBox.setMargin(frameNum, new Insets(10, 0, 0, 5));
		HBox.setMargin(opacityLabel, new Insets(10, 0, 0, 5));
		HBox.setMargin(opacityField, new Insets(10, 0, 0, 5));
		HBox.setMargin(lineThicknessLabel, new Insets(10, 0, 0, 5));
		HBox.setMargin(lineThicknessField, new Insets(10, 0, 0, 5));
//		nav.setMargin(next, new Insets(8, 0, 0, 5));
		
		// Final setup of all containers
//		view = new Scene(root, 1024, 631);
		getChildren().add(mediaView);
		getChildren().add(nav);
		
		// Create and add keyboard handler to the whole scene
		keyHandler = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ESCAPE) {
				}
			
				if (event.getCode() == KeyCode.RIGHT) {
					if (currFrameNum < currPath.size()) {	// any frame except foremost one
						seekNext();
					}
					// this case handles IndexOutOfBounds exception when holding down right button till
					// the foremost frame (seeking does not update to false fast enough)
					else if (currFrameNum == currPath.size()) {
						isSeeking = false;
						seekNext();
					}
				}
//				else if (event.getCode() == KeyCode.ENTER) {
//					seek();
//				}
				else if (event.getCode() == KeyCode.LEFT) {
					if (currFrameNum != 1) {	// any frame except 1st one
						seekPrev();
					}
				}
				else if (event.getCode() == KeyCode.SPACE) {				
//					System.out.println(player.getStatus());
//					System.out.println(player.getCurrentTime().toSeconds());
				}
				else if (event.getCode() == KeyCode.S) {
					currFrameNum = maxFrames - 1;
				}
			}
		};
		// must be focus traversable for key events to bubble from parent Scene to this descendant Region
		this.setFocusTraversable(true);
		this.addEventHandler(KeyEvent.KEY_PRESSED, keyHandler);
		
//		mediaView.setCursor(Cursor.CROSSHAIR);	// crosshair is transparent for some reason..
		// Add ability to drop path data files onto media view
		mediaView.setOnDragOver(new EventHandler<DragEvent>() {
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
		
		mediaView.setOnDragDropped(new EventHandler<DragEvent>(){
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
	            
	            importPath(videoFiles.remove());
			}
		});
	}
	
	@Override
	protected void layoutChildren() {
		mediaView.relocate(hOffset, 0);
		mediaView.setFitWidth(960);
		mediaView.setFitHeight(540);
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
	
	public Video getVideo() {
		return video;
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
	 * Import path from file.
	 */
	public void importPath(String path) {
		// replace back slashes in Windows file path with forward slashes to conform to RFC 2396 naming scheme
		path = "file:/" + path.replace('\\', '/');
		
		// strip file name from path
//		String fileName = path.substring(path.lastIndexOf('/') + 1);
		
		// replace spaces with URL encoded %20 to conform to RFC 2396
		path = path.replace(" ", "%20");
		
		switch (paths.size()) {
		case 0:		// game derived data imported first
			
			break;
		case 1:
			break;
		case 2:
			break;
		}
	}
	
	/**
	 * Seeks to frame in frame number field into the video and generates each separate path up that frame.
	 */
	public void seek() {
		String seekFrame = frameNum.getText(); 
		double seekTime = -1;
		
		if (frameNum.getText() != null) {
			try {
				currFrameNum = Integer.parseInt(frameNum.getText());
				seekTime = (Double.parseDouble(frameNum.getText()) - 1)/frameRate;
			} catch (NumberFormatException nfe) {
				frameNum.setText(currFrameNum + "/" + maxFrames);	// reset frame display to previous value
				return;
			}
		}
		else { return; }
		
		frameNum.setText(seekFrame + "/" + maxFrames);
		if (currFrameNum == maxFrames) seekTime = seekTime*1000 - 1;
		else seekTime *= 1000;
		player.setStartTime(new Duration(seekTime));
		player.setStopTime(new Duration(seekTime));
		play();	
	}
	
	public void seekNext() {
//		if (circle != null) this.getChildren().remove(circle);
//		for (Node child : GameView.this.getChildren()) {
//			if (child instanceof Circle) {
//				GameView.this.getChildren().remove(child);
//				break;
//			}
//		}
		
//		if (currFrameNum <= currPath.size()) {
			isSeeking = true;
//		}
		
		// Jump seek by setting start and stop time to destination time and playing
		player.setStartTime(new Duration((currFrameNum)*1000));
		player.setStopTime(new Duration((currFrameNum)*1000));
		play();
		
		// Update the navigation panel
		currFrameNum++;
		prev.setDisable(false);   // should be able to seek previous frame after seeking next
		if (currFrameNum > currPath.size()) {
			next.setDisable(true);   // user must click to advance, no peeking ahead in video   
		}
		frameNum.setText(currFrameNum + "/" + maxFrames);
	}
	
	public void seekPrev() {
//		for (Node child : GameView.this.getChildren()) {
//			if (child instanceof Circle) {
//				GameView.this.getChildren().remove(child);
//				break;
//			}
//		}
		
//		if (circle != null) this.getChildren().remove(circle);
		isSeeking = true;
		player.setStartTime(new Duration((currFrameNum-2)*1000));
		player.setStopTime(new Duration((currFrameNum-2)*1000));
		play();
		
		// Update the navigation panel
		currFrameNum--;
		next.setDisable(false);   // should be able to seek next frame after seeking previous
		if (currFrameNum == 1) {   // no frames before 1st one
			prev.setDisable(true);
		}
		frameNum.setText(currFrameNum + "/" + maxFrames);
	}
	
	/**
	 * Seek next but play the video till next frame
	 */
	public void playTillNextFrame() {
		isSeeking = true;
		player.setStartTime(new Duration((currFrameNum-1)*1000));
		player.setStopTime(new Duration((currFrameNum)*1000));
		play();
		isPaused = false;
		
		// Update the navigation panel
		currFrameNum++;
		prev.setDisable(false);   // should be able to seek previous frame after seeking next
		if (currFrameNum > currPath.size()) {
			next.setDisable(true);   // user must click to advance, no peeking ahead in video   
		}
		frameNum.setText(currFrameNum + "/" + maxFrames);
	}
	
}
