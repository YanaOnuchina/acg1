import org.ejml.simple.SimpleMatrix;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class Viewer {

        JFrame frame;
        Graphics2D g2;
        Model model;
        JPanel renderPanel;
        Camera camera;
        float[][] zBuffer;

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
                    drawProection();
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
            zBuffer = new float[frame.getHeight()][frame.getWidth()];
            freeZbuffer();
            frame.requestFocus();
        }


        public void drawProection(){

            SimpleMatrix viewport = new SimpleMatrix(new double[][] {
                    new double[]{frame.getWidth() / 2d, 0, 0, frame.getWidth() / 2d},
                    new double[]{0, -frame.getHeight() / 2d, 0, frame.getHeight() / 2d},
                    new double[]{0, 0, 1, 0},
                    new double[]{0, 0, 0, 1},
            });

            double aspect = (double)frame.getWidth()/frame.getHeight();
            double FOV = Math.toRadians(90);
            double depth = model.getModelDepth();
            double znear = camera.eye.z;
            double zfar = depth + znear;

            SimpleMatrix projection = new SimpleMatrix(new double[][] {
                    new double[]{1 / (aspect * Math.tan(FOV / 2)), 0, 0, 0},
                    new double[]{0, 1 / (Math.tan(FOV / 2)), 0, 0},
                    new double[]{0, 0, zfar / (znear - zfar), zfar * znear / (znear - zfar)},
                    new double[]{0, 0, -1, 0},
            });

            freeZbuffer();
            //g2.setColor(Color.WHITE);
            Random random = new Random();
            camera.cameraNormalize();
            for (Polygon t : model.polygons) {
                g2.setColor(new Color(random.nextFloat(), random.nextFloat(), random.nextFloat()));
                Polygon tCopy = new Polygon(new Vertex(t.v1.x, t.v1.y, t.v1.z),
                        new Vertex(t.v2.x, t.v2.y, t.v2.z),
                        new Vertex(t.v3.x, t.v3.y, t.v3.z));

                tCopy.v1 = model.multuplyColumn(model.modelMatrix, tCopy.v1);
                tCopy.v2 = model.multuplyColumn(model.modelMatrix, tCopy.v2);
                tCopy.v3 = model.multuplyColumn(model.modelMatrix, tCopy.v3);
                //
                tCopy.v1 = model.multuplyColumn(camera.view, tCopy.v1);
                tCopy.v2 = model.multuplyColumn(camera.view, tCopy.v2);
                tCopy.v3 = model.multuplyColumn(camera.view, tCopy.v3);
                //
                tCopy.v1 = model.multuplyColumn(projection, tCopy.v1);
                tCopy.v2 = model.multuplyColumn(projection, tCopy.v2);
                tCopy.v3 = model.multuplyColumn(projection, tCopy.v3);

                tCopy.v1.deformation();
                tCopy.v2.deformation();
                tCopy.v3.deformation();

                tCopy.v1 = model.multuplyColumn(viewport, tCopy.v1);
                tCopy.v2 = model.multuplyColumn(viewport, tCopy.v2);
                tCopy.v3 = model.multuplyColumn(viewport, tCopy.v3);
                tCopy.sort();
                fillPolygon(tCopy);
//                drawPolygon(tCopy.v1, tCopy.v2, tCopy.v3);
//                Path2D path = new Path2D.Double(); //default drawing
//                path.moveTo(v1.x, v1.y);
//                path.lineTo(v2.x, v2.y);
//                path.lineTo(v3.x, v3.y);
//                path.closePath();
//                g2.draw(path);

            }
        }

        public void drawPolygon(Vertex v1, Vertex v2, Vertex v3){
            drawDDALine(v1.x, v1.y, v2.x, v2.y);
            drawDDALine(v2.x, v2.y, v3.x, v3.y);
            drawDDALine(v3.x, v3.y, v1.x, v1.y);
        }

        public void fillPolygon(Polygon t){
            float crossX1;
            float crossX2;
            float crossZ1;
            float crossZ2;
            float dx1 = t.v2.x - t.v1.x;
            float dy1 = t.v2.y - t.v1.y;
            float dz1 = t.v2.z - t.v1.z;
            float dx2 = t.v3.x - t.v1.x;
            float dy2 = t.v3.y - t.v1.y;
            float dz2 = t.v3.z - t.v1.z;
            float topY = t.v1.y;

            while(topY < t.v2.y){
                crossX1 = t.v1.x + dx1 * (topY - t.v1.y) / dy1;
                crossX2 = t.v1.x + dx2 * (topY - t.v1.y) / dy2;
                crossZ1 = t.v1.z + dz1 * (topY - t.v1.y) / dy1;
                crossZ2 = t.v1.z + dz2 * (topY - t.v1.y) / dy2;
                drawDDALine(crossX1, topY, crossZ1, crossX2, topY, crossZ2);
                topY++;
            }

            dx1 = t.v3.x - t.v2.x;
            dy1 = t.v3.y - t.v2.y;
            dz1 = t.v3.z - t.v2.z;

            while(topY < t.v3.y){
                crossX1 = t.v2.x + dx1 * (topY - t.v2.y) / dy1;
                crossX2 = t.v1.x + dx2 * (topY - t.v1.y) / dy2;
                crossZ1 = t.v2.z + dz1 * (topY - t.v2.y) / dy1;
                crossZ2 = t.v1.z + dz2 * (topY - t.v1.y) / dy2;
                drawDDALine(crossX1, topY, crossZ1, crossX2, topY, crossZ2);
                topY++;
            }
        }

        public void drawDDALine(float x1, float y1, float z1, float x2, float y2, float z2){
            float dx = x2 - x1;
            float dy = y2 - y1;
            float dz = z2 - z1;
            int step;
            step = Math.round(Math.max(Math.abs(dx), Math.abs(dy)));
            float xInc = dx / step;
            float yInc = dy / step;
            float zInc = dz / step;
            float x = x1;
            float y = y1;
            float z = z1;
            for (int i = 0; i <= step; i++){
                //
                int indexX = (Math.round(x) < frame.getWidth() ? Math.round(x) : frame.getWidth() - 1);
                int indexY = (Math.round(y) < frame.getHeight() ? Math.round(y) : frame.getHeight() - 1);
                try {
                    if ((zBuffer[indexY][indexX] < z && camera.eye.z > 0) || (zBuffer[indexY][indexX] > z && camera.eye.z < 0)) {
                        zBuffer[indexY][indexX] = z;
                        g2.fillRect(indexX, indexY, 1, 1);
                    }
                }
                catch (IndexOutOfBoundsException ignored){

                }
                //
                x += xInc;
                y += yInc;
                z += zInc;
            }
        }

        public void drawDDALine(float x1, float y1, float x2, float y2) {
            float dx = x2 - x1;
            float dy = y2 - y1;
            int step;
            step = Math.round(Math.max(Math.abs(dx), Math.abs(dy)));
            float xInc = dx / step;
            float yInc = dy / step;
            float x = x1;
            float y = y1;
            for (int i = 0; i <= step; i++) {
                g2.fillRect(Math.round(x), Math.round(y), 1, 1);
                x += xInc;
                y += yInc;
            }
        }

        public void freeZbuffer(){
            for (float[] row: zBuffer) {
                Arrays.fill(row, Float.MIN_VALUE);
            }
        }


}
