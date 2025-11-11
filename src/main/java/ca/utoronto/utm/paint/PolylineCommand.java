package ca.utoronto.utm.paint;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;

public class PolylineCommand extends PaintCommand {
    private ArrayList<Point> points=new ArrayList<Point>();
    private Point currentEnd = null;

    public void add(Point p){
        this.points.add(p);
        this.setChanged();
        this.notifyObservers();
    }
    public ArrayList<Point> getPoints(){ return this.points; }

    public void setCurrentEnd(Point p) {
        this.currentEnd = p;
        this.setChanged();
        this.notifyObservers();
    }

    @Override
    public String getPaintSaveFileString() {
        StringBuilder s = new StringBuilder();
        s.append("Polyline\n");
        s.append("\tcolor:").append(Math.round(getColor().getRed()*255)).append(",").append
                (Math.round(getColor().getGreen()*255)).append(",").append(Math.round(getColor().getBlue()*255)).append("\n");
        s.append("\tfilled:").append(isFill()).append("\n");
        s.append("\tpoints\n");
        for (Point p : points) {
            s.append("\t\tpoint:(").append(p.x).append(",").append(p.y).append(")\n");
        }
        s.append("\tend points\n");
        s.append("EndPolyline");
        return s.toString();
    }

    @Override
    public void execute(GraphicsContext g) {
        g.setStroke(this.getColor());
        if (points.size() > 1) {
            for (int i = 0; i < points.size() - 1; i++) {
                Point p1 = getPoints().get(i);
                Point p2 = getPoints().get(i + 1);
                g.strokeLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
        if (currentEnd != null && !points.isEmpty()) {
            Point lastPoint = getPoints().get(points.size() - 1);
            g.strokeLine(lastPoint.x, lastPoint.y, currentEnd.x, currentEnd.y);
        }
    }
}
