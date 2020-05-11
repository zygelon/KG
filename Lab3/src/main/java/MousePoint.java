import no.geosoft.cc.geometry.Geometry;
import no.geosoft.cc.graphics.*;

import java.awt.*;

public class MousePoint extends GObject {

    private double x, y;
    private GSegment circle;

    public MousePoint(double x, double y, GScene scene) {

        this.x = x;
        this.y = y;

        circle = new GSegment();
        addSegment(circle);

        setStyle(new GStyle());

        scene.add(this);
    }

    double getX() {
        return x;
    }

    double getY() {
        return y;
    }

    public void draw() {
        circle.setGeometryXy(Geometry.createEllipse(x, y,
                1250.0 / getScene().getViewport().getWidth(),
                1000.0 / getScene().getViewport().getHeight()
        ));
    }

    public void move(double x, double y) {
        this.x = x / getScene().getViewport().getWidth() * 100.0;
        this.y = y / getScene().getViewport().getHeight() * 100.0;
    }

    private void setColor(Color color) {
        //getStyle().setForegroundColor(color);
        getStyle().setBackgroundColor(color);
    }

    public void setText(String text) {
        circle.setText(new GText(text, GPosition.MIDDLE));
    }

    public void setState(int state) {
        if (state == 2) {
            setColor(Color.GREEN);
        } else if (state == 1) {
                setColor(Color.YELLOW);
        } else {
            setColor(Color.RED);
        }
    }
}
