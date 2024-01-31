import org.ejml.simple.SimpleMatrix;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Path2D;

public class Viewer {

        JFrame frame;
        Graphics2D g2;
        Model model;
        JPanel renderPanel;
        Camera camera;

        public void addModel(Model model){
            this.model = model;
        }
        public void addCamera(Camera camera){
        this.camera = camera;
    }

        public void draw() {

            frame = new JFrame();
            Container pane = frame.getContentPane();
            pane.setLayout(new BorderLayout());

            // slider to control horizontal rotation
            //JSlider headingSlider = new JSlider(0, 360, 180);
            JSlider headingSlider = new JSlider(0, 180, 90);
            pane.add(headingSlider, BorderLayout.SOUTH);

            // slider to control vertical rotation
            //JSlider pitchSlider = new JSlider(SwingConstants.VERTICAL, 0, 360, 0);
            JSlider pitchSlider = new JSlider(SwingConstants.VERTICAL, 0, 180, 90);
            pane.add(pitchSlider, BorderLayout.EAST);

            // panel to display render results
            renderPanel = new JPanel() {
                public void paintComponent(Graphics g) {
                    g2 = (Graphics2D) g;
                    g2.setColor(Color.BLACK);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    // rendering
                    drawOrtographicProection();
                }
            };
            //здесь я верчу кокату на чем хочу
            MySliderListener sliderListener = new MySliderListener(model, renderPanel, this);
            pitchSlider.addChangeListener(e -> sliderListener.pitchListener(pitchSlider.getValue()));
            headingSlider.addChangeListener(e -> sliderListener.headingListener(headingSlider.getValue()));
            pane.add(renderPanel, BorderLayout.CENTER);
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        }


        public void drawOrtographicProection(){
            SimpleMatrix viewport = new SimpleMatrix(new double[][] {
                    new double[]{frame.getWidth() / 2, 0, 0, frame.getWidth() / 2},
                    new double[]{0, -frame.getHeight() / 2, 0, frame.getHeight() / 2},
                    new double[]{0, 0, 1, 0},
                    new double[]{0, 0, 0, 1},
            });

            double aspect = frame.getWidth()/frame.getHeight();
            double FOV = Math.toRadians(60);
            double depth = model.getModelDepth();
            double znear = camera.eye.z;
            double zfar = depth + znear;

            SimpleMatrix projection = new SimpleMatrix(new double[][] {
                    new double[]{1 / (aspect * Math.tan(FOV / 2)), 0, 0, 0},
                    new double[]{0, 1 / (Math.tan(FOV / 2)), 0, 0},
                    new double[]{0, 0, zfar / (znear - zfar), zfar * znear / (znear - zfar)},
                    new double[]{0, 0, -1, 0},
            });
            //g2.translate(frame.getWidth() / 2, frame.getHeight() / 2);
            g2.setColor(Color.WHITE);
            camera.cameraNormalize();
            for (Polygon t : model.polygons) {
                Vertex v1 = new Vertex(t.v1.x/100, t.v1.y/100, t.v1.z/100);
                Vertex v2 = new Vertex(t.v2.x/100, t.v2.y/100, t.v2.z/100);
                Vertex v3 = new Vertex(t.v3.x/100, t.v3.y/100, t.v3.z/100);
                v1 = model.multuplyColumn(model.modelMatrix, v1);
                v2 = model.multuplyColumn(model.modelMatrix, v2);
                v3 = model.multuplyColumn(model.modelMatrix, v3);
                //v1 = model.multuplyColumn(camera.view, v1);
                //v2 = model.multuplyColumn(camera.view, v2);
                //v3 = model.multuplyColumn(camera.view, v3);
                v1 = model.multuplyColumn(projection, v1);
                v2 = model.multuplyColumn(projection, v2);
                v3 = model.multuplyColumn(projection, v3);
                v1 = model.multuplyColumn(viewport, v1);
                v2 = model.multuplyColumn(viewport, v2);
                v3 = model.multuplyColumn(viewport, v3);
                Path2D path = new Path2D.Double();
                path.moveTo(v1.x, v1.y);
                path.lineTo(v2.x, v2.y);
                path.lineTo(v3.x, v3.y);
                path.closePath();
                g2.draw(path);

            }
        }


}
