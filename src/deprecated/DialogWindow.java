package deprecated;
/**@deprecated
 * @author: Kevin Daniel Velazquez Vega
 * Class to show a dialog window with options to customize the program.
 */
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DialogWindow extends Stage{
	private Stage dialog;
	private GridPane gpane;
	private double longitude;
	private double latitude;
	private int hour;
	private int minute;
	private int seconds;
	private int month;
	private int day;
	private int year;
	
	/**
	 * Constructor for DialogWindow, it initializes objects, and shows the window.
	 */
	public DialogWindow() {
		dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		addButtons(dialog);
		Scene scene = new Scene(gpane);
		dialog.setTitle("Settings");
		dialog.setScene(scene);
		dialog.showAndWait();
		
	}
	/**
	 * Add all the buttons, labels and text fields to the dialog window.
	 * @param dialog
	 */
	public void addButtons(Stage dialog) {
		gpane = new GridPane();
		gpane.setPadding(new Insets(20, 20, 20, 20));
		gpane.setHgap(10);
		gpane.setVgap(20);
		
		FlowPane first = new FlowPane();
		first.setHgap(10);
		Label longitudLabel = new Label("Longitude: ");
		first.getChildren().add(longitudLabel);
		TextField longitudField = new TextField();
		first.getChildren().add(longitudField);
		gpane.add(first, 0, 0);
		
		FlowPane second = new FlowPane();
		second.setHgap(20);
		Label latitudeLabel = new Label("Latitude: ");
		second.getChildren().add(latitudeLabel);
		TextField latitudeField = new TextField();
		second.getChildren().add(latitudeField);
		gpane.add(second, 0, 1);
		
		FlowPane third = new FlowPane();
		third.setHgap(37);
		Label dateLabel = new Label("Date: ");
		third.getChildren().add(dateLabel);
		TextField dateField = new TextField("MM/DD/YYYY");
		third.getChildren().add(dateField);
		gpane.add(third, 0, 2);
		
		FlowPane fourth = new FlowPane();
		fourth.setHgap(34);
		Label hourLabel = new Label("Hour: ");
		fourth.getChildren().add(hourLabel);
		TextField hourField = new TextField();
		fourth.getChildren().add(hourField);
		gpane.add(fourth, 0, 3);
		
		FlowPane fifth = new FlowPane();
		fifth.setHgap(18);
		Label minuteLabel = new Label("Minutes: ");
		fifth.getChildren().add(minuteLabel);
		TextField minuteField = new TextField();
		fifth.getChildren().add(minuteField);
		gpane.add(fifth, 0, 4);
		
		FlowPane sixth = new FlowPane();
		sixth.setHgap(15);
		Label secondsLabel = new Label("Seconds: ");
		sixth.getChildren().add(secondsLabel);
		TextField secondsField = new TextField();
		sixth.getChildren().add(secondsField);
		gpane.add(sixth, 0, 5);
		
		FlowPane seventh = new FlowPane();
		seventh.setHgap(10);
		Button okButton = new Button("OK");
		seventh.getChildren().add(okButton);
		gpane.add(seventh, 0, 6);

	}
	/**
	 * Add event handler for the OK button, it initializes the variables to the user input from the text fields.
	 * @param ok
	 * @param longitudeField
	 * @param latitudeField
	 * @param dateField
	 * @param hourField
	 * @param minuteField
	 * @param secondsField
	 */
	public void okEventHandler(Button ok, TextField longitudeField, TextField latitudeField, TextField dateField, TextField hourField, TextField minuteField, TextField secondsField) {
		ok.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				longitude = Double.valueOf(longitudeField.getText());
				latitude = Double.valueOf(latitudeField.getText());
				String[] dateTemp = dateField.getText().split("/");
				month = Integer.valueOf(dateTemp[0]);
				day = Integer.valueOf(dateTemp[1]);
				year = Integer.valueOf(dateTemp[2]);
				hour = Integer.valueOf(hourField.getText());
				minute = Integer.valueOf(minuteField.getText());
				seconds = Integer.valueOf(secondsField.getText());
				dialog.close();
			}
		});
	}
	
	/**
	 * Getter method for longitude
	 * @return longitude
	 */
	public double getLongitude() {
		return longitude;
	}
	
	/**
	 * Getter method for latitude
	 * @return latitude
	 */
	public double getLatitude() {
		return latitude;
	}
	
	/**
	 * Getter method for month
	 * @return month
	 */
	public int getMonth() {
		return month;
	}
	
	/**
	 * Getter method for day
	 * @return day
	 */
	public int getDay() {
		return day;
	}
	
	/**
	 * Getter method for year
	 * @return year
	 */
	public int getYear() {
		return year;
	}
	
	/**
	 * Getter method for hour
	 * @return hour
	 */
	public int getHour() {
		return hour;
	}
	
	/**
	 * Getter method for minute
	 * @return minute
	 */
	public int getMinute() {
		return minute;
	}
	
	/**
	 * Getter method for seconds
	 * @return seconds
	 */
	public int getSeconds() {
		return seconds;
	}
}
