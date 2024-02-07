import org.ejml.simple.SimpleMatrix;
import javax.swing.*;
import java.awt.*;

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
            JSlider headingSlider = new JSlider(0, 180, 90);
            pane.add(headingSlider, BorderLayout.SOUTH);

            // slider to control vertical rotation
            JSlider pitchSlider = new JSlider(SwingConstants.VERTICAL, 0, 180, 90);
            pane.add(pitchSlider, BorderLayout.EAST);

            // slider to control Z-rotation
            JSlider zSlider = new JSlider(SwingConstants.VERTICAL, 0, 180, 90);
            pane.add(zSlider, BorderLayout.WEST);

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
            zSlider.addChangeListener(e -> sliderListener.zListener(zSlider.getValue()));
            pane.add(renderPanel, BorderLayout.CENTER);
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        }


        public void drawOrtographicProection(){

// Potentially good drawing, but doesn't work
//            BufferedImage image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
//            for (int i = 0; i < frame.getWidth(); i++){
//                for (int j = 0; j < frame.getHeight(); j++){
//                    image.setRGB(i, j, 0xff0000);
//                }
//            }
//            g2.drawImage(image, 0, 0, null);

            SimpleMatrix viewport = new SimpleMatrix(new double[][] {
                    new double[]{frame.getWidth() / 2d, 0, 0, frame.getWidth() / 2d},
                    new double[]{0, -frame.getHeight() / 2d, 0, frame.getHeight() / 2d},
                    new double[]{0, 0, 1, 0},
                    new double[]{0, 0, 0, 1},
            });

            double aspect = (double)frame.getWidth()/frame.getHeight();
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
            g2.setColor(Color.WHITE);
            camera.cameraNormalize();
            for (Polygon t : model.polygons) {
                Vertex v1 = new Vertex(t.v1.x/100, t.v1.y/100, t.v1.z/100);
                Vertex v2 = new Vertex(t.v2.x/100, t.v2.y/100, t.v2.z/100);
                Vertex v3 = new Vertex(t.v3.x/100, t.v3.y/100, t.v3.z/100);
                v1 = model.multuplyColumn(model.modelMatrix, v1);
                v2 = model.multuplyColumn(model.modelMatrix, v2);
                v3 = model.multuplyColumn(model.modelMatrix, v3);
                //
                v1 = model.multuplyColumn(camera.view, v1);
                v2 = model.multuplyColumn(camera.view, v2);
                v3 = model.multuplyColumn(camera.view, v3);
                //
                v1 = model.multuplyColumn(projection, v1);
                v2 = model.multuplyColumn(projection, v2);
                v3 = model.multuplyColumn(projection, v3);
                v1 = model.multuplyColumn(viewport, v1);
                v2 = model.multuplyColumn(viewport, v2);
                v3 = model.multuplyColumn(viewport, v3);
                drawPolygon(v1, v2, v3);
//                Path2D path = new Path2D.Double(); //default drawing
//                path.moveTo(v1.x, v1.y);
//                path.lineTo(v2.x, v2.y);
//                path.lineTo(v3.x, v3.y);
//                path.closePath();
//                g2.draw(path);

            }
//            g2.drawString(("eye: " + camera.eye.x + "; " +  camera.eye.y + "; " + camera.eye.z), 10, 10);
//            SphericalCoordinates sc = camera.convertCoordinatesToSpherical(camera.eye.x, camera.eye.y, camera.eye.z);
//            g2.drawString(("theta: " + sc.theta + "; phi " +  sc.phi), 10, 25);
//            g2.drawString(("up: " + camera.up.x + "; " +  camera.up.y + "; " + camera.up.z), 10, 40);
        }

        public void drawPolygon(Vertex v1, Vertex v2, Vertex v3){
            drawDDALine(v1.x, v1.y, v2.x, v2.y);
            drawDDALine(v2.x, v2.y, v3.x, v3.y);
            drawDDALine(v3.x, v3.y, v1.x, v1.y);
        }

        public void drawDDALine(float x1, float y1, float x2, float y2){
            float dx = x2 - x1;
            float dy = y2 - y1;
            int step;
            step = Math.round(Math.max(Math.abs(dx), Math.abs(dy)));
            float xInc = dx / step;
            float yInc = dy / step;
            float x = x1;
            float y = y1;
            for (int i = 0; i <= step; i++){
                g2.fillRect(Math.round(x), Math.round(y), 1, 1);
                x += xInc;
                y += yInc;
            }
        }


}
