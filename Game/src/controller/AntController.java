/**
 * @author Paul Shen
 * 
 * Sets up the primary GUI container.  Interchanges the different views.  Invokes calls on the
 * model (e.g. addPath()).  
 */

package controller;

import java.util.LinkedList;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.AntGame;
import model.Click;
import model.Path;
import model.Video;
import view.GameView;

public class AntController extends Application {

	private Group root;
	private AntGame game;
	private GameView gameView;
	
    final double gameWidth = 960;  
    final double gameHeight = 596;
    
    private int playCnt = 0;
    
    /**
     * Helper method to create the video selection screen
     * @param primaryStage
     * @throws Exception 
     */
    private void init(Stage primaryStage) throws Exception {
        root = new Group();
        primaryStage.setScene(new Scene(root, gameWidth, gameHeight));
        gameView = new GameView(this);
		gameView.setMinSize(gameWidth, gameHeight);  
        gameView.setPrefSize(gameWidth, gameHeight);
        gameView.setMaxSize(gameWidth, gameHeight);
        root.getChildren().add(gameView);
    }
	
    /**
     * The constructor of this JavaFX application.
     * @throws Exception 
     */
	@Override
	public void start(Stage primaryStage) throws Exception {
		game = new AntGame("default");
		gameView = null;
		primaryStage.setTitle("AntTracer");
//		primaryStage.setMinWidth(gameWidth);
//		primaryStage.setMinHeight(gameHeight);
//        primaryStage.setMaxWidth(740);	// max dimensions larger than underlying scene, includes OS GUI
//        primaryStage.setMaxHeight(668);
//		primaryStage.setWidth(gameWidth);		// Pre-sized for browser-embedded app
//		primaryStage.setHeight(gameHeight);
		
		// Create and display video selection screen
	    init(primaryStage);
//	    System.out.println(primaryStage.getWidth() > 0);	// detects instance of this application being run from website
//	    System.out.println(Application.getHostServices() == null);	// same^^
	    primaryStage.show();
	}
	
	/**
	 * Chain call AntGame's getVideos().
	 * @return List of videos imported into game.
	 */
	public LinkedList<Video> getVideos() {
		return game.getVideos();
	}
	
    /**
     * Runs verification test on first and last clicks of the path against the stored start and end coordinates of the ant, then
     * sends the path to the CGI server.
     * @param vName
     * @param p
     */
	public void addPath(String vName, Path p) {
		Click first = p.getPath().getFirst();
		Click last = p.getPath().getLast();
		Click start = p.getAnt().getStart();
		Click end = p.getAnt().getEnd();
		
		double distA, distZ;
		distA = Math.sqrt(Math.pow(first.getX() - start.getX(), 2) + Math.pow(first.getY() - start.getY(), 2));
		distZ = Math.sqrt(Math.pow(last.getX() - end.getX(), 2) + Math.pow(last.getY() - end.getY(), 2));
		
		System.out.println("Distances: " + distA + " and " + distZ);
		if (distA > 17 || distZ > 17) {		// tag path name as invalid
			System.out.println("FAILED VERIFICATION");
			System.out.println("Number of successful plays: " + playCnt);
			p.setName(Integer.toString(-1) + " " + p.getName());
		}
		else {
			playCnt++;
			System.out.println("Number of successful plays: " + playCnt);
		}
		
		game.addPath(vName, p);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
