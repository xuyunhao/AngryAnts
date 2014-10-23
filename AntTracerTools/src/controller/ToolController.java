/**
 * @author Paul Shen
 * 
 * Sets up the primary GUI container.  Interchanges the different views.  Invokes calls on the
 * model (e.g. addPath()).  
 */

package controller;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.AntGame;
import model.Path;
import model.Video;
import view.GameView;
import view.TagVideoView;
import view.VideoAnalysisView;
import view.VideoView;

public class ToolController extends Application {

	private Group root;
	private AntGame game;
	private VideoView vidView;
	private GameView gameView;
	private TagVideoView tagView;
	private VideoAnalysisView vidAltView;
	private Stage primary;
	
    final double gameWidth = 960;  
    final double gameHeight = 596;
    
    /**
     * Helper method to create the video selection screen
     * @param primaryStage
     */
    private void init(Stage primaryStage) {
        root = new Group();
        primaryStage.setScene(new Scene(root, gameWidth, gameHeight));
//        tagView = new TagVideoView(this);
//        tagView.setMinSize(gameWidth, gameHeight);  
//        tagView.setPrefSize(gameWidth, gameHeight);
//        tagView.setMaxSize(gameWidth, gameHeight);
//        root.getChildren().add(tagView);
        
        vidView = new VideoView(this, game.getVideos());
        vidView.setMinSize(gameWidth, gameHeight);  
        vidView.setPrefSize(gameWidth, gameHeight);
        vidView.setMaxSize(gameWidth, gameHeight);
        root.getChildren().add(vidView);
    }
	
    /**
     * The constructor of this JavaFX application.
     */
	@Override
	public void start(final Stage primaryStage) {
		game = new AntGame("default");
		primaryStage.setTitle("AntTracer Tools");
		
		// Create and display video selection screen
		primary = primaryStage;
	    init(primaryStage);
	    primaryStage.show();
	}
    
	public void addPath(String vName, Path p) {
		game.addPath(vName, p);
	}
	
	/**
	 * Chain call AntGame's getVideos().
	 * @return List of videos imported into game.
	 */
	public LinkedList<Video> getVideos() {
		return game.getVideos();
	}
	
	public void reselectVid() {
		root.getChildren().remove(gameView);
		root.getChildren().add(vidView);
		
//		gameView = null;	// for now, just remove game instance
	}
	
	public void reselectVideo(Region r) {
		root.getChildren().remove(r);
		root.getChildren().add(vidView);
	}
	
	public void startGame(String videoName) {
		// Find video from game's list of videos by matching name then start game
		for (Video v : game.getVideos()) {
			if (v.getName().equals(videoName)) {
				root.getChildren().remove(vidView);
				
				// Launching/re-launching GameView or choosing another video to play
				if (gameView == null || !videoName.equals(gameView.getVideo().getName())) {
					try {
						gameView = new GameView(this);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if (gameView != null) {
					gameView.setMinSize(gameWidth, gameHeight);  
			        gameView.setPrefSize(gameWidth, gameHeight);
			        gameView.setMaxSize(gameWidth, gameHeight);
			        root.getChildren().add(gameView);
				}
			}
		}
	}
	

	
	public void startVideo(String videoName) {
		// Find video from game's list of videos by matching name then start game
		for (Video v : game.getVideos()) {
			if (v.getName().equals(videoName)) {
				root.getChildren().remove(vidView);
				
				// Launching/re-launching GameView or choosing another video to play
				if (vidAltView == null || !videoName.equals(vidAltView.getVideo().getName())) {
					vidAltView = new VideoAnalysisView(this, v);
				}
				
				if (vidAltView != null) {
					vidAltView.setMinSize(gameWidth, gameHeight);  
					vidAltView.setPrefSize(gameWidth, gameHeight);
					vidAltView.setMaxSize(gameWidth, gameHeight);
			        root.getChildren().add(vidAltView);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
