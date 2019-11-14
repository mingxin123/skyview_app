package Core;
/**
 * @author Lou Shin, Ivan Ko, Ming Xin, Kevin Velazquez
 * SkyView class acts like the view for our program, it has methods to draw the stars, constellations, deep sky objects and planets on the canvas according to 
 * what the user chooses on the customize window.
 * The user can choose to have customize the program to show what he/she wants. (This window can be accessed by clicking anywhere inside the Sky View window.)
 */
import java.util.HashMap;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SkyView extends Application {

	private Sky sky = new Sky();
	private MoonPhase moon = new MoonPhase();
	ResizableCanvas canvas = new ResizableCanvas();
	private double magnitude = 8, factor = 6, zoom = 1, x = 0, y = 0, mouseX = 0, mouseY = 0;
	//Array of booleans that contains the options for the Customize windows. It defaults to this values.
	boolean[] opt = new boolean[] { true, true, true, false, true, true, true, false, true, false, false, false,
			false };

	public class ResizableCanvas extends Canvas {

		private HashMap<Integer, String> starNames;
		//Labels for the stars in the messier catalogue.
		private String[] messierLabels = new String[] { "Nebula", "Globular Cluster\nCrab Nebula", "Globular Cluster",
				"Globular Cluster", "Globular Cluster", "Open Cluster\nButterfly Cluster",
				"Open Cluster\nThe Scorpion's Tail", "Nebula\nLagoon Nebula", "Globular Cluster", "Globular Cluster",
				"Open Cluster\nWild Duck Cluster", "Globular Cluster", "Globular Cluster\nHercules Globular Cluster",
				"Globular Cluster", "Globular Cluster", "Open Cluster\nAssociated with the Eagle Nebula",
				"Open Cluster\nOmega Swan Horseshoe or Lobster Nebula", "Open Cluster", "Globular Cluster",
				"Open Cluster\nTrifid Nebula", "Open Cluster", "Globular Cluster", "Open Cluster",
				"Open Cluster\nSagittarius Star Cloud", "Open Cluster", "Open Cluster", "Nebula\nDumbbell Nebula",
				"Globular Cluster", "Open Cluster", "Globular Cluster", "Galaxy\nAndromeda Galaxy",
				"Galaxy\nA Satellite of the Andromeda Galaxy M31", "Galaxy\nTriangulum Galaxy", "Open Cluster",
				"Open Cluster", "Open Cluster", "Open Cluster", "Open Cluster", "Open Cluster",
				"Open Cluster\nWinnecke 4", "Open Cluster", "Nebula\nOrion Nebula",
				"Nebula\nDeMairan's Nebula part of Orion Nebula", "Open Cluster\nBeehive Cluster Praesepe",
				"Open Cluster\nPleiades", "Open Cluster", "Open Cluster", "Open Cluster", "Galaxy", "Open Cluster",
				"Galaxy\nWhirlpool Galaxy", "Open Cluster", "Globular Cluster", "Globular Cluster", "Globular Cluster",
				"Globular Cluster", "Nebula\nRing Nebula", "Galaxy", "Galaxy", "Galaxy", "Galaxy", "Globular Cluster",
				"Sunflower Galaxy", "Galaxy\nBlackeye Galaxy", "Galaxy\nIn the Leo Triplett",
				"Galaxy\nIn the Leo Triplett", "Open Cluster", "Globular Cluster", "Globular Cluster",
				"Globular Cluster", "Globular Cluster", "Globular Cluster", "Open Cluster", "Galaxy",
				"Globular Cluster", "Nebula\nLittle Dumbbell Nebula", "Galaxy\nCetus A", "Nebula", "Globular Cluster",
				"Globular Cluster", "Galaxy\nBode's Galaxy", "Galaxy\nCigar Galaxy", "Galaxy\nSouthern Pinwheel",
				"Galaxy", "Galaxy", "Galaxy", "Galaxy\nVirgo A", "Galaxy", "Galaxy", "Galaxy", "Galaxy",
				"Globular Cluster", "Open Cluster", "Galaxy", "Galaxy", "Galaxy", "Nebula\nOwl Nebula", "Galaxy",
				"Galaxy", "Galaxy", "Galaxy\nPinwheel Galaxy", "Galaxy\nMay duplicate M101", "Open Cluster",
				"Galaxy\nSombrero Galaxy", "Galaxy", "Galaxy", "Globular Cluster", "Galaxy", "Galaxy",
				"Galaxy\nA Satellite of the Andromeda Galaxy M31" };
		private String[] caldwellLabels = new String[] { "Open Cluster", "Planetary Nebula\nBow-Tie Nebula",
				"Barred Spiral Galaxy", "Open Cluster and Nebula\nIris Nebula", "Spiral Galaxy",
				"Planetary Nebula\nCat's Eye Nebula", "Spiral Galaxy", "Open Cluster", "Nebula\nCave Nebula",
				"Open Cluster", "Nebula\nBubble Nebula", "Spiral Galaxy\nFireworks Galaxy",
				"Open Cluster\nOwl Cluster, E.T. Cluster", "Open Cluster\nDouble Cluster, H & Ï? Persei",
				"Planetary Nebula\nBlinking Planetary", "Open Cluster", "Dwarf Spheroidal Galaxy",
				"Dwarf Spheroidal Galaxy", "Open Cluster and Nebula\nCocoon Nebula", "Nebula\nNorth America Nebula",
				"Irregular galaxy", "Planetary Nebula\nBlue Snowball", "Spiral Galaxy", "Type-cD galaxy\nPerseus A",
				"Globular Cluster", "Spiral Galaxy", "Nebula\nCrescent Nebula", "Open Cluster", "Spiral Galaxy",
				"Spiral Galaxy", "Nebula\nFlaming Star Nebula", "Barred Spiral Galaxy\nWhale Galaxy",
				"Supernova Remnant\nEast Veil Nebula", "Supernova Remnant\nWest Veil Nebula", "Type-cD galaxy",
				"Spiral Galaxy", "Open Cluster", "Spiral Galaxy\nNeedle Galaxy",
				"Planetary Nebula\nEskimo Nebula/Clown Face Nebula", "Spiral Galaxy", "Open Cluster\nHyades",
				"Globular Cluster", "Spiral Galaxy", "Barred Spiral Galaxy", "Spiral Galaxy",
				"Nebula\nHubble's Variable Nebula", "Globular Cluster", "Spiral Galaxy", "Nebula\nRosette Nebula",
				"Open Cluster", "Irregular galaxy", "Elliptical galaxy", "Lenticular Galaxy\nSpindle Galaxy",
				"Open Cluster", "Planetary Nebula\nSaturn Nebula", "Planetary Nebula",
				"Barred irregular galaxy\nBarnard's Galaxy", "Open Cluster", "Planetary Nebula\nGhost of Jupiter",
				"Galaxy\nAntennae Galaxies", "Interacting galaxy\nAntennae Galaxies", "Spiral Galaxy",
				"Planetary Nebula\nHelix Nebula", "Open Cluster and Nebula", "Galaxy\nSculptor Galaxy",
				"Globular Cluster", "Galaxy", "Nebula\nR CrA Nebula", "Planetary Nebula\nBug Nebula", "Galaxy",
				"Open Cluster", "Galaxy", "Globular Cluster", "Planetary Nebula\nEight Burst Nebula", "Open Cluster",
				"Open Cluster and Nebula", "Galaxy\nCentaurus A", "Globular Cluster", "Globular Cluster",
				"Globular Cluster\nOmega Centauri", "Globular Cluster", "Open Cluster", "Galaxy", "Globular Cluster",
				"Open Cluster\nOmicron Velorum Cluster", "Globular Cluster", "Globular Cluster", "Open Cluster",
				"Open Cluster\nS Norma Cluster", "Planetary Nebula", "Open Cluster\nWishing Well Cluster",
				"Nebula\nEta Carinae Nebula", "Globular Cluster", "Open Cluster\nJewel Box", "Open Cluster",
				"Open Cluster", "Open Cluster\nPearl Cluster", "Open Cluster", "Dark Nebula\nCoalsack Nebula",
				"Open Cluster and Nebula\nLambda Centauri Nebula", "Galaxy", "Open Cluster\nTheta Car Cluster",
				"Open Cluster and Nebula\nTarantula Nebula", "Globular Cluster", "Globular Cluster",
				"Globular Cluster\n47 Tucanae", "Globular Cluster", "Globular Cluster", "Planetary Nebula" };
		private String mp[] = { "New moon", "Waxing crescent", "First quarter", "Waxing gibbous", "Full moon",
				"Waning gibbous", "Last quarter", "Waning crescent" };

		/**
		 * Constructor the ResizableCanvas inner class. This class has method to redraw the canvas if the window size is changed.
		 */
		public ResizableCanvas() {
			// Redraw canvas when size changes.
			widthProperty().addListener(evt -> draw());
			heightProperty().addListener(evt -> draw());
			//HashMap of the Stars ID to their names. (Most popular stars)
			starNames = new HashMap<Integer, String>();
			starNames.put(7588, "Achernar");
			starNames.put(11767, "Polaris");
			starNames.put(21421, "Aldebaran");
			starNames.put(24436, "Rigel");
			starNames.put(24608, "Capella");
			starNames.put(27989, "Betelgeuse");
			starNames.put(30438, "Canopus");
			starNames.put(32349, "Sirius");
			starNames.put(33579, "Adara");
			starNames.put(37279, "Procyon");
			starNames.put(37826, "Pollux");
			starNames.put(49669, "Regulus");
			starNames.put(62434, "Mimosa");
			starNames.put(65378, "Mizar");
			starNames.put(65474, "Spica");
			starNames.put(68702, "Hadar");
			starNames.put(69673, "Arcturus");
			starNames.put(71683, "Alpha Centauri A");
			starNames.put(80763, "Antares");
			starNames.put(85927, "Shaula");
			starNames.put(91262, "Vega");
			starNames.put(97649, "Altair");
			starNames.put(102098, "Deneb");
			starNames.put(113368, "Fomalhaut");
		}

		/**
		 *This method draws everything we need (stars, constellations, deep sky objects, planets, sun and moon)
		 */
		private void draw() {
			double width = getWidth() * zoom;
			double height = getHeight() * zoom;
			GraphicsContext gc = getGraphicsContext2D();

			gc.setTextAlign(TextAlignment.CENTER);
			gc.setTextBaseline(VPos.BOTTOM);
			gc.setFont(new Font("Aril", 14));
			gc.setFill(Color.color(0, 0, .15));
			gc.fillRect(0, 0, width, height);
			gc.setLineCap(StrokeLineCap.ROUND);

			Stop[] stops = new Stop[] { new Stop(0, Color.color(0, 0, 0, .9)), new Stop(.45, Color.color(0, 0, 0, 0)),
					new Stop(.5, Color.color(1, 1, 1, .15)), new Stop(.8, Color.color(0, 0, 0, 0)) };
			RadialGradient lg1 = new RadialGradient(0, 0, .5, .5, .5, true, CycleMethod.NO_CYCLE, stops);
			gc.setFill(lg1);
			gc.beginPath();
			gc.arc(width / 2 + x, height / 2 + y, Math.min(width, height), Math.min(width, height), 0, 360);
			gc.closePath();
			gc.fill();

			HashMap<Integer, double[]> stars = sky.getStars(width, height, magnitude);
			//Option to show messier object.
			if (opt[6]) {
				HashMap<Integer, double[]> messier = sky.getMessier(width, height, magnitude);
				for (int id : messier.keySet()) {
					double[] i = messier.get(id);
					if (i[2] <= magnitude) {
						double d = zoom * factor * .8 * Math.max(3 - i[2] / 2.1, .5) * Math.exp(-(90 - i[3]) * .01);
						gc.setLineWidth(d);
						gc.setStroke(Color.color(.7, .2, .7, Math.min(Math.max(1 - i[2] / 6, .5), 1)));
						gc.strokeLine(i[0] + x, i[1] + y, i[0] + x, i[1] + y);
						//Option to show messier catalog star labels.
						if (opt[7]) {
							gc.setLineWidth(4);
							gc.setFill(gc.getStroke());
							gc.setStroke(Color.color(0, 0, 0, 0.5));
							gc.strokeText("M" + id + "\n" + messierLabels[id - 1], i[0] + x, i[1] + y - d);
							gc.fillText("M" + id + "\n" + messierLabels[id - 1], i[0] + x, i[1] + y - d);
						}
					}
				}
			}
			//Show cadwell catalog stars.
			if (opt[8]) {
				HashMap<Integer, double[]> caldwell = sky.getCaldwell(width, height, magnitude);
				for (int id : caldwell.keySet()) {
					double[] i = caldwell.get(id);
					if (i[2] <= magnitude) {
						double d = zoom * factor * .8 * Math.max(3 - i[2] / 2.1, .5) * Math.exp(-(90 - i[3]) * .01);
						gc.setLineWidth(d);
						gc.setStroke(Color.color(.7, .3, .2, Math.min(Math.max(1 - i[2] / 6, .5), 1)));
						gc.strokeLine(i[0] + x, i[1] + y, i[0] + x, i[1] + y);

						if (opt[9]) {
							gc.setLineWidth(4);
							gc.setFill(gc.getStroke());
							gc.setStroke(Color.color(0, 0, 0, 0.5));
							gc.strokeText("C" + id + "\n" + caldwellLabels[id - 1], i[0] + x, i[1] + y - d);
							gc.fillText("C" + id + "\n" + caldwellLabels[id - 1], i[0] + x, i[1] + y - d);
						}
					}
				}
			}
			//Option to show constellations
			if (opt[2]) {
				gc.setLineWidth(1.5 * zoom);
				gc.setStroke(Color.color(1, 1, 1, .3));
				int[][] lines = sky.getLines();
				for (int[] i : lines) {
					if (stars.get(i[0]) != null && stars.get(i[1]) != null
							&& Math.abs(stars.get(i[0])[0] - stars.get(i[1])[0]) < width / 2
							&& Math.abs(stars.get(i[0])[1] - stars.get(i[1])[1]) < height / 2)
						gc.strokeLine(stars.get(i[0])[0] + x, stars.get(i[0])[1] + y, stars.get(i[1])[0] + x,
								stars.get(i[1])[1] + y);
				}
				//Option to show constellation labels
				if (opt[3]) {
					gc.setTextBaseline(VPos.CENTER);
					HashMap<String, double[]> constellationLabels = sky.getConstellationLabels(width, height);
					gc.setLineWidth(4);
					gc.setStroke(Color.color(0, 0, 0, 0.5));
					gc.setFill(Color.color(1, 1, 1, .3));
					for (String l : constellationLabels.keySet()) {
						double[] i = constellationLabels.get(l);
						gc.fillText(l, i[0] + x, i[1] + y);
					}
					gc.setTextBaseline(VPos.BOTTOM);
				}
			}

			for (int id : stars.keySet()) {
				double[] i = stars.get(id);
				if (i[2] <= magnitude) {
					double d = zoom * factor * .8 * Math.max(3 - i[2] / 2.1, .5) * Math.exp(-(90 - i[3]) * .01);
					gc.setLineWidth(d);
					gc.setStroke(Color.color(1, 1, 1, Math.min(Math.max(1 - i[2] / 6, .5), 1)));
					gc.strokeLine(i[0] + x, i[1] + y, i[0] + x, i[1] + y);
					//Option one = show star labels.
					if (opt[1] && starNames.containsKey(id)) {
						gc.setLineWidth(4);
						gc.setFill(gc.getStroke());
						gc.setStroke(Color.color(0, 0, 0, 0.5));
						gc.strokeText(starNames.get(id), i[0] + x, i[1] + y - d);
						gc.fillText(starNames.get(id), i[0] + x, i[1] + y - d);
					}
				}
			}

			if (magnitude >= 4.5) {
				HashMap<Integer, double[]> fStars = sky.getFineStars(width, height, magnitude);
				for (int id : fStars.keySet()) {
					double[] i = fStars.get(id);
					if (i[2] <= magnitude) {
						gc.setLineWidth(
								zoom * factor * .8 * Math.max(3 - i[2] / 2.1, .5) * Math.exp(-(90 - i[3]) * .01));
						gc.setStroke(Color.color(1, 1, 1, Math.min(Math.max(1 - i[2] / 6, .5), 1)));
						gc.strokeLine(i[0] + x, i[1] + y, i[0] + x, i[1] + y);
					}
				}
			}
			//Option to show planets
			if (opt[4]) {
				Planet[] planets = sky.getPlanets(width, height);
				for (Planet p : planets) {
					if (p.mag <= magnitude) {
						double d = zoom * factor
								* Math.max(.8 * Math.max(3 - p.mag / 2.1, .5) * Math.exp(-(90 - p.mag) * .01), .6);
						gc.setLineWidth(d);
						gc.setStroke(p.color);
						if (p.name.equals("Moon")) {
							gc.setLineWidth(d + 2);
							gc.setStroke(Color.color(1, 1, 1, .3));
							gc.strokeLine(p.x + x, p.y + y, p.x + x, p.y + y);
							gc.setLineWidth(d);
							gc.setStroke(Color.BLACK);
							gc.strokeLine(p.x + x, p.y + y, p.x + x, p.y + y);
							moon.setDateAndUpdate(sky.getJD());
							gc.setFill(p.color);
							moonPhace(moon.getMoonAgeInDays(), gc, p.x + x, p.y + y, d / 2);
						} else
							gc.strokeLine(p.x + x, p.y + y, p.x + x, p.y + y);
						if (opt[5]) {
							gc.setLineWidth(4);
							gc.setFill(p.color);
							gc.setStroke(Color.color(0, 0, 0, 0.5));
							String name = p.name;
							if (p.name.equals("Moon"))
								name += "\n" + mp[(int) (moon.getMoonAgeInDays() * 8 / 29.531)];
							gc.strokeText(name, p.x + x, p.y + y - d);
							gc.fillText(name, p.x + x, p.y + y - d);
						}
					}
				}
			}
			//if option to show below the horizon is not turned on.
			if (!opt[0]) {
				gc.setFill(Color.color(0, 0, .15));
				gc.beginPath();
				gc.arc(width / 2 + x, height / 2 + y, Math.min(width, height) / 2, Math.min(width, height) / 2, 0, 360);
				gc.rect(0, 0, width, height);
				gc.closePath();
				gc.fill();

				Stop[] stops2 = new Stop[] { new Stop(.49999, Color.color(0, 0, 0, 0)),
						new Stop(.5, Color.color(1, 1, 1, 0.15)), new Stop(.8, Color.color(0, 0, 0, 0)) };
				RadialGradient lg12 = new RadialGradient(0, 0, .5, .5, .5, true, CycleMethod.NO_CYCLE, stops2);
				gc.setFill(lg12);
				gc.beginPath();
				gc.arc(width / 2 + x, height / 2 + y, Math.min(width, height), Math.min(width, height), 0, 360);
				gc.closePath();
				gc.fill();
			}

			gc.setTextAlign(TextAlignment.LEFT);
			gc.setTextBaseline(VPos.TOP);
			gc.setFont(new Font("Aril", 12));
			gc.setLineWidth(4);
			gc.setFill(Color.color(1, 1, 1, .7));
			gc.setStroke(Color.color(0, 0, 0, .3));
			String info = sky.getClock().toString() + "\n" + String.format("%.2f", sky.getLatitude()) + ", "
					+ String.format("%.2f", sky.getLongitude()) + "\n" + String.format("%.2f", sky.getAzOffset());
			gc.strokeText(info, 5, 5);
			gc.fillText(info, 5, 5);
		}
		
		/**
		 * Method the return if the canvas is resizable (always returns true)
		 */
		@Override
		public boolean isResizable() {
			return true;
		}

		/**
		 * Method to return the width.
		 */
		public double prefWidth(double height) {
			return getWidth();
		}
		
		/**
		 * Method to return the height
		 */
		@Override
		public double prefHeight(double width) {
			return getHeight();
		}

		private void moonPhace(double date, GraphicsContext gc, double x, double y, double d) {
			gc.beginPath();
			int n = 1;
			if (date > 14.765) {
				n = 3;
				gc.arc(x, y, d, d, -90, -180);
			} else
				gc.arc(x, y, d, d, -90, 180);
			gc.arc(x, y, (n - date / 7.3825) * d, d, 90, -180);
			gc.arc(x, y, (date / 7.3825 - n) * d, d, 90, 180);
			gc.closePath();
			gc.fill();
		}
	}

	@Override
	public void start(Stage stage) {
		StackPane stackPane = new StackPane(canvas);
		// Bind canvas size to stack pane size.
		canvas.widthProperty().bind(stackPane.widthProperty());
		canvas.heightProperty().bind(stackPane.heightProperty());
		
		//event handler for mouse scroll (zooming in or out)
		canvas.setOnScroll(new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				double zoomFactor = .0003;
				double scale = zoom;
				scale += -event.getDeltaY() * zoomFactor;
				if (scale <= 1 || scale >= 4)
					scale = scale < 1 ? 1 : 4;
				if (Math.abs(zoom - scale) < 0.5)
					zoom = scale;
				x = -mouseX * (zoom - 1);
				y = -mouseY * (zoom - 1);

				if (Math.abs(event.getDeltaX()) > 25)
					sky.setAzOffset(sky.getAzOffset() + event.getDeltaX() * .1 / zoom);
				canvas.draw();
			}
		});
		
		//Event handler for moving the mouse inside the canvas.
		canvas.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mouseX = event.getSceneX();
				mouseY = event.getSceneY();
				x = -mouseX * (zoom - 1);
				y = -mouseY * (zoom - 1);
				canvas.draw();
			}
		});

		//Timeline object to create an animation of the sky moving depending on the option selected.
		final Timeline timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> {
			//Option to show the sky in real time (live)
			if (opt[12]) {
				sky.setClock(new Date());
				canvas.draw();
			//Option to show the sky in the "past" (rotate to left)
			} else if (opt[10]) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(sky.getClock());
				cal.add(Calendar.MINUTE, -1);
				sky.setClock(cal.getTime());
				canvas.draw();
			//Option to show the sky in the "future" (rotate to right)
			} else if (opt[11]) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(sky.getClock());
				cal.add(Calendar.MINUTE, 1);
				sky.setClock(cal.getTime());
				canvas.draw();
			}
		}));
		//Animation will go on indefinitely, can be stopped by choosing another option.
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();

		Customize customize = new Customize(stage);
		canvas.setOnMouseClicked(e -> {
			customize.setResizable(false);
			customize.reset();
			customize.show();
		});
		stage.setScene(new Scene(stackPane, 500, 500));
		stage.setTitle("Sky View");
		stage.show();
	}

	//Private inner class to show a customization window when you click on the canvas
	private class Customize extends Stage {

		final TextField Date = new TextField();
		final TextField Location = new TextField();
		final TextField AzOffset = new TextField();
		final TextField Magnitude = new TextField();
		final TextField Scale = new TextField();
		final CheckBox[] c = new CheckBox[14];

		/**
		 * Constructor for Customize inner class
		 * @param owner
		 */
		public Customize(Stage owner) {
			super();
			initOwner(owner);
			setTitle("Customize");
			initModality(Modality.APPLICATION_MODAL);
			Group root = new Group();
			Scene scene = new Scene(root);
			setScene(scene);
			//create a grid pane to show all the options available.
			GridPane gridpane = new GridPane();
			gridpane.setPadding(new Insets(20));
			gridpane.setHgap(15);
			gridpane.setVgap(20);

			Label DateL = new Label("Date:");
			Label LocationL = new Label("Latitude, Latitude:");
			Label AzOffsetL = new Label("Azimuthal Offset:");
			Label MagnitudeL = new Label("Magnitude Limit:");
			Label ScaleL = new Label("Scale Stars:");

			String st[] = { "Show Below Horizon", "Show Star Labels", "Show Constellations",
					"Show Constellation Labels", "Show Planets", "Show Planet Labels", "Show Messier",
					"Show Messier abels", "Show Caldwell", "Show Caldwell Labels", "Past", "future", "Live",
					"Zoom to 100%" };

			for (int i = 0; i < 14; i++) {
				c[i] = new CheckBox(st[i]);
				gridpane.add(c[i], i % 2, i / 2 + 5);
			}

			reset();
			//Button to apply all the options selected and data inputed.
			Button ok = new Button("OK");
			ok.setOnAction(e -> {
				update();
			});

			//Button to close the window (any options selected will now be applied)
			Button cancel = new Button("Cancel");
			cancel.setOnAction(e -> {
				close();
			});

			GridPane.setHalignment(ok, HPos.RIGHT);
			GridPane.setHalignment(cancel, HPos.CENTER);
			//add all labels, textfield and buttons to the grid pane.
			gridpane.add(DateL, 0, 0);
			gridpane.add(Date, 1, 0);
			gridpane.add(LocationL, 0, 1);
			gridpane.add(Location, 1, 1);
			gridpane.add(AzOffsetL, 0, 2);
			gridpane.add(AzOffset, 1, 2);
			gridpane.add(MagnitudeL, 0, 3);
			gridpane.add(Magnitude, 1, 3);
			gridpane.add(ScaleL, 0, 4);
			gridpane.add(Scale, 1, 4);
			gridpane.add(ok, 1, 12);
			gridpane.add(cancel, 1, 12);
			root.getChildren().add(gridpane);
		}
		
		/**
		 * Reset the values of the Date, Location, AzOffSet, Magnitude and Scale.
		 */
		public void reset() {
			Date.setText(sky.getClock().toString());
			Location.setText(String.valueOf(sky.getLatitude()) + ", " + String.valueOf(sky.getLongitude()));
			AzOffset.setText(String.valueOf(sky.getAzOffset()));
			Magnitude.setText(String.valueOf(magnitude));
			Scale.setText(String.valueOf(factor));
			for (int i = 0; i < 13; i++)
				c[i].setSelected(opt[i]);
			c[13].setSelected(zoom == 1);
		}

		/**
		 * Method to apply all the options selected by the user.
		 */
		public void update() {
			try {
				DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss zzz yyyy");
				sky.setClock(df.parse(Date.getText()));
				String[] l = Location.getText().split(",");
				sky.setLatitude(Double.parseDouble(l[0]));
				sky.setLongitude(Double.parseDouble(l[1]));
				sky.setAzOffset(Double.parseDouble(AzOffset.getText()));
				magnitude = Double.parseDouble(Magnitude.getText());
				if (magnitude > 23 || magnitude < -26)
					magnitude = magnitude > 23 ? 23 : -26;
				factor = Double.parseDouble(Scale.getText());
				if (factor > 10 || factor < 0)
					factor = factor > 10 ? 10 : 0;
				for (int i = 0; i < 13; i++)
					opt[i] = c[i].isSelected();
				if (c[13].isSelected()) {
					zoom = 1;
					x = 0;
					y = 0;
				}
				canvas.draw();
				close();
			} catch (ParseException e) {
				Alert wrong = new Alert(AlertType.WARNING,
						"Pleace enter the date in this format:\nEEE MMM dd kk:mm:ss zzz yyyy", ButtonType.OK);
				wrong.showAndWait();
			} catch (NumberFormatException e) {
				Alert wrong = new Alert(AlertType.WARNING,
						"Pleace enter the location in this format:\nLatitude, Latitude", ButtonType.OK);
				wrong.showAndWait();
			}
		}
	}
}