/**
 * @author Paul Shen
 * 
 * Sets up the primary GUI container.  Interchanges the different views.  Invokes calls on the
 * model (e.g. addPath()).  
 */

package controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.Stage;
import model.AntGame;
import model.Path;
import model.Profile;
import model.Video;
import view.CreateView;
import view.GameView;
import view.GuestView;
import view.IntroScreen;
import view.PlayerPane;
import view.Tutorial;
import view.UserView;
//import view.PlayerPsane;
import view.VideoView;

import javafx.event.EventHandler;
import javafx.scene.media.AudioClip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;

public class AntController extends Application {

	private Group root, root2, root3, root4, root5, root6;
	private AntGame game;
	private VideoView vidView;
	private GameView gameView;
	private IntroScreen intro;
	private UserView uView;
	private CreateView cView;
	private GuestView gView;
	private Tutorial tutorial;
	private Stage mainStage;
	private Scene s;
	private Media musicFile;
	private MediaPlayer musicPlayer;
	private ServerCommunication server;
	private Profile profile;
	private static final String MEDIA_URL = "http://download.oracle.com/otndocs/javafx/JavaRap_ProRes_H264_768kbit_Widescreen.mp4";
	private MediaPlayer mediaPlayer;
	final double mediaWidth = 960;
	final double mediaHeight = 596;

	public void play() {
		Status status = mediaPlayer.getStatus();
		if (status == Status.UNKNOWN || status == Status.HALTED) {
			return;
		}
		if (status == Status.PAUSED || status == Status.STOPPED
				|| status == Status.READY) {
			mediaPlayer.play();
		}
	}

	// End Test

	/**
	 * Helper method to create the video selection screen
	 * 
	 * @param primaryStage
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	private void init(final Stage primaryStage) throws MalformedURLException,
			URISyntaxException {
		server = new ServerCommunication(this);
		root = new Group();
		root2 = new Group();
		root3 = new Group();
		root4 = new Group();
		root5 = new Group();
		root6 = new Group();
		cView = new CreateView(this, mediaWidth, mediaHeight);
		uView = new UserView(this, mediaWidth, mediaHeight);
		intro = new IntroScreen(this, mediaWidth, mediaHeight);
		gView = new GuestView(this, mediaWidth, mediaHeight);
		tutorial = new Tutorial(this, mediaWidth, mediaHeight);
		s = new Scene(root);
		vidView = new VideoView(this, game.getVideos());
		vidView.setMinSize(mediaWidth, mediaHeight);
		vidView.setPrefSize(mediaWidth, mediaHeight);
		vidView.setMaxSize(mediaWidth, mediaHeight);
		root.getChildren().add(intro);

		s.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				// System.out.println(event.getCode());
			}

		});
		
		mainStage.setScene(s);
		File mFile = new File(System.getProperty("user.dir")
				+ "/bin/sound/ElectroSwing.mp3");
		musicFile = new Media(mFile.toURI().toURL().toString());
		musicPlayer = new MediaPlayer(musicFile);
		musicPlayer.play();
	}

	/**
	 * The constructor of this JavaFX application.
	 */
	@Override
	public void start(Stage primaryStage) {
		mainStage = primaryStage;
		File file = new File(System.getProperty("user.dir")
				+ "/bin/img/ant2.png");
		try {
			mainStage.getIcons()
					.add(new Image(file.toURI().toURL().toString()));
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		game = new AntGame("default", this);
		gameView = null;
		primaryStage.setTitle("Angry Ants");
		// primaryStage.setMaxWidth(740); // max dimensions larger than actual
		// GUI components
		// primaryStage.setMaxHeight(668);

		// Create and display video selection screen
		try {
			init(primaryStage);
		} catch (MalformedURLException | URISyntaxException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in start method with init()");
			e.printStackTrace();
		}
		primaryStage.show();
	}

	public void addPath(String vName, Path p) {
		game.addPath(vName, p);
	}

	public void reselectVid() {
		root.getChildren().remove(gameView);
		root.getChildren().add(vidView);

		// gameView = null; // for now, just remove game instance
	}

	//Attempt at setting video to video requested from Sever
	
//	public void startGame() {
//		// Find video from game's list of videos by matching name then start
//		// game
////		for (Video v : game.getVideos()) {
//		Video v = game.getVideos().get(profile.getVid());
////			if (v.getName().equals(videoName)) {
//				root.getChildren().remove(vidView);
//
//				if (v.getName() == "test") {
//					mediaPlayer = new MediaPlayer(new Media(MEDIA_URL));
//					mediaPlayer.setAutoPlay(true);
//					PlayerPane playerPane = new PlayerPane(mediaPlayer);
//					playerPane.setMinSize(mediaWidth, mediaHeight);
//					playerPane.setPrefSize(mediaWidth, mediaHeight);
//					playerPane.setMaxSize(mediaWidth, mediaHeight);
//					root.getChildren().add(playerPane);
//					return;
//				}
//
//				// Launching/re-launching GameView or choosing another video to
//				// play
//				if (gameView == null) {
//					gameView = new GameView(this, v);
//				}
//
//				if (gameView != null) {
//					gameView.setMinSize(mediaWidth, mediaHeight);
//					gameView.setPrefSize(mediaWidth, mediaHeight);
//					gameView.setMaxSize(mediaWidth, mediaHeight);
//					root.getChildren().add(gameView);
//				}
////			}
////		}
//	}

	public void startGame(String videoName) {
		// Find video from game's list of videos by matching name then start
		// game
		for (Video v : game.getVideos()) {
			if (v.getName().equals(videoName)) {
				root.getChildren().removeAll();
				//root.getChildren().remove(vidView);

				if (v.getName() == "test") {
					mediaPlayer = new MediaPlayer(new Media(MEDIA_URL));
					mediaPlayer.setAutoPlay(true);
					PlayerPane playerPane = new PlayerPane(mediaPlayer);
					playerPane.setMinSize(mediaWidth, mediaHeight);
					playerPane.setPrefSize(mediaWidth, mediaHeight);
					playerPane.setMaxSize(mediaWidth, mediaHeight);
					root.getChildren().add(playerPane);
					return;
				}

				// Launching/re-launching GameView or choosing another video to
				// play
				if (gameView == null) {
					gameView = new GameView(this, v, profile);
				}

				if (gameView != null) {
					gameView.setMinSize(mediaWidth, mediaHeight);
					gameView.setPrefSize(mediaWidth, mediaHeight);
					gameView.setMaxSize(mediaWidth, mediaHeight);
					root.getChildren().add(gameView);
				}
			}
		}
	}

	
	public static void main(String[] args) {
		launch(args);
	}

	public void guestStart() {
		root.getChildren().remove(intro);
		root.getChildren().add(gView);
		mainStage.setScene(s);
		mainStage.centerOnScreen();

	}

	public void userStart() {
		//Skip vidView and goes into gameView
		root.getChildren().remove(uView);
		startGame(game.getVideos().get(profile.getVid()).getName());
		//mainStage.setScene(s);
		mainStage.centerOnScreen();
	}

	public void userScreen() {
		root.getChildren().remove(intro);
		root.getChildren().add(uView);
		mainStage.centerOnScreen();

	}

	public void setUser(String typeRequest, String user, String pass) {
		String response = server.sendRequest(typeRequest, user, pass);
		if (response.equals("AlreadyExists")) {
			cView.popUp();
		} else {
			root.getChildren().remove(cView);
			root.getChildren().add(intro);
			mainStage.centerOnScreen();
		}
	}

	public void createUser() {
		root.getChildren().remove(intro);
		root.getChildren().add(cView);
		//mainStage.setScene(cView);
		mainStage.centerOnScreen();

	}

	public void tutStart() {
		if(root.getChildren().contains(uView)){
			root.getChildren().remove(uView);
		}else if(root.getChildren().contains(gView)){
			root.getChildren().remove(gView);
		}
		root.getChildren().add(tutorial);
		//mainStage.setScene(tutorial);
		mainStage.centerOnScreen();
	}

	public void sendToServer(String typeRequest, String user, String pass) {
		String response1 = server.sendRequest(typeRequest, user, pass);
		if (response1.equals("LoginIncorrect")) {
			intro.popUp();
		} else {
			String response2 = server.sendRequest("PICK", "", "");
			profile = new Profile(getCred(response1), getVid(response2), getVid2(response2),
					getAnt(response2), getScore(response1), getMult(response1));
			System.out.println("Profile name is:" + profile.getName());
			System.out.println("Ant choosen is:" + Integer.toString(profile.getAnt()));
			System.out.println("Vid choosen is:" + Integer.toString(profile.getVid()));
			userScreen();
		}
	}

	private String getCred(String response) {
		String cred = "Credential:";
		int begin = response.indexOf(cred) + cred.length();
		int end = response.indexOf("HighestScore:") - 1;
		return response.substring(begin, end);
	}

	private int getMult(String response) {
		String hMult = "HighestMultiplier:";
		int begin = response.indexOf(hMult) + hMult.length();
		int end = response.length()-1;
		return Integer.parseInt(response.substring(begin, end));
	}

	private int getScore(String response) {
		String hScore = "HighestScore:";
		int begin = response.indexOf(hScore) + hScore.length();
		int end = response.indexOf("HighestMultiplier:") - 1;
		return Integer.parseInt(response.substring(begin, end));
	}

	private int getAnt(String response) {
		String ant = "aid:";
		int begin = response.indexOf(ant) + ant.length();
		int end = response.length()-1;
		return Integer.parseInt(response.substring(begin, end));
	}

	private int getVid(String response) {
		String vid = "vid:";
		int begin = response.indexOf(vid) + vid.length();
		int end = response.indexOf("vid2:") - 1;
		return Integer.parseInt(response.substring(begin, end));
	}

	private String getVid2(String response) {
		String vid2 = "vid2:";
		int begin = response.indexOf(vid2) + vid2.length();
		int end = response.indexOf("aid:") - 1;
		return response.substring(begin, end);
	}

	public void playSound() throws MalformedURLException {
		File file2 = new File(System.getProperty("user.dir")
				+ "/bin/sound/tada.wav");
		AudioClip tada = new AudioClip(file2.toURI().toURL().toString());
		tada.play();
	}
	
	/*
	 * This is used to synchronize music players between screens
	 * by returning what musicPlayer's volume is.
	 */
	public double getMusicVolume() {
		return musicPlayer.getVolume();
	}

	public void toggleSound() {
		if (musicPlayer.getVolume() == 0.0) {
			musicPlayer.setVolume(1.0);
		} else {
			musicPlayer.setVolume(0.0);
		}

	}

	public void gotoIntro() {
		root.getChildren().remove(gameView);
		root.getChildren().remove(cView);
		root.getChildren().add(intro);
		//mainStage.setScene(intro);
		mainStage.centerOnScreen();

	}

	public void uploadContents(String output) {
		server.uploadFile(output, profile.getName(),
				Integer.toString(profile.getVid()),
				Integer.toString(profile.getAnt()),
				profile.getvid2());

	}

}
