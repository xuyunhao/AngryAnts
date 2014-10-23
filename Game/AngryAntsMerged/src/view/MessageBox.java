package view;

//import java.net.MalformedURLException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MessageBox extends Stage {

	private static String message;

	public MessageBox(String text) {
		message = text;

		this.initModality(Modality.WINDOW_MODAL);
		HBox tmpBtnPanel = new HBox();
		getIcons().add(new Image(getClass().getResource("/img/ant2.png").toString()));

		Button ok = new Button("Ok");
		ok.setPrefSize(60, 30);
		ok.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				close();
			}

		});

		tmpBtnPanel.getChildren().add(ok);

		setScene(new Scene(VBoxBuilder.create()
				.children(new Text(message), tmpBtnPanel).alignment(Pos.CENTER)
				.padding(new Insets(5)).build()));

	}

	public void setSize(int i, int j) {
		this.setWidth(i);
		this.setHeight(j);

	}
}
