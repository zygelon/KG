package com.gr.five;

import no.geosoft.cc.graphics.*;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class Main extends JFrame implements GInteraction {
    private GScene mainScene;

    public Main() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GWindow window = new GWindow(new Color(200, 210, 200));
        getContentPane().add(window.getCanvas());

        mainScene = new GScene(window);

        GStyle style = new GStyle();
        style.setForegroundColor(new Color(0, 0, 0));
        style.setBackgroundColor(new Color(255, 255, 255));
        style.setFont(new Font("Dialog", Font.BOLD, 14));
        mainScene.setStyle(style);

        /*
        Double[] points = new double[]{
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
        };

        QuickHull hull = new QuickHull(points, mainScene);
         */

        int points = 100;
        LinkedList<Double> pointsList = new LinkedList<>();
        for (int i = 0; i < points * 2; i++) {
            pointsList.add(Math.random() * 40 + Math.random() * 40 + 10);
        }
        QuickHull hull = new QuickHull(pointsList.toArray(new Double[0]), mainScene);

        pack();
        setSize(new Dimension(600, 600));
        setVisible(true);

        window.startInteraction(this);
    }


    public void event(GScene scene, int event, int x, int y) {
        /*
        switch (event) {
            case GWindow.BUTTON1_DOWN:
                //searcher.moveFrom(x, y);
                //searcher.draw();
                mainScene.refresh();
                break;
            case GWindow.MOTION:
                //searcher.moveTo(x, y);
                //searcher.draw();
                mainScene.refresh();
        }*/
    }

    public static void main(String[] args) {
        new Main();
    }
}
