import no.geosoft.cc.graphics.*;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame implements GInteraction {
    private GScene mainScene;
    private boolean mouseDown = false;
    private Graph graph;
    private MousePoint point;

    public Main() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GWindow window = new GWindow (new Color (200, 210, 200));
        getContentPane().add (window.getCanvas());

        mainScene = new GScene (window);

        GStyle style = new GStyle();
        style.setForegroundColor (new Color (0, 0, 0));
        style.setBackgroundColor (new Color (255, 255, 255));
        style.setFont (new Font ("Dialog", Font.BOLD, 14));
        mainScene.setStyle (style);

        graph = new Graph(new double[]{
                25, 15,
                75, 15,
                50, 35,
                37, 45,
                63, 45,
                25, 55,
                75, 55,
                37, 65,
                63, 65,
                50, 55,
                50, 85
        }, new int[] {
                0, 2,
                0, 3,
                0, 5,
                1, 2,
                1, 4,
                1, 6,
                2, 3,
                2, 4,
                3, 5,
                3, 7,
                4, 6,
                4, 8,
                5, 7,
                6, 8,
                3, 9,
                4, 9,
                7, 9,
                8, 9,
                7, 10,
                8, 10
        }, mainScene);

//        graph = new Graph(new double[]{
//                15, 15,
//                50, 15,
//                45, 55,
//                15, 60
//        }, new int[] {
//                0, 1,
//                0, 2,
//                1, 2,
//                1, 3,
//                2, 3,
//                3, 0
//        }, mainScene);


        point = new MousePoint(50, 50, mainScene);
        point.setText(String.valueOf(graph.face(point.getX(), point.getY())));

        pack();
        setSize(new Dimension(600, 600));
        setVisible(true);

        window.startInteraction(this);
    }

    public void event(GScene scene, int event, int x, int y) {
        switch (event) {
            case GWindow.BUTTON1_DOWN:
                mouseDown = true;
                point.move(x, y);
                point.setText(String.valueOf(graph.face(point.getX(), point.getY())));
                point.draw();
                mainScene.refresh();
                break;
            case GWindow.BUTTON1_UP:
                mouseDown = false;
                mainScene.refresh();
                break;
            case GWindow.BUTTON1_DRAG:
                if (mouseDown) point.move(x, y);
                point.setText(String.valueOf(graph.face(point.getX(), point.getY())));
                point.draw();
                mainScene.refresh();
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}
