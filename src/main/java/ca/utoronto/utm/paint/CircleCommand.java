package ca.utoronto.utm.paint;
import javafx.scene.canvas.GraphicsContext;

public class CircleCommand extends PaintCommand {
	private Point centre;
	private int radius;
	
	public CircleCommand(Point centre, int radius){
		this.centre = centre;
		this.radius = radius;
	}
	public Point getCentre() { return centre; }
	public void setCentre(Point centre) { 
		this.centre = centre; 
		this.setChanged();
		this.notifyObservers();
	}
	public int getRadius() { return radius; }
	public void setRadius(int radius) { 
		this.radius = radius; 
		this.setChanged();
		this.notifyObservers();
	}

	@Override
	public String getPaintSaveFileString() {
		StringBuilder s = new StringBuilder();
		s.append("Circle\n");
		s.append("\tcolor:").append(Math.round(getColor().getRed()*255)).append(",").append
				(Math.round(getColor().getGreen()*255)).append(",").append(Math.round(getColor().getBlue()*255)).append("\n");
		s.append("\tfilled:").append(isFill()).append("\n");
		s.append("\tcenter:(").append(getCentre().x).append(",").append(getCentre().y).append(")\n");
		s.append("\tradius:").append(getRadius()).append("\n");
		s.append("EndCircle");
		return s.toString();
	}
	public void execute(GraphicsContext g){
		int x = this.getCentre().x;
		int y = this.getCentre().y;
		int radius = this.getRadius();
		if(this.isFill()){
			g.setFill(this.getColor());
			g.fillOval(x-radius, y-radius, 2*radius, 2*radius);
		} else {
			g.setStroke(this.getColor());
			g.strokeOval(x-radius, y-radius, 2*radius, 2*radius);
		}
	}
}
