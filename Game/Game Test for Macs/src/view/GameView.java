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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import model.Click;
import model.Path;
import model.Profile;
import model.Video;
import model.Behavior;
import javafx.animation.FadeTransition;
import javafx.animation.FadeTransitionBuilder;
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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SeparatorMenuItemBuilder;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
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
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
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
	private final Video video;
	private final int hOffset;
	private final int maxFrames;
	
	private Media media;
	private MediaPlayer player;
	private MediaView mediaView;
	private AnchorPane mediaPlaceholder;	// Displays loading indicator while video is downloaded

	private final int navHeight = 56;
	private Button vidSelect;
	private Button prev;
	private Button next;
	//private Button restart;
	private Button sound;             // ADDED
	private TextField frameNum;
	private Slider speedSlider;
	private VBox slider;
	private HBox nav;
	private ProgressBar progress;     // ADDED
	
	private String imageView;         // ADDED
	private Profile currProfile;
	
	// ADDED
//	private HBox infoH;
//	private Text gameOps;
	private Button show;              // ADDED
	private Button hide;              // ADDED
	// END: ADDED
	
	// ADDED TODO: Change when updating point system
	private final int ptPerClick = 10; // ADDED: points given per click 
	private final int maxMult = 10;   // ADDED: Max value for multiplier
	private double goodScore;         // ADDED: Minimum score to increment multiplier/considered good score
	private double badScore;          // ADDED: Maximum score to decrement multiplier/considered bad score
	private int points;               // ADDED: Points for a single game 
	private double multiplier;        // ADDED: Represents how well player has done
	                                  //  if does well, increment by 0.1; if does 
	                                  //  poorly, decrement by 0.1 [min is 1]
	
	private HBox ptDisplay;           
	private TextField thousands;
	private TextField hundreds;
	private TextField tens;
	private TextField ones;
	// END: ADDED
	
//	private ProgressIndicator vLoadProgress;
	private BehaviorPopupMenu popupMenu;
	
	private EventHandler<MouseEvent> clickHandler;
	private EventHandler<MouseEvent> dragStartHandler;
//	private EventHandler<MouseEvent> dragDetectedHandler;
	private EventHandler<MouseEvent> draggedHandler;
	private EventHandler<MouseEvent> dragReleasedHandler;
	private final EventHandler<KeyEvent> keyHandler;
	private Runnable pauseHandler;
	
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
	private int numPrev = 0;                // ADDED: counts how many frames back the user has gone using previous
	private boolean isPaused = true;		// pause status independent of JavaFX STATUS
	private boolean isSeeking = false;		// indicates seeking to a previously used frame
	private boolean isSliderAdjusted = false;	// indicates slider was used while playing
	private boolean isInstantReplay = false;	// indicates instant replay has started
	private boolean isAdvanced = false;		// indicates game mode is basic (click) or advanced (drag, behavior menu)
	private boolean isBuffering = false;
	private boolean showPath = false;        // ADDED: hide path by default

	private static final AudioClip APPLAUSE = new AudioClip(GameView.class.getResource("/sound/Ooooh").toString());  // ADDED
	//private static final AudioClip BACKGROUND = new AudioClip(GameView.class.getResource("/sound/Background.m4a").toString());   // ADDED
	private static final AudioClip CLICK = new AudioClip(GameView.class.getResource("/sound/Pencil Check").toString()); // ADDED
	//private static final AudioClip CLICKDRAG = new AudioClip(GameView.class.getResource("/sound/Stapler").toString()); // ADDED
	private static final AudioClip DIALOG = new AudioClip(GameView.class.getResource("/sound/tada.wav").toString());  // ADDED
	private static final AudioClip MENU = new AudioClip(GameView.class.getResource("/sound/Menu.m4a").toString()); // ADDED
	private static final AudioClip PREVNEXT = new AudioClip(GameView.class.getResource("/sound/SoundFX.m4a").toString());
	private static final AudioClip SHOWHIDE = new AudioClip(GameView.class.getResource("/sound/Wood Bonk").toString()); // ADDED
	
	@SuppressWarnings("static-access")
	public GameView(AntController controller, Video video, Profile profile) {
		currProfile = profile;
		this.setStyle("-fx-background-color: limegreen");    // ADDED
		
		// ADDED: Set up sounds
		//GameView.BACKGROUND.setCycleCount(MediaPlayer.INDEFINITE);   // ADDED
		//GameView.BACKGROUND.setVolume(0.3); // ADDED
		//GameView.BACKGROUND.play();       // ADDED
		//CLICKDRAG.setVolume(0.3);  // ADDED
		GameView.APPLAUSE.setVolume(2.0);
		GameView.CLICK.setVolume(2.0);
		GameView.DIALOG.setVolume(2.0);
		GameView.MENU.setVolume(2.0);
		GameView.PREVNEXT.setVolume(2.0);
		GameView.SHOWHIDE.setVolume(2.0);
		
		this.controller = controller;
		this.video = video;
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
		System.out.println(video.getFileName());
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
				duration = media.getDuration();
				System.out.println(duration);
				
				// Randomly select starting ant and draw red circle around it
				//int antIndex = (int) (Math.random()*GameView.this.video.getStarting().size());
				// set antIndex to the ant number received from the server
				int antIndex = currProfile.getAnt();
				currPath = new Path(GameView.this.video.getStarting().get(antIndex), GameView.this.video.getFileName());
				
				double x, y;
				x = GameView.this.video.getStarting().get(antIndex).getX();
				y = GameView.this.video.getStarting().get(antIndex).getY();
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
				player.bufferProgressTimeProperty().addListener(new ChangeListener<Duration>() {
					@Override
					public void changed(ObservableValue<? extends Duration> obs, Duration oldVal, Duration newVal) {
						if (isBuffering && (newVal.greaterThanOrEqualTo(new Duration((currPath.size()+3)*1000)))) {
							GameView.this.getChildren().remove(vLoadProgress);
							System.out.println("BUFFERED!!!");
							System.out.println(player.getBufferProgressTime() + " > " + ((currPath.size()+2)*1000));
							isBuffering = false;
							return;
						}
						else if (newVal.equals(duration)) {	// discard listener, whole video has been buffered
							isBuffering = false;
							player.bufferProgressTimeProperty().removeListener(this);
						}
					}
				});
				
				// Don't display the video portion of the game until everything else is ready
				//restart.setDisable(false);
				GameView.this.getChildren().remove(mediaPlaceholder);
				GameView.this.getChildren().add(mediaView);
				GameView.this.getChildren().add(startCircle);
				mediaView.relocate(hOffset, 0);
				mediaView.setFitWidth(960);
				mediaView.setFitHeight(540);
				
//				int tmp = (currPath.size() + 2)*1000;
//				System.out.println(player.getBufferProgressTime() + " < " + tmp);
				// Progress indicator goes
				Duration bufferTime = player.getBufferProgressTime();
				if (bufferTime.lessThan(new Duration((currPath.size()+3)*1000)) && !bufferTime.equals(duration)) {
					System.out.println("BUFFERING");
					isBuffering = true;
					GameView.this.getChildren().add(vLoadProgress);
					vLoadProgress.relocate(455, 245);
				}
			}
		});
		
		// Handle pauses in video
		pauseHandler = new Runnable() {
			@Override
			public void run() {
				isPaused = true;
				
				// need at least 1 second in buffer to play continuously
				int tmp = (currPath.size() + 2)*1000;
				System.out.println(player.getBufferProgressTime() + " < " + tmp);
				Duration bufferTime = player.getBufferProgressTime();
				if (!bufferTime.equals(duration) && !isBuffering && bufferTime.lessThan(new Duration((currPath.size()+3)*1000))) {
					System.out.println("BUFFERING");
					isBuffering = true;
					getChildren().add(vLoadProgress);
					vLoadProgress.relocate(455, 245);
				}
				
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
						circle.setStrokeWidth(4);
						circle.setOnMouseClicked(clickHandler);
						GameView.this.getChildren().add(circle);
					}
					else if (currFrameNum == currPath.size() + 1 && showPath)   
					{
						// ADDED: draw circle around previous click of final frame showing path
						double x, y;
						x = currPath.getClick(currFrameNum - 2).getX();
						y = currPath.getClick(currFrameNum - 2).getY();
						circle = new Circle(x + hOffset, y, 25, Color.web("white", 0.05));
						circle.setStrokeType(StrokeType.OUTSIDE);
						circle.setStroke(Color.web("blue", 0.5));
						circle.setStrokeWidth(4);
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
						GameView.CLICK.play();  // ADDED
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
							//GameView.BACKGROUND.stop();  // ADDED
							GameView.APPLAUSE.play();    // ADDED
							
							// Name path by date, time, and video name
							Calendar cal = Calendar.getInstance();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							currPath.setName(sdf.format(cal.getTime()));
							

							Click thisClick = new Click(currFrameNum*50 + 1, x1, y1, degree, Behavior.NONE);        // ADDED
							//currPath.addClick(new Click(currFrameNum*50 + 1, x1, y1, degree, Behavior.NONE));
							currPath.addClick(thisClick);     // ADDED
							
							// ADDED: draw line
							if (showPath) {
								if (circle != null) {
									GameView.this.getChildren().remove(circle);
									circle = null;
								}
								
								double startX, startY, endX, endY;
								startX = (double) currPath.getClick(currFrameNum - 2).getX();
								startY = (double) currPath.getClick(currFrameNum - 2).getY();
								endX = (double) thisClick.getX();
								endY = (double) thisClick.getY();
								Line line = new Line(startX + hOffset, startY, endX + hOffset, endY);
								line.setStroke(Color.web("chartreuse", 0.5));
								line.setStrokeWidth(10);       // ADDED
								line.setOpacity(0.8); 
								line.setMouseTransparent(true);
						        GameView.this.getChildren().add(line);
							}
							updatePoints(ptPerClick);
							updateMultiplier();
							// END ADDED
							
							// Disable mouse actions
							System.out.println("disabling mouse events");
							GameView.this.mediaView.removeEventHandler(MouseEvent.DRAG_DETECTED, dragStartHandler);
							GameView.this.mediaView.removeEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);
							GameView.this.mediaView.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);
							
							// ADDED: Disable show, hide buttons
							show.setDisable(true);
							hide.setDisable(true);
							
							GameView.DIALOG.play();  // ADDED
							submitPrompt();
						}
						else if (currFrameNum > currPath.size()) {
							nextFrame();
							// Add click
							Click thisClick = new Click((currFrameNum-1)*50 + 1, x1, y1, degree, Behavior.NONE);        // ADDED
							//currPath.addClick(new Click((currFrameNum-1)*50 + 1, x1, y1, degree, Behavior.NONE));
							currPath.addClick(thisClick);     // ADDED
							
							// ADDED: draw line and circle
							if (showPath && currFrameNum > 2)
							{
								GameView.this.getChildren().remove(circle);
								circle = null;
								
								double startX, startY, endX, endY;
								startX = (double) currPath.getClick(currFrameNum - 3).getX();
								startY = (double) currPath.getClick(currFrameNum - 3).getY();
								endX = (double) thisClick.getX();
								endY = (double) thisClick.getY();
								Line line = new Line(startX + hOffset, startY, endX + hOffset, endY);
								line.setStroke(Color.web("chartreuse", 0.5));
								line.setStrokeWidth(10);          // ADDED
								line.setOpacity(0.8); 
								line.setMouseTransparent(true);
								GameView.this.getChildren().add(line);
								
								// ADDED: Draw circle each time around last click
								double x, y;
								x = endX;
								y = endY;
								circle = new Circle(x + hOffset, y, 25, Color.web("white", 0.05));
								circle.setStrokeType(StrokeType.OUTSIDE);
								circle.setStroke(Color.web("blue", 0.5));
								circle.setStrokeWidth(4);
								circle.addEventHandler(MouseEvent.MOUSE_PRESSED, dragStartHandler);
								circle.addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);
								circle.addEventHandler(MouseEvent.MOUSE_RELEASED, dragReleasedHandler);
								GameView.this.getChildren().add(circle);
								circle.setOnMouseClicked(clickHandler);
								// END: ADDED
							}
							else if (showPath && currFrameNum == 2) 
							{
								// Draw circle only
								GameView.this.getChildren().remove(circle);
								circle = null;
								
								double x, y;
								x = thisClick.getX();
								y = thisClick.getY();
								circle = new Circle(x + hOffset, y, 25, Color.web("white", 0.05));
								circle.setStrokeType(StrokeType.OUTSIDE);
								circle.setStroke(Color.web("blue", 0.5));
								circle.setStrokeWidth(4);
								circle.addEventHandler(MouseEvent.MOUSE_PRESSED, dragStartHandler);
								circle.addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);
								circle.addEventHandler(MouseEvent.MOUSE_RELEASED, dragReleasedHandler);
								GameView.this.getChildren().add(circle);
								circle.setOnMouseClicked(clickHandler);
							}
							updatePoints(ptPerClick);
							// END ADDED
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
							GameView.CLICK.play();  // ADDED
							if (currFrameNum == maxFrames) {		// last frame
								//GameView.BACKGROUND.stop();  // ADDED
								GameView.APPLAUSE.play();    // ADDED
								
								// Name path by date, time, and video name
								Calendar cal = Calendar.getInstance();
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								currPath.setName(sdf.format(cal.getTime()));
								
								Click thisClick = new Click((currFrameNum-1)*50 + 1, event.getX() + hOffset, event.getY(), -1, 
										Behavior.NONE);        // ADDED
								//currPath.addClick(new Click((currFrameNum-1)*50 + 1, event.getX() + hOffset, event.getY(), -1, 
								//      Behavior.NONE));
								currPath.addClick(thisClick);     // ADDED
								
								// ADDED: draw line
								if (showPath) {
									if (circle != null) {
										GameView.this.getChildren().remove(circle);
										circle = null;
									}
									
									double startX, startY, endX, endY;
									startX = (double) currPath.getClick(currFrameNum - 2).getX();
									startY = (double) currPath.getClick(currFrameNum - 2).getY();
									endX = (double) thisClick.getX();
									endY = (double) thisClick.getY();
									Line line = new Line(startX + hOffset, startY, endX + hOffset, endY);
									line.setStroke(Color.web("chartreuse", 0.5));
									line.setStrokeWidth(10);       // ADDED
									line.setOpacity(0.8); 
									line.setMouseTransparent(true);
									GameView.this.getChildren().add(line);
								}
								updatePoints(ptPerClick);
								updateMultiplier();
								// END ADDED
								
								// Disable mouse actions
								GameView.this.mediaView.removeEventHandler(MouseEvent.DRAG_DETECTED, dragStartHandler);
								GameView.this.mediaView.removeEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);
								GameView.this.mediaView.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);
								
								// ADDED: Disable show, hide click, drag buttons
								show.setDisable(true);
								hide.setDisable(true);
								
								GameView.DIALOG.play(); // ADDED
	//							submitDialog.show();
								submitPrompt();
							}
							else if (currFrameNum > currPath.size()) {
								if (startCircle != null) {
									circleFade.play();
								}
								
								nextFrame();
								// Add click
								Click thisClick = new Click((currFrameNum-2)*50 + 1, event.getX() + hOffset, event.getY(), -1, 
										Behavior.NONE);        // ADDED
								//currPath.addClick(new Click((currFrameNum-2)*50 + 1, event.getX() + hOffset, event.getY(), -1, 
								//      Behavior.NONE));
								currPath.addClick(thisClick);     // ADDED
								
								// ADDED: draw line
								if (showPath && currFrameNum > 2)
								{
									GameView.this.getChildren().remove(circle);
									circle = null;
									
									double startX, startY, endX, endY;
									startX = (double) currPath.getClick(currFrameNum - 3).getX();
									startY = (double) currPath.getClick(currFrameNum - 3).getY();
									endX = (double) thisClick.getX();
									endY = (double) thisClick.getY();
									Line line = new Line(startX + hOffset, startY, endX + hOffset, endY);
									line.setStroke(Color.web("chartreuse", 0.5));
									line.setStrokeWidth(10);          
									line.setOpacity(0.8); 
									line.setMouseTransparent(true);
									GameView.this.getChildren().add(line);
									
									// ADDED: Draw circle each time around last click
									double x, y;
									x = endX;
									y = endY;
									circle = new Circle(x + hOffset, y, 25, Color.web("white", 0.05));
									circle.setStrokeType(StrokeType.OUTSIDE);
									circle.setStroke(Color.web("blue", 0.5));
									circle.setStrokeWidth(4);
									circle.addEventHandler(MouseEvent.MOUSE_PRESSED, dragStartHandler);
									circle.addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);
									circle.addEventHandler(MouseEvent.MOUSE_RELEASED, dragReleasedHandler);
									GameView.this.getChildren().add(circle);
									circle.setOnMouseClicked(clickHandler);
									// END: ADDED
								}
								else if (showPath && currFrameNum == 2) 
								{
									// Draw circle only
									GameView.this.getChildren().remove(circle);
									circle = null;
									
									double x, y;
									x = thisClick.getX();
									y = thisClick.getY();
									circle = new Circle(x + hOffset, y, 25, Color.web("white", 0.05));
									circle.setStrokeType(StrokeType.OUTSIDE);
									circle.setStroke(Color.web("blue", 0.5));
									circle.setStrokeWidth(4);
									circle.addEventHandler(MouseEvent.MOUSE_PRESSED, dragStartHandler);
									circle.addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);
									circle.addEventHandler(MouseEvent.MOUSE_RELEASED, dragReleasedHandler);
									GameView.this.getChildren().add(circle);
									circle.setOnMouseClicked(clickHandler);
								}
								updatePoints(ptPerClick);
								// END ADDED
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
		// Back to video selection button
		vidSelect = new Button("Main Menu"); // ADDED
		vidSelect.setMinSize(70, 30);
		vidSelect.setFont(Font.font("Chalkduster", 13));  // ADDED
		vidSelect.setStyle("-fx-base: maroon");  // ADDED
		vidSelect.setEffect(new InnerShadow());  // ADDED 
		vidSelect.setFocusTraversable(false);	// keyboard shortcuts only work if nothing has focus
		vidSelect.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				GameView.MENU.play();
				// TODO
			}	
		});

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
		prev.setStyle("-fx-base: orange"); // ADDED
		prev.setEffect(new InnerShadow());  // ADDED
		prev.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				seekPrev();
				GameView.PREVNEXT.play();
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
		frameNum.setEffect(new InnerShadow());  // ADDED
		frameNum.setStyle("-fx-background-color: darksalmon");  // ADDED
		
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
		next.setStyle("-fx-base: orange");
		next.setEffect(new InnerShadow());  // ADDED
		next.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				seekNext();
				GameView.PREVNEXT.play();
			}
		});
		
		// Restart video button
//		tempImg = new Image(getClass().getResourceAsStream("/img/new_ant.png"), 36, 36, false, true);
//		restart = new Button();
//		restart.setDisable(true);
//		restart.setGraphic(new ImageView(tempImg));
//		restart.setTooltip(new Tooltip("Restart Video"));
//		restart.setFocusTraversable(false);
//		restart.setEffect(new InnerShadow());  // ADDED
//		restart.setPrefSize(36, 36);
//		restart.setOnAction(new EventHandler<ActionEvent>() {
//			@Override
//			public void handle(ActionEvent event) {
//				GameView.CHIMES.play();  // ADDED
//				restart();
//			}
//		});
		
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
				if (isPaused) {	
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
		slider.setStyle("-fx-base: black; -fx-color: darkorange");      // ADDED
//		slider.getChildren().addAll(speedSlider);
		
		// ADDED: Create progress bar
		progress = new ProgressBar();    
		progress.setProgress(((double)currFrameNum)/maxFrames); 
		progress.setPrefSize(240, 20);  
		progress.setStyle("-fx-color: black; -fx-box-border: firebrick; -fx-accent: coral");  
		progress.setEffect(new Bloom());     
		
		// ADDED: Create sound on/off button
		initSound();
		
		// Assemble navigation bar
		nav = new HBox();
		nav.getChildren().addAll(vidSelect, sound, /*clickMode, dragMode, */prev, frameNum, progress, next, slider/*, restart*/);
		nav.setMargin(vidSelect, new Insets(10, 0, 10, 5));
		//nav.setMargin(clickMode, new Insets(10, 0, 0, 5));
		//nav.setMargin(dragMode, new Insets(10, 0, 0, 0));
//		nav.setMargin(prev, new Insets(8, 0, 0, 165));
		nav.setMargin(sound, new Insets(8, 0, 0, 70));
		nav.setMargin(prev, new Insets(8, 0, 0, /*140*/20));
		nav.setMargin(frameNum, new Insets(10, 0, 0, 10));
		nav.setMargin(progress, new Insets(10, 0, 0, 10));  // ADDED
		nav.setMargin(next, new Insets(8, 0, 0, 5));
		nav.setMargin(slider, new Insets(10, 0, 0, 35));
		//nav.setMargin(restart, new Insets(8, 5, 0, 10));
		
		// ADDED: Set up show path button
		tempImg = new Image(getClass().getResourceAsStream("/img/show_path.png"), 120, 40, false, true);
		show = new Button();
		show.setGraphic(new ImageView(tempImg));
		show.setTooltip(new Tooltip("Show Lines"));
		//show.setDisable(true);		// until there's a click on the next frame, can't seek there
		show.setFocusTraversable(false);
		show.setPrefSize(120, 40);
		show.setStyle("-fx-base: black");
		show.setEffect(new DropShadow());
		show.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!showPath)
				{
					SHOWHIDE.play();
					showLines(currFrameNum);
					showPath = true;
					show.setDisable(true);
					hide.setDisable(false);
				}
			}
		});
				
		// Set up hide path button
		tempImg = new Image(getClass().getResourceAsStream("/img/hide_path.png"), 120, 40, false, true);
		hide = new Button();
		hide.setGraphic(new ImageView(tempImg));
		hide.setTooltip(new Tooltip("Hide Lines"));
		hide.setDisable(true);		// until there's a click on the next frame, can't hide path
		hide.setFocusTraversable(false);
		hide.setPrefSize(120, 40);
		hide.setStyle("-fx-base: black");
	    hide.setEffect(new DropShadow());
		hide.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (showPath)
				{
					SHOWHIDE.play();
					hideLines();
					showPath = false;
					hide.setDisable(true);
					show.setDisable(false);
				}
			}
		});
		/*
		// Set up Click and Drag buttons
		final ToggleButton clickMode = new ToggleButton("Click");
		final ToggleButton dragMode = new ToggleButton("Drag");
		clickMode.setDisable(true);
		clickMode.setFocusTraversable(false);
		clickMode.setMinSize(60,30);  // ADDED
		clickMode.setFont(Font.font("Chalkduster", 20));  // ADDED
		clickMode.setStyle("-fx-base: goldenrod");  // ADDED
		clickMode.setEffect(new InnerShadow());  // ADDED
		clickMode.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				GameView.CLICKDRAG.play();
				isAdvanced = false;
				dragMode.setDisable(false);
				clickMode.setDisable(true);
			}
		});
		dragMode.setFocusTraversable(false);
		dragMode.setMinSize(60, 30);  // ADDED
		dragMode.setFont(Font.font("Chalkduster", 20));  // ADDED
		dragMode.setStyle("-fx-base: goldenrod");  // ADDED
		dragMode.setEffect(new InnerShadow());  // ADDED
		dragMode.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				GameView.CLICKDRAG.play();
				isAdvanced = true;
				clickMode.setDisable(false);
				dragMode.setDisable(true);
			}
		});
        ToggleGroup group = new ToggleGroup();
        clickMode.setToggleGroup(group);
        dragMode.setToggleGroup(group);
        group.selectToggle(clickMode);
		*/
		// Set up game options text
//		gameOps = new Text("<- Game Levels ->");
//		gameOps.setFont(Font.font("Chalkduster", 20));
//		gameOps.setFocusTraversable(false);
//		gameOps.setDisable(true);
//		gameOps.setOpacity(1);
//		
//		// Assemble infoH bar with game options
//		infoH = new HBox();
//		infoH.setStyle("-fx-alignment: center; -fx-background-color: skyblue");
//		infoH.getChildren().addAll(/*clickMode, dragMode, gameOps,*/ show, hide);
//		//infoH.setMargin(clickMode, new Insets(10, 0, 0, 110));
//		//infoH.setMargin(dragMode, new Insets(10, 0, 0, 20));
//		infoH.setMargin(gameOps, new Insets(10, 0, 0, 50));
//		infoH.setMargin(show, new Insets(10, 0, 0, 20));
//		infoH.setMargin(hide, new Insets(10, 0, 0, 20));
		// END: ADDED
		
		setUpPoints();
		ptDisplay.getChildren().addAll(show, thousands, hundreds, tens, ones, hide);
		ptDisplay.setMargin(show, new Insets(0, 0, 0, 40));
		ptDisplay.setMargin(thousands, new Insets(0, 0, 0, 20));
		ptDisplay.setMargin(hide, new Insets(0, 0, 0, 20));
		
		// Final setup of all containers
		getChildren().add(mediaPlaceholder);
//		getChildren().add(mediaView);
		getChildren().add(nav);
		getChildren().add(ptDisplay); // ADDED
		
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
					System.out.println(player.getBufferProgressTime());
//					submitPrompt();
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
	}

	/*
	 * ADDED: This initializes the sound button.
	 */
	private void initSound() {
		sound = new Button();
		sound.setEffect(new InnerShadow());
		final ImageView im1 = new ImageView(new Image(getClass().getResourceAsStream("/img/sound.png"), 36, 36, false, true));
		final ImageView im2 = new ImageView(new Image(getClass().getResourceAsStream("/img/Nosound.png"), 36, 36, false, true));
		if (controller.getMusicVolume() != 0.0)
		{
			sound.setGraphic(im1);
			imageView = "im1";
		}
		else
		{
			sound.setGraphic(im2);
			imageView = "im2";
			
			GameView.APPLAUSE.setVolume(0.0);
			GameView.CLICK.setVolume(0.0);
			GameView.DIALOG.setVolume(0.0);
			GameView.SHOWHIDE.setVolume(0.0);
		}
		sound.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (imageView.equals("im1")) {
					sound.setGraphic(im2);
					imageView = "im2";
					
					GameView.APPLAUSE.setVolume(0.0);
					//GameView.BACKGROUND.setVolume(0.0);
					GameView.CLICK.setVolume(0.0);
					GameView.DIALOG.setVolume(0.0);
					GameView.MENU.setVolume(0.0);
					GameView.PREVNEXT.setVolume(0.0);
					GameView.SHOWHIDE.setVolume(0.0);
				} else {
					sound.setGraphic(im1);
					imageView = "im1";
					
					GameView.APPLAUSE.setVolume(2.0);
					//GameView.BACKGROUND.setVolume(0.3);
					GameView.CLICK.setVolume(2.0);
					GameView.DIALOG.setVolume(2.0);
					GameView.MENU.setVolume(2.0);
					GameView.PREVNEXT.setVolume(2.0);
					GameView.SHOWHIDE.setVolume(2.0);
				}
				controller.toggleSound();
			}
			
		});
		
	}

	@Override
	protected void layoutChildren() {
		mediaPlaceholder.resizeRelocate(0, 0, 960, 540);
		nav.resizeRelocate(0, 540, 960, navHeight);
		//infoH.resizeRelocate(0, 540 + navHeight, 960, navHeight + 5); // ADDED
		ptDisplay.resizeRelocate(0, 540 + navHeight, 960, navHeight + 5); // ADDED
	}
	
	@Override
	protected double computePrefWidth(double width) {
		return 960;
	}
	
	@Override
	protected double computePrefHeight(double height) {
		return 540 + 2*navHeight;  // ADDED
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
				// ADDED: If path already visible, hide before instantReplay()
				if (showPath)
					hideLines();
				
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
				// ADDED: If path already visible, hide before instantReplay()
				if (showPath)
					hideLines();
				
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
		progress.setProgress(((double)currFrameNum)/maxFrames); // ADDED
	}
	
	/**
	 * Restarts video.  Can be used at any time during play or post-game.
	 */
	public void restart() {
//		for (Node child : this.getChildren()) {
//			if (child instanceof Circle) {
//				this.getChildren().remove(child);
//				break;
//			}
//		}
		if (circle != null) {
			getChildren().remove(circle);
			circle = null;
		}
		else if (startCircle != null) {
			getChildren().remove(startCircle);
			startCircle = null;
		}
		
		if (showPath)
			hideLines();     // ADDED: remove lines drawn
		
		// ADDED: reset points
		points = 0;
		thousands.setText("0");
		hundreds.setText("0");
		tens.setText("0");
		ones.setText("0");
		// END: ADDED
		
		// Go to start of video
		player.setStartTime(new Duration(0));
		player.setStopTime(new Duration(0));
		
		// After instant replay has started
		if (isInstantReplay) {
			isInstantReplay = false;
			player.setOnEndOfMedia(pauseHandler);	// revert back to original pause handler
			mediaView.addEventHandler(MouseEvent.MOUSE_PRESSED, dragStartHandler);
			mediaView.addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);
			mediaView.addEventHandler(MouseEvent.MOUSE_RELEASED, dragReleasedHandler);
			this.addEventHandler(KeyEvent.KEY_PRESSED, keyHandler);
			player.stop();
			
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
			
			slider.setDisable(false);	// Re-enable speed slider
			isPaused = true;
		}
		
		// Reset navigation bar
		currFrameNum = 1;
		prev.setDisable(true);
		next.setDisable(true);
		frameNum.setText(currFrameNum + "/" + maxFrames);
		progress.setProgress(((double)currFrameNum)/maxFrames); // ADDED
		
		// Randomly select starting ant and draw red circle around it
		int antIndex = (int) (Math.random()*video.getStarting().size());
		currPath = new Path(video.getStarting().get(antIndex), video.getFileName());
		
		double x, y;
		x = video.getStarting().get(antIndex).getX();
		y = video.getStarting().get(antIndex).getY();
		startCircle = new Circle(x + hOffset, y, 25, Color.web("white", 0.05));
		startCircle.setStrokeType(StrokeType.OUTSIDE);
		startCircle.setStroke(Color.web("red", 0.9));
		startCircle.setStrokeWidth(2);
		startCircle.addEventHandler(MouseEvent.MOUSE_PRESSED, dragStartHandler);
		startCircle.addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);
		startCircle.addEventHandler(MouseEvent.MOUSE_RELEASED, dragReleasedHandler);
		this.getChildren().add(startCircle);
		startCircle.setOnMouseClicked(clickHandler);
		circleFade.setNode(startCircle);
//		circleFade = FadeTransitionBuilder.create()		// create new fade transition for new starting circle
//	            .duration(Duration.seconds(1))
//	            .node(circle)
//	            .fromValue(1)
//	            .toValue(0.1)
//	            .cycleCount(1)
//	            .autoReverse(false)
//	            .build();
	}
	
	public void seekNext() {
		numPrev--;  // ADDED
		
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
		progress.setProgress(((double)currFrameNum)/maxFrames); // ADDED
		
		// ADDED: Update path when seeking other frames
		if (showPath)
		{
			hideLines();
			showLines(currFrameNum);
		}
	}
	
	public void seekPrev() {
		numPrev++;   // ADDED

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
		progress.setProgress(((double)currFrameNum)/maxFrames); // ADDED
		
		// ADDED: Update path when seeking other frames
		if (showPath)
		{
			hideLines();
			showLines(currFrameNum);
		}
	}
	
	/**
	 * Seek next but play the video till next frame
	 */
	public void playTillNextFrame() {
		numPrev--;  // ADDED
		
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
		progress.setProgress(((double)currFrameNum)/maxFrames); // ADDED
		
		// ADDED: Update path when seeking other frames
		if (showPath)
		{
			hideLines();
			showLines(currFrameNum);
		}
	}
	
	/**
	 * Undo last click, must be on the frame (2nd foremost) on which the click was made.
	 */
	public void undo() {
		if (currFrameNum != currPath.size()) return;	// check for correct frame
		
		frameNum.setText(currFrameNum + "/" + maxFrames);
		progress.setProgress(((double)currFrameNum)/maxFrames); // ADDED
		next.setDisable(true);
		
		if (circle != null) {
			getChildren().remove(circle);
			circle = null;
		}
		
		currPath.removeLast();
		updatePoints(-ptPerClick);  // ADDED
		
		// ADDED: Update path
		if (showPath)
		{
			hideLines();
			showLines(currFrameNum);
		}
	}
	
	// Used by instantReplay()
	private int frameCnt;  
//	private Path replayPath;
	
	/**
	 * Replays the trajectory of the ant based on the users click by drawing a line from the 1st click to the
	 * 2nd and so on till the last click.
	 */
	public void instantReplay() {
		isInstantReplay = true;
		frameCnt = 1;
		player.setRate(4);
		player.setStartTime(new Duration(0));
		player.setStopTime(new Duration(1000));
		
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
		
		// Briefly stop video every second to draw line
		player.setOnEndOfMedia(new Runnable() {
			@Override
			public void run() {
				if (frameCnt == maxFrames) {		// no end point in last frame 
					frameNum.setText(frameCnt + "/" + maxFrames);	// simply update display
					return;
				}
				
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
				line.setStroke(Color.web("hotpink", 0.5));
				line.setStrokeWidth(10);  // ADDED
				GameView.this.getChildren().add(line);
				
				frameNum.setText(frameCnt + "/" + maxFrames);
				frameCnt++;
			}
		});
		
		frameNum.setText(frameCnt + "/" + maxFrames);
		// Disable navigation buttons, speed slider, and keyboard controls
		prev.setDisable(true);
		next.setDisable(true);
		speedSlider.setDisable(true);
		this.removeEventHandler(KeyEvent.KEY_PRESSED, keyHandler);
		
		play();
	}
	
	/**
	 * Properly exit game: stop and close media resources.
	 */
	private void exitGame() {
		player.stop();
		
		//GameView.BACKGROUND.stop();  // ADDED
		
		GameView.this.controller.reselectVid();
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
	
	/*
	 * ADDED: this method will redraw all of the lines, assuming that
	 * they are not already drawn on the video.
	 */
	private void showLines(int endFrame)
	{
		if (circle != null)
		{
			GameView.this.getChildren().remove(circle);
			circle = null;
		}
		
		double startX, startY, endX = currPath.getStart().getX(), endY = currPath.getStart().getY();
	
		if ((numPrev == 0 && endFrame == 2) || (numPrev > 0 && currFrameNum == 1))
		{
			endX = currPath.getClick(0).getX();
			endY = currPath.getClick(0).getY();
		}
		
		else if (!next.isDisabled())  // If seeking previous frames, need to show path differently.
			endFrame = currFrameNum + 1;
		
		
		for (int count = 2; count < endFrame; count++)
		{
			startX = (double) currPath.getClick(count - 2).getX();
			startY = (double) currPath.getClick(count - 2).getY();
			endX = (double) currPath.getClick(count-1).getX();
			endY = (double) currPath.getClick(count-1).getY();
			Line line = new Line(startX + hOffset, startY, endX + hOffset, endY);
			line.setStroke(Color.web("chartreuse", 0.5));
			line.setStrokeWidth(10);
			line.setOpacity(0.8); 
			line.setMouseTransparent(true);
			GameView.this.getChildren().add(line);
		}
		
		circle = new Circle(endX + hOffset, endY, 25, Color.web("white", 0.05));
		circle.setStrokeType(StrokeType.OUTSIDE);
		circle.setStroke(Color.web("blue", 0.5));
		circle.setStrokeWidth(4);
		circle.setOnMouseClicked(clickHandler);
		GameView.this.getChildren().add(circle);	
	}
	
	/*
	 * ADDED: this method will remove all of the lines and
	 * circle, in effect hiding the path.
	 */
	private void hideLines()
	{
		GameView.this.getChildren().remove(circle);
		circle = null;
		
		List<Node> toRemove = new LinkedList<Node>();
		for (Node child : this.getChildren()) {
			if (child instanceof Line) {
				toRemove.add(child);
			}
		}

		for (Node dispose : toRemove) {
			this.getChildren().remove(dispose);
		}
	}
	
	/*
	 * ADDED: This is a private method that sets up the points display
	 */
	private void setUpPoints() {
		// ADDED: Set up point display
		points = 0;
		multiplier = 1;  // TODO: Get multiplier from database
		goodScore = ptPerClick*multiplier*(maxFrames - 10);  // ADDED TODO: Change when updating point system
		badScore = ptPerClick*multiplier*10;    // ADDED TODO: Change when updating point system
		
		ptDisplay = new HBox();
		
		// Set up thousands text field
		thousands = new TextField("0");
		thousands.setPrefColumnCount(1);
		thousands.setAlignment(Pos.CENTER);
		thousands.setPrefSize(30, 50);
		thousands.setFocusTraversable(false);
		thousands.setDisable(true);
		thousands.setOpacity(1);
		thousands.setStyle("-fx-background-color: crimson; -fx-font-size: 25; -fx-font-weight: bold");
		thousands.setEffect(new DropShadow());
		
		// Set up hundreds text field
		hundreds = new TextField("0");
		hundreds.setPrefColumnCount(1);
		hundreds.setAlignment(Pos.CENTER);
		hundreds.setPrefSize(30, 50);
		hundreds.setFocusTraversable(false);
		hundreds.setDisable(true);
		hundreds.setOpacity(1);
		hundreds.setStyle("-fx-background-color: cyan; -fx-font-size: 25; -fx-font-weight: bold");
		hundreds.setEffect(new DropShadow());
		
		// Set up tens text field
		tens = new TextField("0");
		tens.setPrefColumnCount(1);
		tens.setAlignment(Pos.CENTER);
		tens.setPrefSize(30, 50);
		tens.setFocusTraversable(false);
		tens.setDisable(true);
		tens.setOpacity(1);
		tens.setStyle("-fx-background-color: gold; -fx-font-size: 25; -fx-font-weight: bold");
		tens.setEffect(new DropShadow());
		
		// Set up ones text field
		ones = new TextField("0");
		ones.setPrefColumnCount(1);
		ones.setAlignment(Pos.CENTER);
		ones.setPrefSize(30, 50);
		ones.setFocusTraversable(false);
		ones.setDisable(true);
		ones.setOpacity(1);
		ones.setStyle("-fx-background-color: orchid; -fx-font-size: 25; -fx-font-weight: bold");
		ones.setEffect(new DropShadow());
		
		ptDisplay.setStyle("-fx-alignment: center; -fx-background-color: limegreen");
		// END: ADDED	
	}
	
	/*
	 * ADDED: this method adjusts the point display and the points
	 * TODO: make this better
	 */
	private void updatePoints(int pts)
	{
		int toAdd = pts;
		if (pts == ptPerClick)
			toAdd = (int)(pts * multiplier);
		points += toAdd;
		String newTho, newHun, newTen, newOne;
		newTho = "" + points/1000;
		newHun = "" + (points%1000)/100;
		newTen = "" + (points%100)/10;
		newOne = "" + points%10;
		
		if (!newTho.equals(thousands.getText()))
			thousands.setText(newTho);
		if (!newHun.equals(hundreds.getText()))
			hundreds.setText(newHun);
		if (!newTen.equals(tens.getText()))
			tens.setText(newTen);
		if (!newOne.equals(ones.getText()))
			ones.setText(newOne);
	}
	
	/*
	 * ADDED: this method is called at the end of a game to check
	 * whether or not this was a good score.
	 */
	private void updateMultiplier()
	{
		if (points >= goodScore && multiplier < maxMult)
			multiplier += 0.1;
		else if (points <= badScore && multiplier > 1)
			multiplier -= 0.1;
	}


}
