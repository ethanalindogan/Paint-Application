package ca.utoronto.utm.paint;
import javafx.scene.input.MouseEvent;

class PolylineManipulatorStrategy extends ShapeManipulatorStrategy {
    PolylineManipulatorStrategy(PaintModel paintModel) {
        super(paintModel);
    }

    private PolylineCommand polylineCommand;

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPrimaryButtonDown()) {
            Point p = new Point((int) e.getX(), (int) e.getY());
            if (polylineCommand == null) {
                polylineCommand = new PolylineCommand();
                this.addCommand(polylineCommand);
            }
            polylineCommand.add(p);
        } else if (e.isSecondaryButtonDown() && polylineCommand != null) {
            polylineCommand = null;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (polylineCommand != null) {
            Point p = new Point((int) e.getX(), (int) e.getY());
            polylineCommand.setCurrentEnd(p);
            polylineCommand.notifyObservers();
        }
    }
}