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

        public void addModel(Model model){
            this.model = model;
        }

        public void draw() {

            frame = new JFrame();
            Container pane = frame.getContentPane();
            pane.setLayout(new BorderLayout());

            // slider to control horizontal rotation
            JSlider headingSlider = new JSlider(0, 360, 180);
            pane.add(headingSlider, BorderLayout.SOUTH);

            // slider to control vertical rotation
            JSlider pitchSlider = new JSlider(SwingConstants.VERTICAL, 0, 360, 0);
            pane.add(pitchSlider, BorderLayout.EAST);

            // panel to display render results
            renderPanel = new JPanel() {
                public void paintComponent(Graphics g) {
                    g2 = (Graphics2D) g;
                    g2.setColor(Color.BLACK);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    // rendering magic will happen here
                    drawOrtographicProection();
                }
            };
            //здесь я верчу кокату на чем хочу
            MySliderListener sliderListener = new MySliderListener(model, renderPanel, this);
            pitchSlider.addChangeListener(e -> sliderListener.pitchListener(pitchSlider.getValue()));
            headingSlider.addChangeListener(e -> sliderListener.headingListener(headingSlider.getValue()));
            pane.add(renderPanel, BorderLayout.CENTER);
            frame.setSize(400, 400);
            frame.setVisible(true);
        }


        public void drawOrtographicProection(){
            g2.translate(frame.getWidth() / 2, frame.getHeight() / 2);
            g2.setColor(Color.WHITE);
            for (Polygon t : model.polygons) {
                Path2D path = new Path2D.Double();
                path.moveTo(t.v1.x, t.v1.y);
                path.lineTo(t.v2.x, t.v2.y);
                path.lineTo(t.v3.x, t.v3.y);
                path.closePath();
                g2.draw(path);
            }
        }


}
