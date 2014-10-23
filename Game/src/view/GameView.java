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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import model.Click;
import model.Path;
import model.Video;
import model.Behavior;
import javafx.animation.FadeTransition;
import javafx.animation.FadeTransitionBuilder;
import javafx.animation.Timeline;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.media.Media;
import javafx.scene.media.MediaMarkerEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.util.StringConverter;
import controller.AntController;

public class GameView extends Region {

	private AntController controller;
	
	// video information, both inherent and derived
	private final LinkedList<Video> videos;
	private Video video;
	private final int hOffset;
	private int maxFrames;
	private int vidIndex;
	private int antIndex;
	
	private Media media;
	private MediaPlayer player;
	private MediaView mediaView;
	private AnchorPane mediaPlaceholder;	// Displays loading indicator while video is downloaded

	private final int navHeight = 56;
	private Button prev;
	private Button next;
	private Button restart;
	private TextField frameNum;
	private Slider speedSlider;
	private VBox slider;
	private HBox nav;
	private Button playAgain;
	
//	private ProgressIndicator vLoadProgress;
	private BehaviorPopupMenu popupMenu;
	
	private EventHandler<MouseEvent> clickHandler;
	private EventHandler<MouseEvent> dragStartHandler;
//	private EventHandler<MouseEvent> dragDetectedHandler;
	private EventHandler<MouseEvent> draggedHandler;
	private EventHandler<MouseEvent> dragReleasedHandler;
	private final EventHandler<KeyEvent> keyHandler;
	private Runnable pauseHandler;
	private Runnable readyHandler;
	
	// Visual effects
	private Line line = null;				// drawn from dragging
	private Circle startCircle = null;
	private Circle circle = null;			// circle drawn around clicks
	private FadeTransition circleFade;
	private ProgressIndicator vLoadProgress;
	
	// objects, values used by the game engine
	private Duration duration;
	private Path currPath;
	private int currFrameNum = 1;
	private boolean isPaused = true;		// pause status independent of JavaFX STATUS
	private boolean isSeeking = false;		// indicates seeking to a previously used frame
	private boolean isSliderAdjusted = false;	// indicates slider was used while playing
	private boolean isInstantReplay = false;	// indicates instant replay has started
	private boolean isAdvanced = false;		// indicates game mode is basic (click) or advanced (drag, behavior menu)
	private boolean isBuffering = false;
	
	@SuppressWarnings("static-access")
	public GameView(AntController controller) throws Exception {
		this.controller = controller;
		
		// Receive the assigned <video id, ant #> pair from server
		int n = receiveAntAssign();
		int numAnts = 10;
		int numVids = 3;
//		vidIndex = -1;
//		antIndex = -1;
		if (n > -1) {
			vidIndex = (n/numAnts)%numVids;
			antIndex = n%numAnts;
		}
		else {
			throw new Exception("Starting <video id, ant #> pair could not be received from server.\n");
		}
		
		// Select the assigned video from list of videos, initiate associated data
		videos = controller.getVideos();
		this.video = videos.get(vidIndex);
		maxFrames = video.getMaxFrames();
		// the offset from the left in which the video pane lies in the parent Region using the video's dimensions scaled to a height of 540
		hOffset = 480 - video.getAlphaWidth()*540/video.getAlphaHeight()/2;
        
        // Show progress indicator while video loads
		mediaPlaceholder = new AnchorPane();
		vLoadProgress = new ProgressIndicator();
		vLoadProgress.setPrefSize(50, 50);
		AnchorPane.setTopAnchor(vLoadProgress, 245.0);
		AnchorPane.setLeftAnchor(vLoadProgress, 455.0);
		mediaPlaceholder.getChildren().add(vLoadProgress);
		
		// Setup mediaView, the node in which the video is played
		// Read local video file from jar or 'bin/' folder where .class files are
		System.out.println("Video name: " + video.getName());
		System.out.println("Ant ID: " + video.getAnts().get(antIndex).getId() + "\n");
//		if (getClass().getResource("/vid/" + video.getFileName()) != null) {
			media = new Media(getClass().getResource("/vid/" + video.getFileName()).toString());
//		}
//		else {
//			media = new Media(video.getFileName());
//		}
		player = new MediaPlayer(media);
		player.setAutoPlay(false);
		mediaView = new MediaView(player);
		
		media.setOnError(new Runnable() {
			@Override
			public void run() {
				System.out.println("Error in Media");
				System.out.println("Error message: " + media.getError().getMessage());
				System.out.println("Localized error message: " + media.getError().getLocalizedMessage());
			}
		});
		
		player.setOnError(new Runnable() {
			@Override
			public void run() {
				System.out.println("Error in MediaPlayer");
				System.out.println("Error message: " + player.getError().getMessage());
				System.out.println("Localized error message: " + player.getError().getLocalizedMessage());
			}
		});

		// Wait for streaming video to load to draw the starting circle
		readyHandler = new Runnable() {
			@Override
			public void run() {
				duration = media.getDuration();
				//System.out.println(duration);
				
				// Randomly select starting ant and draw red circle around it
				currPath = new Path(GameView.this.video.getAnts().get(antIndex), GameView.this.video.getName());
				
				double x, y;
				x = GameView.this.video.getAnts().get(antIndex).getStart().getX();
				y = GameView.this.video.getAnts().get(antIndex).getStart().getY();
				startCircle = new Circle(x + hOffset, y, 25, Color.web("white", 0.05));
				startCircle.setStrokeType(StrokeType.OUTSIDE);
				startCircle.setStroke(Color.web("red", 0.9));
				startCircle.setStrokeWidth(2);
				
				startCircle.addEventHandler(MouseEvent.MOUSE_PRESSED, dragStartHandler);
				startCircle.addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);
				startCircle.addEventHandler(MouseEvent.MOUSE_RELEASED, dragReleasedHandler);
				startCircle.setOnMouseClicked(clickHandler);
				
				// Setup the fade effect for the start circle
				circleFade = FadeTransitionBuilder.create()
			            .duration(Duration.seconds(1))
			            .node(startCircle)
			            .fromValue(1)
			            .toValue(0.1)
			            .cycleCount(1)
			            .autoReverse(false)
			            .build();
				
				circleFade.setOnFinished(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent arg0) {
						GameView.this.getChildren().remove(startCircle);
						startCircle = null;
					}
				});
				
				// Buffer loading indicator will disappear after 1 second has been buffered
//				player.bufferProgressTimeProperty().addListener(new ChangeListener<Duration>() {
//					@Override
//					public void changed(ObservableValue<? extends Duration> obs, Duration oldVal, Duration newVal) {
//						if (isBuffering && (newVal.greaterThanOrEqualTo(new Duration((currPath.size()+3)*1000)))) {
//							GameView.this.getChildren().remove(vLoadProgress);
//							System.out.println("BUFFERED!!!");
//							System.out.println(player.getBufferProgressTime() + " > " + ((currPath.size()+2)*1000));
//							isBuffering = false;
//							return;
//						}
//						else if (newVal.equals(duration)) {	// discard listener, whole video has been buffered
//							isBuffering = false;
//							player.bufferProgressTimeProperty().removeListener(this);
//						}
//					}
//				});
				
				// Don't display the video portion of the game until everything else is ready
				restart.setDisable(false);
				GameView.this.getChildren().remove(mediaPlaceholder);
				GameView.this.getChildren().add(mediaView);
				GameView.this.getChildren().add(startCircle);
				mediaView.relocate(hOffset, 0);
				mediaView.setFitWidth(960);
				mediaView.setFitHeight(540);
				
//				int tmp = (currPath.size() + 2)*1000;
//				System.out.println(player.getBufferProgressTime() + " < " + tmp);
				// Progress indicator goes
//				Duration bufferTime = player.getBufferProgressTime();
//				if (bufferTime.lessThan(new Duration((currPath.size()+3)*1000)) && !bufferTime.equals(duration)) {
//					System.out.println("BUFFERING");
//					isBuffering = true;
//					GameView.this.getChildren().add(vLoadProgress);
//					vLoadProgress.relocate(455, 245);
//				}
			}
		};
		player.setOnReady(readyHandler);
		
		// Handle pauses in video
		pauseHandler = new Runnable() {
			@Override
			public void run() {
				isPaused = true;
				
				// need at least 1 second in buffer to play continuously
//				int tmp = (currPath.size() + 2)*1000;
//				System.out.println(player.getBufferProgressTime() + " < " + tmp);
//				Duration bufferTime = player.getBufferProgressTime();
//				if (!bufferTime.equals(duration) && !isBuffering && bufferTime.lessThan(new Duration((currPath.size()+3)*1000))) {
//					System.out.println("BUFFERING");
//					isBuffering = true;
//					getChildren().add(vLoadProgress);
//					vLoadProgress.relocate(455, 245);
//				}
				
				// Draw circle after seek.  When seeking with click, drawing will happen after pause
				if (isSeeking) {
					// Remove previous circle if necessary
					if (circle != null) {
						GameView.this.getChildren().remove(circle);
						circle = null;
					}
					
					if (currFrameNum <= currPath.size()) {	// only draw a circle for frames that have been clicked
						double x, y;
						x = currPath.getClick(currFrameNum - 1).getX();
						y = currPath.getClick(currFrameNum - 1).getY();
						circle = new Circle(x + hOffset, y, 25, Color.web("white", 0.05));
						circle.setStrokeType(StrokeType.OUTSIDE);
						circle.setStroke(Color.web("blue", 0.5));
						circle.setStrokeWidth(2);
						circle.setOnMouseClicked(clickHandler);
						GameView.this.getChildren().add(circle);
					}
					isSeeking = false;
				}
				
				if (isSliderAdjusted) {		// slider adjustment during play takes effect on pause
					Duration currTime = player.getStartTime();	
					player.setRate(Math.pow(2, speedSlider.getValue()));
					player.seek(currTime);		
					GameView.this.requestFocus();	
					circleFade.setDuration(new Duration(1000/player.getRate()));
					isSliderAdjusted = false;
				}
			}
		};
		player.setOnEndOfMedia(pauseHandler);
		
		// Create drag mechanic for drawing line over ant
		line = null;
		
		// Mouse button has been pressed down
		dragStartHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
//				System.out.println("Click to drag coords: " + event.getX() + ", " + event.getY());
				if (isPaused && !isBuffering) {
					if (isAdvanced && event.getButton() == MouseButton.PRIMARY && currFrameNum > currPath.size()) {
							// create line at a single point
							line = new Line(event.getX() + hOffset, event.getY(), event.getX() + hOffset, event.getY());
							line.setStrokeWidth(3);
							GameView.this.getChildren().add(line);
					}
				}
			}
		};
		mediaView.addEventHandler(MouseEvent.MOUSE_PRESSED, dragStartHandler);
		
		// Mouse has been moved while pressed down
		draggedHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// move end point of line to position of mouse cursor
				if (line != null) {
					line.setEndX(event.getX() + hOffset);
					line.setEndY(event.getY());
				}
			}
		};
//		mediaView.setOnMouseDragged(draggedHandler);
		mediaView.addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);
		
		// Mouse button has been released after entering a drag state (press + move)
		dragReleasedHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// if line exists, then we're handling a drag, not just any mouse release
				// also make sure that the line isn't of length 0
				if (line != null && !(line.getStartX() == (event.getX()+hOffset) && line.getStartY() == event.getY())) {
					if (event.getButton() == MouseButton.PRIMARY) {
						// Remove starting ant circle
						if (circle != null) {
							GameView.this.getChildren().remove(circle);
							circle = null;
						}
						
						// Calculate the direction the ant is facing as planar angle counter-clockwise from positive x-axis
						double x1, x2, y1, y2, degree;
						x1 = line.getStartX() - hOffset;
						y1 = line.getStartY();
						x2 = line.getEndX() - hOffset;
						y2 = line.getEndY();
						degree = Math.atan((x2 - x1)/(y2 - y1));	
						degree = degree*180 / Math.PI;				// convert from radians to degrees
						degree = 90 - degree;						// adjust from x-axis
						if (y2 < y1) {		// Quadrants 3 & 4
							degree += 180;	// rotate degree so that degree is taken clockwise from x-axis
						}
					
						GameView.this.getChildren().remove(line);
						line = null;
						
						System.out.println("Degree:      " + degree);
						System.out.println("Drag start: (" + x1 + ", " + y1 + ")");
//						System.out.println("Drag done:  (" + x2 + ", " + y2 + ")");
							
						if (currFrameNum == maxFrames) {		// last frame
							// Name path by date, time, and video name
							Calendar cal = Calendar.getInstance();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							currPath.setName(sdf.format(cal.getTime()));
							
							currPath.addClick(new Click(currFrameNum*50 + 1, x1, y1, degree, Behavior.NONE));
							
							// Disable mouse actions
//							System.out.println("disabling mouse events");
//							GameView.this.mediaView.removeEventHandler(MouseEvent.DRAG_DETECTED, dragStartHandler);
//							GameView.this.mediaView.removeEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);
//							GameView.this.mediaView.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);
							
							submitPrompt();
						}
						else if (currFrameNum > currPath.size()) {
							nextFrame();
							// Add click
							currPath.addClick(new Click((currFrameNum-1)*50 + 1, x1, y1, degree, Behavior.NONE));
						}
					}
					else if (event.getButton() == MouseButton.SECONDARY) { // cancel line while dragging
						GameView.this.getChildren().remove(line);
						line = null;
						return;
					}
				}
				else if (event.getButton() == MouseButton.SECONDARY) {		// right click
					if (currFrameNum > 1) { 	// must watch at least 1 section of video to observe behavior
						popupMenu.show(GameView.this, Side.TOP, event.getX() + hOffset, event.getY());
					}
				}
			}
			
		};
//		mediaView.setOnMouseReleased(dragReleasedHandler);
		mediaView.addEventHandler(MouseEvent.MOUSE_RELEASED, dragReleasedHandler);
		
		// Used for hiding behavior popup menu when mouse exits that region and enters underlying 
		// game view
		mediaView.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				popupMenu.hide();
			}
		});
		
		// Create and add mouse eventhandler
		clickHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (isPaused && !isBuffering) {
					if (event.getButton() == MouseButton.PRIMARY) {		// left click
						// Clicking only works if paused
						if (!isAdvanced) {
//							System.out.println("(" + event.getX() + ", " + event.getY() + ")");
							
							if (currFrameNum == maxFrames) {		// last frame
								// Name path by date, time, and video name
								Calendar cal = Calendar.getInstance();
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								currPath.setName(sdf.format(cal.getTime()));
								
								currPath.addClick(new Click((currFrameNum-1)*50 + 1, event.getX() + hOffset, event.getY(), -1, 
										Behavior.NONE));
								
								// Disable mouse actions
//								GameView.this.mediaView.removeEventHandler(MouseEvent.DRAG_DETECTED, dragStartHandler);
//								GameView.this.mediaView.removeEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);
								//GameView.this.mediaView.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
								
	//							submitDialog.show();
								submitPrompt();
							}
							else if (currFrameNum > currPath.size()) {
								if (startCircle != null) {
									circleFade.play();
								}
								
								nextFrame();
								// Add click
								currPath.addClick(new Click((currFrameNum-2)*50 + 1, event.getX() + hOffset, event.getY(), -1, 
										Behavior.NONE));
							}
							else {		// seeking on previously clicked-on frame
								playTillNextFrame();
							}
						}
					}
					else if (event.getButton() == MouseButton.SECONDARY) {		// right click
						if (currFrameNum > 1) { 	// must watch at least 1 section of video to observe behavior
							popupMenu.show(GameView.this, Side.TOP, event.getX() + hOffset, event.getY());
						}
					}
				}
				
			}
			
		};
		mediaView.setOnMouseClicked(clickHandler);
		
		// Create behavior PopupMenu
		popupMenu = new BehaviorPopupMenu();
		popupMenu.setAutoFix(true);
		//System.out.println(popupMenu.getOwnerNode().isHover());
		
		// Setup navigation buttons		
//		final ToggleButton clickMode = new ToggleButton("Click");
//		final ToggleButton dragMode = new ToggleButton("Drag");
//		clickMode.setDisable(true);
//		clickMode.setPrefSize(60, 36);
//		clickMode.setFocusTraversable(false);
//		clickMode.setOnAction(new EventHandler<ActionEvent>() {
//			@Override
//			public void handle(ActionEvent arg0) {
//				isAdvanced = false;
//				dragMode.setDisable(false);
//				clickMode.setDisable(true);
//			}
//		});
//		dragMode.setFocusTraversable(false);
//		dragMode.setPrefSize(60, 36);
//		dragMode.setOnAction(new EventHandler<ActionEvent>() {
//			@Override
//			public void handle(ActionEvent arg0) {
//				isAdvanced = true;
//				clickMode.setDisable(false);
//				dragMode.setDisable(true);
//			}
//		});
//        ToggleGroup group = new ToggleGroup();
//        clickMode.setToggleGroup(group);
//        dragMode.setToggleGroup(group);
//        group.selectToggle(clickMode);
		
		// Seek previous frame button
		Image tempImg = new Image(getClass().getResourceAsStream("/img/arrow_left.png"), 50, 36, false, true);
		prev = new Button();
		prev.setGraphic(new ImageView(tempImg));
		prev.setTooltip(new Tooltip("Previous Frame"));
		prev.setDisable(true);		// can't seek previous frame on 1st frame
		prev.setFocusTraversable(false);
		prev.setPrefSize(70, 36);
		prev.setMaxWidth(70);
//		prev.setPrefSize(45, 35);
		prev.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				seekPrev();
			}
		});
		
		// Current and total frame count display
		frameNum = new TextField(currFrameNum + "/" + maxFrames);
		frameNum.setPrefColumnCount(7);		// <3-digit space>/<3-digit space>
		frameNum.setAlignment(Pos.CENTER);
//		frameNum.setPrefSize(80, 35);
		frameNum.setPrefSize(70, 36);
		frameNum.setFocusTraversable(false);
		frameNum.setDisable(true);		// disable interaction, display only
		frameNum.setOpacity(1);			// but should still be fully visible
		
		// Seek next frame button
		tempImg = new Image(getClass().getResourceAsStream("/img/arrow_right.png"), 50, 36, false, true);
		next = new Button();
		next.setGraphic(new ImageView(tempImg));
		next.setTooltip(new Tooltip("Next Frame"));
		next.setDisable(true);		// until there's a click on the next frame, can't seek there
		next.setFocusTraversable(false);
		next.setPrefSize(70, 36);
		next.setMaxWidth(70);
//		next.setPrefSize(45, 35);
		next.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				seekNext();
			}
		});
		
		// Restart video button
		tempImg = new Image(getClass().getResourceAsStream("/img/restart.png"), 36, 36, false, true);
		restart = new Button();
		restart.setDisable(true);
		restart.setGraphic(new ImageView(tempImg));
		restart.setTooltip(new Tooltip("Restart Video"));
		restart.setFocusTraversable(false);
		restart.setPrefSize(36, 36);
		restart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				restart();
			}
		});
		
		// Setup playback speed slider.  The value of the slider, x, represents a playspeed of 
		// 2^x.  The range of speed is then (1/4x - 8x)
		speedSlider = new Slider(-2, 3, 0);
		speedSlider.setMajorTickUnit(1.0);
		speedSlider.setSnapToTicks(true);
		speedSlider.setBlockIncrement(1.0);
		speedSlider.setFocusTraversable(false);
		speedSlider.setPrefWidth(180);
		
		// Axis of formatted speed values that accompanies the tick marks on the video speed slider
		NumberAxis tickLabel = new NumberAxis(-2.0, 3.0, 1.0);
		tickLabel.setPrefWidth(180.0);
		tickLabel.setMinorTickCount(0);
		tickLabel.setMaxWidth(180);
		tickLabel.setTickLabelGap(1);					// vertical distance between tick marks and labels
		tickLabel.setTickLength(8);						// vertical length of tick marks from axis
		tickLabel.setTickLabelFormatter(new StringConverter<Number>() {
			@Override
			public Number fromString(String str) {
				Double temp = Double.parseDouble(str.substring(0, str.length() - 1));
				return Math.log(temp) / Math.log(2.0);
			}

			@Override
			public String toString(Number val) {
				Double x = (Double) val;
				
				if (x == -2) {
//					return "1/4x";
					return ".25x";
				}
				else if (x == -1) {
//					return "1/2x";
					return ".5x";
				}
				else if (x == 0) {
					return "1x";
				}
				else if (x == 1) {
					return "2x";
				}
				else if (x == 2) {
					return "4x";
				}
				else if (x == 3) {
					return "8x";
				}
				
				return "";
			}
		});
		
		// Handles adjustment to the video speed slider
		speedSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal)
			{
				// slider adjustment will only take effect when video is paused
				if (isPaused && !isInstantReplay) {	
					// record time at pause b/c changing rate after playing started will change position in video
					Duration currTime = player.getStartTime();	
					player.setRate(Math.pow(2, (Double) newVal));
					player.seek(currTime);	// re-adjust video to correct time
					GameView.this.requestFocus();	// transfer focus back to top-level node so key shortcuts work
					circleFade.setDuration(new Duration(1000/player.getRate()));
				}
				else {	// mark slider as adjusted to be handled on next pause
					isSliderAdjusted = true;
				}
			}
		});
		
		slider = new VBox();
		slider.setPrefWidth(180);
		slider.getChildren().addAll(speedSlider, tickLabel);
		slider.setMargin(tickLabel, new Insets(0, 6, 0, 7));	// align tick labels with slider
//		slider.getChildren().addAll(speedSlider);
		
		// Assemble navigation bar
		nav = new HBox();
//		nav.getChildren().addAll(clickMode, dragMode);
		nav.getChildren().addAll(prev, frameNum, next, slider, restart);
//		nav.setMargin(clickMode, new Insets(10, 0, 0, 5));
//		nav.setMargin(dragMode, new Insets(10, 0, 0, 0));
//		nav.setMargin(prev, new Insets(8, 0, 0, 165));
		nav.setMargin(prev, new Insets(8, 0, 0, 370));
		nav.setMargin(frameNum, new Insets(10, 0, 0, 5));
		nav.setMargin(next, new Insets(8, 0, 0, 5));
		nav.setMargin(slider, new Insets(10, 0, 0, 20));
		nav.setMargin(restart, new Insets(8, 5, 0, 110));
		
		// Final setup of all containers
		getChildren().add(mediaPlaceholder);
//		getChildren().add(mediaView);
		getChildren().add(nav);
		
		// Create and add keyboard handler to the whole scene
		keyHandler = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ESCAPE) {
					exitGame();
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
				else if (event.getCode() == KeyCode.LEFT) {
					if (currFrameNum != 1) {	// any frame except 1st one
						seekPrev();
					}
				}
				else if ((event.getCode().equals(KeyCode.Z)) && event.isControlDown()) {		// Ctrl-Z
                    undo();
                }
				else if (event.getCode() == KeyCode.SPACE) {
//					System.out.println(player.getBufferProgressTime());
//					submitPrompt();
					System.out.println(player.getStatus());
//					System.out.println(player.getCurrentTime().toSeconds());
				}
//				else if (event.getCode() == KeyCode.S) {
//					currFrameNum = maxFrames - 1;
//				}
			}
		};
		// must be focus traversable for key events to bubble from parent Scene to this descendant Region
		this.setFocusTraversable(true);
		this.addEventHandler(KeyEvent.KEY_PRESSED, keyHandler);
		
//		mediaView.setCursor(Cursor.CROSSHAIR);	// crosshair is transparent for some reason..
	}
	
	@Override
	protected void layoutChildren() {
		mediaPlaceholder.resizeRelocate(0, 0, 960, 540);
//		mediaView.relocate(hOffset, 0);
//		mediaView.setFitWidth(960);
//		mediaView.setFitHeight(540);
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
	
	/**
	 * Connects to CGI script and accepts a number, n, used to identify the <video #, index of ant in list> pair as
		<(n/x)%y + 1, n%x> where x = # ants to track, y = # video segments
		(e.g.) given x = 10, y = 3 in our case, 
		n = 0: <1, 0>
		n = 1: <1, 1>
		n = 10: <2, 0>
		n = 29: <3, 9> 
		n = 30: <1, 0>
	*/
	private int receiveAntAssign() {
		int n = -1;
		
		try {
			URL fileUrl = new URL("http://cgi.cs.arizona.edu/cgi-bin/projects/angryants/assignAnt.cgi");
			URLConnection url = fileUrl.openConnection();
			url.setUseCaches(false);
			url.setRequestProperty("Content-Type", "text/plain");
			url.setDoInput(true);
	        url.setDoOutput(true);
	        
	        // Read response from script and print
	        BufferedReader inRead = new BufferedReader(new InputStreamReader(url.getInputStream()));
	        
	        n = Integer.parseInt(inRead.readLine());
//	        System.out.println(n);
	        
	        inRead.close();
		}
		catch (MalformedURLException mue) {	
			System.err.println("URL not found");
		}
		catch(IOException ioe) {
			System.err.println("File not found");
			ioe.printStackTrace();
		}
		
		return n;
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
	
	private HBox selectPanel;
	
	/**
	 * Replaces nav bar with submit prompt and buttons to submit or not
	 */
	public void submitPrompt() {
		mediaView.setOnMouseClicked(null); // disabled mouse clicks, only action available is answering prompt
		mediaView.setOnMouseReleased(null);
		mediaView.setOnMouseDragged(null);
		mediaView.setOnMousePressed(null);
		
		Text prompt = new Text("Do You Want to Submit This Path?");
		prompt.setFont(new Font(15));
//		prompt.setPrefHeight(30);
		
		Button yes = new Button("Yes");
		yes.setPrefSize(72, 36);
		yes.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				GameView.this.controller.addPath(GameView.this.video.getFileName(), currPath);
				getChildren().remove(selectPanel);
				getChildren().add(nav);
				nav.resizeRelocate(0, 540, 960, navHeight);
				instantReplay();
			}
		});
		Button no = new Button("No");
		no.setPrefSize(72, 36);
		no.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				getChildren().remove(selectPanel);
				getChildren().add(nav);
				nav.resizeRelocate(0, 540, 960, navHeight);
				instantReplay();
			}
		});
		
		selectPanel = new HBox();
		selectPanel.getChildren().addAll(prompt, yes, no);
		HBox.setMargin(prompt, new Insets(5, 10, 0, 5));
		HBox.setMargin(yes, new Insets(5, 10, 0, 5));
		HBox.setMargin(no, new Insets(5, 10, 0, 10));
		
		getChildren().remove(nav);
		getChildren().add(selectPanel);
		selectPanel.resizeRelocate(0, 540, 960, navHeight);
	}
	
	/**
	 * User performs click on foremost frame, play till subsequent second.
	 */
	public void nextFrame() {	
		isPaused = false;
		player.setStartTime(new Duration((currFrameNum-1)*1000));
		player.setStopTime(new Duration((currFrameNum)*1000));
		play();
		
		// Update the navigation panel
		currFrameNum++;
		prev.setDisable(false);   // should be able to seek previous frame after moving on
		if (currFrameNum > currPath.size()) {
			next.setDisable(true);   // user must click to advance, no peeking ahead in video   
		}
		frameNum.setText(currFrameNum + "/" + maxFrames);
	}
	
	/**
	 * Restarts video.  Can be used at any time during play or post-game.
	 */
	public void restart() {
		player.setStartTime(new Duration(0));		// Prepare for jump to the start of video
		player.setStopTime(new Duration(0));
		
		// Restarting after instant replay has started
		if (isInstantReplay) {
			isInstantReplay = false;
			player.setOnEndOfMedia(pauseHandler);				// revert back to original pause handler
			mediaView.setOnMouseClicked(clickHandler); 			// re-enable click/drag, keyboard actions
			mediaView.setOnMouseReleased(dragReleasedHandler);
			mediaView.setOnMouseDragged(draggedHandler);
			mediaView.setOnMousePressed(dragStartHandler);
			this.setOnKeyPressed(keyHandler);
			
			// Remove the instant replay tracking lines
			List<Node> toRemove = new LinkedList<Node>();
			for (Node child : this.getChildren()) {
				if (child instanceof Line) {
					toRemove.add(child);
				}
			}
			for (Node dispose : toRemove) {
				this.getChildren().remove(dispose);
			}
			
//			isPaused = true;
		}
		
		if (circle != null) {									// remove all graphics on top of MediaView
			getChildren().remove(circle);
			circle = null;
		}
		else if (startCircle != null) {
			getChildren().remove(startCircle);
			startCircle = null;
		}
		
		// Remove play again button if necessary
		if (nav.getChildren().contains(playAgain)) {
			nav.getChildren().remove(playAgain);
			HBox.setMargin(prev, new Insets(8, 0, 0, 370));	// rest prev's margin
		}
		
		// Reset navigation bar
		slider.setDisable(false);
		restart.setDisable(false);
		currFrameNum = 1;
		prev.setDisable(true);
		next.setDisable(true);
		frameNum.setText(currFrameNum + "/" + maxFrames);
		
		// Re-initialize current path
		currPath = new Path(video.getAnts().get(antIndex), video.getFileName());
		// Draw red circle around starting ant
		double x, y;
		x = video.getAnts().get(antIndex).getStart().getX();
		y = video.getAnts().get(antIndex).getStart().getY();
		startCircle = new Circle(x + hOffset, y, 25, Color.web("white", 0.05));
		startCircle.setStrokeType(StrokeType.OUTSIDE);
		startCircle.setStroke(Color.web("red", 0.9));
		startCircle.setStrokeWidth(2);
		startCircle.setOnMouseReleased(dragReleasedHandler);
		startCircle.setOnMouseDragged(draggedHandler);
		startCircle.setOnMousePressed(dragStartHandler);
		startCircle.setOnMouseClicked(clickHandler);
		this.getChildren().add(startCircle);
		circleFade.setNode(startCircle);
		
		play();		// Executes the actual jump to the beginning of the video
	}
	
	public void seekNext() {
		isSeeking = true;
		
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
	
	/**
	 * Undo last click, must be on the frame (2nd foremost) on which the click was made.
	 */
	public void undo() {
		if (currFrameNum != currPath.size()) return;	// check for correct frame
		
		frameNum.setText(currFrameNum + "/" + maxFrames);
		next.setDisable(true);
		
		if (circle != null) {
			getChildren().remove(circle);
			circle = null;
		}
		
		currPath.removeLast();
	}
	
	// Used by instantReplay()
	private int frameCnt;  
//	private Path replayPath;
	
	/**
	 * Replays the trajectory of the ant based on the users click by drawing a line from the 1st click to the
	 * 2nd and so on till the last click.
	 */
	public void instantReplay() {
		// Remove all clicking ability
		//mediaView.removeEventHandler(MouseEvent.MOUSE_PRESSED, dragStartHandler);
		//mediaView.removeEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);
		//mediaView.removeEventHandler(MouseEvent.MOUSE_RELEASED, dragReleasedHandler);
		//mediaView.removeEventHandler(MouseEvent.MOUSE_CLICKED, clickHandler);
		
//		replayPath = new Path();
//		
//		strokeTransition = StrokeTransitionBuilder.create()
//	            .duration(Duration.seconds(3))
//	            .shape(rect)
//	            .fromValue(Color.RED)
//	            .toValue(Color.DODGERBLUE)
//	            .cycleCount(Timeline.INDEFINITE)
//	            .autoReverse(true)
//	            .build();
		
		isInstantReplay = true;
		frameCnt = 1;
		player.setRate(4);
		player.setStartTime(new Duration(0));
		player.setStopTime(new Duration(1000));		// Starting at the first second...
		player.setOnEndOfMedia(new Runnable() {		// Briefly stop video every second to draw line
			@Override
			public void run() {
				frameNum.setText(frameCnt + "/" + maxFrames);
				if (frameCnt < maxFrames) {			
					player.setStartTime(new Duration(frameCnt*1000));
					player.setStopTime(new Duration((frameCnt + 1)*1000));
					play();
					
					// Draw line from previous click to current user click
					double startX, startY, endX, endY;
					startX = currPath.getClick(frameCnt - 1).getX();
					startY = currPath.getClick(frameCnt - 1).getY();
					endX = currPath.getClick(frameCnt).getX();
					endY = currPath.getClick(frameCnt).getY();
					Line line = new Line(startX + hOffset, startY, endX + hOffset, endY);
					line.setStroke(Color.web("hotpink", 0.3));
					line.setStrokeWidth(6);
					GameView.this.getChildren().add(line);
					
					frameCnt++;
				}
			}
		});
		
		// Add play again button to nav bar
		playAgain = new Button("Play Again?");
		playAgain.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// Receive another assigned <video id, ant #> pair from server
				int n = receiveAntAssign();
				int numAnts = 10;
				int numVids = 3;
				int tmpIndex = -1;
				if (n > -1) {
					tmpIndex = (n/numAnts)%numVids;
					antIndex = n%numAnts;

					if (vidIndex == tmpIndex) { // same video, just re-initialize starting ant and game with restart()
						System.out.println("Video: " + video.getName());
						System.out.println("Ant ID: " + video.getAnts().get(antIndex).getId());
						player.stop();		// stop instant replay first
						restart();
					}
					else {	// Re-initialize the video from file
						vidIndex = tmpIndex;
						System.out.println("got here");
						player.setOnEndOfMedia(null);	
						player.stop();					// stop instant replay beforehand
						video = videos.get(vidIndex);
						maxFrames = video.getMaxFrames();
						
						GameView.this.getChildren().remove(mediaView);
						GameView.this.getChildren().add(mediaPlaceholder);
						mediaPlaceholder.resizeRelocate(0, 0, 960, 540);
						
						// Reload video, reset the MediaView, and restart
						System.out.println("Video: " + video.getName());
						System.out.println("Ant ID: " + video.getAnts().get(antIndex).getId());
//						if (getClass().getResource("/vid/" + video.getFileName()) != null) {
							media = new Media(getClass().getResource("/vid/" + video.getFileName()).toString());
//						}
//						else {
//							media = new Media(video.getFileName());
//						}
						player = new MediaPlayer(media);
						player.setAutoPlay(false);
						player.setOnReady(readyHandler);
						mediaView = new MediaView(player);
						duration = media.getDuration();
						GameView.this.isSliderAdjusted = true;		// playback speed will carry on over to next video
						
						restart();
					}
				}
				else {
					try {
						throw new Exception("Starting <video id, ant #> pair could not be received from server.\n");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}		
		});
		playAgain.setPrefSize(100, 36);
		nav.getChildren().add(0, playAgain);
		HBox.setMargin(playAgain, new Insets(10, 0, 0, 5));
		HBox.setMargin(prev, new Insets(8, 0, 0, 265));
		
		frameNum.setText(frameCnt + "/" + maxFrames);
		// Disable navigation controls and keyboard controls
		prev.setDisable(true);
		next.setDisable(true);
//		speedSlider.setDisable(true);
		restart.setDisable(true);
		this.removeEventHandler(KeyEvent.KEY_PRESSED, keyHandler);
		
		play();
	}
	
	/**
	 * Properly exit game: stop and close media resources.
	 */
	private void exitGame() {
		player.stop();
	}

	/**
	 *	Pop-up menu to record ant behavior observed by player or undo last click.
	 */
	private class BehaviorPopupMenu extends ContextMenu {
		
		public BehaviorPopupMenu() {
			setAutoHide(true);	// probably doesn't do anything
			
			getItems().addAll(
//					MenuItemBuilder.create()
//					.text("(Q) Building")
//					.onAction(new EventHandler<ActionEvent>() {
//						public void handle(ActionEvent event) {
//							
//						}
//					})
//					.build(),
//					MenuItemBuilder.create()
//					.text("(W) Brood care")
//					.onAction(new EventHandler<ActionEvent>() {
//						public void handle(ActionEvent event) {
//							
//						}
//					})
//					.build(),
//					MenuItemBuilder.create()
//					.text("(E) Feeding")
//					.onAction(new EventHandler<ActionEvent>() {
//						public void handle(ActionEvent event) {
//							
//						}
//					})
//					.build(),
//					MenuItemBuilder.create()
//					.text("(R) Self-Grooming")
//					.onAction(new EventHandler<ActionEvent>() {
//						public void handle(ActionEvent event) {
//							
//						}
//					})
//					.build(),
//					MenuItemBuilder.create()
//					.text("(A) Interactive Grooming")
//					.onAction(new EventHandler<ActionEvent>() {
//						public void handle(ActionEvent event) {
//							
//						}
//					})
//					.build(),
//					MenuItemBuilder.create()
//					.text("(S) Food exchange")
//					.onAction(new EventHandler<ActionEvent>() {
//						public void handle(ActionEvent event) {
//							
//						}
//					}).build(),
//					SeparatorMenuItemBuilder.create()
					MenuItemBuilder.create()
					.text("Ant is Offscreen")
					.onAction(new EventHandler<ActionEvent>() {
						public void handle(ActionEvent event) {
							// play next second, register (-1,-1) click, add special case for drawing circles for such Clicks
						}
					}).build(),
					MenuItemBuilder.create()
					.text("(Ctrl-Z) Undo last click")
					.onAction(new EventHandler<ActionEvent>() {
						public void handle(ActionEvent event) {
							undo();
						}
					}).build()
			);
		}	
	}
}
