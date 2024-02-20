import org.ejml.simple.SimpleMatrix;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
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
            frame.requestFocus();
        }


        public void drawProection(){

            zBuffer = new float[frame.getHeight()][frame.getWidth()];
            freeZbuffer();
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
            g2.setColor(Color.WHITE);
            Random random = new Random();
            camera.cameraNormalize();
            for (Polygon t : model.polygons) {
                Polygon tCopy = new Polygon(new Vertex(t.v1.x, t.v1.y, t.v1.z),
                        new Vertex(t.v2.x, t.v2.y, t.v2.z),
                        new Vertex(t.v3.x, t.v3.y, t.v3.z));

                tCopy.v1 = model.multuplyColumn(model.modelMatrix, tCopy.v1);
                tCopy.v2 = model.multuplyColumn(model.modelMatrix, tCopy.v2);
                tCopy.v3 = model.multuplyColumn(model.modelMatrix, tCopy.v3);

                Vertex rebro1 = Vertex.vertexDifference(tCopy.v2, tCopy.v1);
                Vertex rebro2 = Vertex.vertexDifference(tCopy.v3, tCopy.v1);
                Vertex normal = Vertex.vertexMultiplication(rebro1, rebro2);
                Vertex eyeView = Vertex.vertexDifference(camera.eye, tCopy.v1);

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
//                Vertex rebro1 = Vertex.vertexDifference(tCopy.v2, tCopy.v1);
//                Vertex rebro2 = Vertex.vertexDifference(tCopy.v3, tCopy.v1);
//                Vertex normal = Vertex.vertexMultiplication(rebro1, rebro2);
                normal = Vertex.normalize(normal);
                eyeView = Vertex.normalize(eyeView);
                tCopy.sort();
                float decision = eyeView.x*normal.x + eyeView.y*normal.y + eyeView.z*normal.z;
                Vertex light = new Vertex(-1, -1, -1);
                light = Vertex.normalize(light);
                float angle = normal.x * -light.x + normal.y * -light.y + normal.z * -light.z;
                angle = Math.max(0, angle);
                angle = (float) Math.pow(angle, 1/2.2f) * 255;
                // double shade = (255 * angle);
                int lightning = (int) angle;
                g2.setColor(new Color(lightning, lightning, lightning));
                if (decision > 0) {
                    fillPolygon(tCopy);
                }
                //drawPolygon(tCopy.v1, tCopy.v2, tCopy.v3);
//                Path2D path = new Path2D.Double(); //default drawing
//                path.moveTo(v1.x, v1.y);
//                path.lineTo(v2.x, v2.y);
//                path.lineTo(v3.x, v3.y);
//                path.closePath();
//                g2.draw(path);

            }

        }

          /*public void drawPolygon(Vertex v1, Vertex v2, Vertex v3){
            drawDDALine(v1.x, v1.y, v2.x, v2.y);
            drawDDALine(v2.x, v2.y, v3.x, v3.y);
            drawDDALine(v3.x, v3.y, v1.x, v1.y);
       }*/

        public void fillPolygon(Polygon t){
//            float crossX1;
//            float crossX2;
//            float crossZ1;
//            float crossZ2;
            float dx1 = t.v2.x - t.v1.x;
            float dy1 = t.v2.y - t.v1.y;
            float dz1 = t.v2.z - t.v1.z;
            float dx2 = t.v3.x - t.v1.x;
            float dy2 = t.v3.y - t.v1.y;
            float dz2 = t.v3.z - t.v1.z;
            float dx3 = t.v3.x - t.v2.x;
            float dy3 = t.v3.y - t.v2.y;
            float dz3 = t.v3.z - t.v2.z;
            float kx1 = dx1/dy1;
            float kx2 = dx2/dy2;
            float kx3 = dx3/dy3;
            float kz1 = dz1/dy1;
            float kz2 = dz2/dy2;
            float kz3 = dz3/dy3;

            int topY = (int) Math.max(Math.ceil(t.v1.y), 0);
            int bottomY = (int) Math.min(Math.ceil(t.v3.y), frame.getHeight()-1);

            for (int y = topY; y < bottomY; y++){
                float crossX1 = y < t.v2.y ? t.v1.x + kx1 * (y - t.v1.y) : t.v2.x + kx3 * (y - t.v2.y);
                float crossX2 = t.v1.x + kx2 * (y - t.v1.y);
                float crossZ1 = y < t.v2.y ? t.v1.z + kz1 * (y - t.v1.y) : t.v2.z + kz3 * (y - t.v2.y);
                float crossZ2 = t.v1.z + kz2 * (y - t.v1.y);
                if (crossX1 > crossX2){
                    float temp = crossX1;
                    crossX1 = crossX2;
                    crossX2 = temp;
                    temp = crossZ1;
                    crossZ1 = crossZ2;
                    crossZ2 = temp;
                }
                float kz = (crossZ2 - crossZ1) / (crossX2 - crossX1);
                int leftX = (int) Math.max(Math.ceil(crossX1), 0);
                int rightX = (int) Math.min(Math.ceil(crossX2), frame.getWidth()-1);

                for (int x = leftX; x < rightX; x++){
                    float z = crossZ1 + kz * (x - crossX1);
                    if ((zBuffer[y][x] > z && camera.eye.z > 0) || (camera.eye.z < 0)) {
                        zBuffer[y][x] = z;
                        g2.fillRect(x, y, 1, 1);
                    }
                }
                //drawDDALine(crossX1, y, crossZ1, crossX2, y, crossZ2);
            }

        }

        public void drawDDALine(float x1, float y1, float z1, float x2, float y2, float z2){
            float dx = x2 - x1;
            //float dy = y2 - y1;
            float dz = z2 - z1;
            int step;
            step = Math.round(Math.max(Math.abs(dx), Math.abs(dz)));
            float xInc = dx / step;
           // float yInc = dy / step;
            float zInc = dz / step;
            float x = x1;
            float y = y1;
            float z = z1;
            for (int i = 0; i <= step; i++){
                //
                int indexX = (int)Math.ceil(x);
                int indexY = (int)Math.ceil(y);
                try {
                    if ((zBuffer[indexY][indexX] > z && camera.eye.z > 0) || (camera.eye.z < 0)) {
                        zBuffer[indexY][indexX] = z;
                       g2.fillRect(indexX, indexY, 1, 1);
                    }
                }
                catch (IndexOutOfBoundsException ignored){
                }
                //
                x += xInc;
                //y += yInc;
                z += zInc;
            }
        }

        public void drawDDALineA(float x1, float y1, float x2, float y2) {
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
                Arrays.fill(row, Float.MAX_VALUE);
            }
        }

        public void drawZbuffer(){
            for (int i = 0; i < zBuffer.length; i++){
                for (int j = 0; j < zBuffer[0].length; j++){
                    if (zBuffer[i][j] == Float.NEGATIVE_INFINITY){
                        g2.setColor(Color.BLACK);
                    }
                    else {
                        g2.setColor(new Color(255, 0, 0));
                    }
                    g2.fillRect(j, i, 1, 1);
                }
            }
        }


}
