package ca.utoronto.utm.paint;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;

public class SquiggleCommand extends PaintCommand {
	private ArrayList<Point> points=new ArrayList<Point>();
	
	public void add(Point p){ 
		this.points.add(p); 
		this.setChanged();
		this.notifyObservers();
	}
	public ArrayList<Point> getPoints(){ return this.points; }


	@Override
	public String getPaintSaveFileString() {
		StringBuilder s = new StringBuilder();
		s.append("Squiggle\n");
		s.append("\tcolor:").append(Math.round(getColor().getRed()*255)).append(",").append
				(Math.round(getColor().getGreen()*255)).append(",").append(Math.round(getColor().getBlue()*255)).append("\n");
		s.append("\tfilled:").append(isFill()).append("\n");
		s.append("\tpoints\n");
		for (Point p : points) {
			s.append("\t\tpoint:(").append(p.x).append(",").append(p.y).append(")\n");
		}
		s.append("\tend points\n");
		s.append("EndSquiggle");
		return s.toString();
	}
	
	@Override
	public void execute(GraphicsContext g) {
		ArrayList<Point> points = this.getPoints();
		g.setStroke(this.getColor());
		for(int i=0;i<points.size()-1;i++){
			Point p1 = points.get(i);
			Point p2 = points.get(i+1);
			g.strokeLine(p1.x, p1.y, p2.x, p2.y);
		}
		
	}
}
