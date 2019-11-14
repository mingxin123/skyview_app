package deprecated;

import com.sun.scenario.effect.DropShadow;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;
/**
 * @deprecated
 * @author
 *
 */
public class MoonDrawingPrototype extends Application{
	private Image sourceImage; 

	public MoonDrawingPrototype() {
		// TODO Auto-generated constructor stub
	}

	
		  

		  @Override
		  public void start(Stage primaryStage) {
			sourceImage = new Image("lunar-phase-drawing.png");
		    primaryStage.setTitle("");
		    Group root = new Group();
		    Scene scene = new Scene(root, 300, 250, Color.WHITE);
		    Canvas canvas = new Canvas(350, 350);
		    GraphicsContext gc = canvas.getGraphicsContext2D();
		    gc.setFill(Color.BLACK);
		    gc.fillRect(0, 0, 300, 250);
		    
		    Group g = new Group();

		    DropShadow ds = new DropShadow();
		    ds.setOffsetY(3);
		    //ds.setColor(Color.BLACK);

		    Ellipse ellipse = new Ellipse();
		    ellipse.setCenterX(50.0f);
		    ellipse.setCenterY(50.0f);
		    ellipse.setRadiusX(10.0f);
		    ellipse.setRadiusY(20.0f);
		    //ellipse.setEffect(ds);
		    
		    Circle circ = new Circle();
		    circ.setCenterX(50.0f);
		    circ.setRadius(30.0f);
		    
		    g.getChildren().add(circ);
		    g.getChildren().add(ellipse);

		    //root.getChildren().add(g);
		    root.getChildren().add(canvas);
		    
		    drawImage(gc, 0, 0.0, 0.0, 100, 100);
		    
		    
		    primaryStage.setScene(scene);
		    primaryStage.show();
		  }
		  
		  /**
		   * 
		   * Input the graphicsContext, the moon phase index, the x and y position of the drawing from
		   * top left, and the size of x and y, and will draw at target.
		   * 
		   * @param gc
		   * @param imageInd moon phase index 0 - 7
		   * @param destinationXPos top left x pos of where we want to start drawing
		   * @param destinationYPos top left y pos of where we want to start drawing
		   * @param xWidth The size of x
		   * @param yLength The size of y
		   */
		  public void drawImage(GraphicsContext gc, int imageInd, double destinationXPos, double destinationYPos,  
				  double xWidth, double yLength) {

			  switch(imageInd) {
			  	case 1:
			  		gc.drawImage(sourceImage, 0, 400, 270, 300, destinationXPos, destinationXPos, xWidth, yLength);
			  		break;
			  	case 2:
			  		gc.drawImage(sourceImage, 300, 400, 270, 300, destinationXPos, destinationXPos, xWidth, yLength);
			  		break;
			  	case 3:
			  		gc.drawImage(sourceImage, 530, 400, 270, 300, destinationXPos, destinationXPos, xWidth, yLength);
			  		break;
			  	case 4:
			  		gc.drawImage(sourceImage, 830, 400, 300, 300, destinationXPos, destinationXPos, xWidth, yLength);
			  		break;
			  	case 5:
			  		gc.drawImage(sourceImage, 1130, 400, 270, 300, destinationXPos, destinationXPos, xWidth, yLength);
			  		break;
			  	case 6:
			  		gc.drawImage(sourceImage, 1410, 400, 250, 300, destinationXPos, destinationXPos, xWidth, yLength);
			  		break;
			  	case 7:
			  		gc.drawImage(sourceImage, 1650, 400, 270, 300, destinationXPos, destinationXPos, xWidth, yLength);
			  		break;
			  	default: 
			  		Paint tempPaint = gc.getStroke();
			  		gc.setStroke(Color.GRAY);
			  		gc.strokeOval(destinationXPos, destinationYPos, xWidth, yLength);
			  		gc.setStroke(tempPaint);
			  		break;
			  }
			    
			}
}


