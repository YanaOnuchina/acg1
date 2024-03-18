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
        Color modelColor = new Color(0, 128, 0);
        Vertex Light = Vertex.normalize(new Vertex(-1, -1, -1));

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
            double znear = 0.1;
            double zfar = 100;
           // double znear = camera.eye.z;
           // double zfar = depth + znear;

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
                //Polygon tCopy = t.getCopy();
//                Polygon t = new Polygon(new Vertex(0, 0, 0), new Vertex(3, 0, 0), new Vertex(0, 4, 0), //test triangle
//                        new Vertex(0, 0, 1, 0), new Vertex(1, 0, 0, 0), new Vertex(0, 1, 0, 0));
                PolygonInSpaces tCopy = t.getCopyInSpaces();

//                tCopy.v1 = model.multuplyColumn(model.modelMatrix, tCopy.v1);
//                tCopy.v2 = model.multuplyColumn(model.modelMatrix, tCopy.v2);
//                tCopy.v3 = model.multuplyColumn(model.modelMatrix, tCopy.v3);

                tCopy.v1World = model.multuplyColumn2(model.modelMatrix, tCopy.v1);
                tCopy.v2World = model.multuplyColumn2(model.modelMatrix, tCopy.v2);
                tCopy.v3World = model.multuplyColumn2(model.modelMatrix, tCopy.v3);
                tCopy.vn1 = Vertex.normalize(model.multuplyColumn2(model.modelMatrix, tCopy.vn1));
                tCopy.vn2 = Vertex.normalize(model.multuplyColumn2(model.modelMatrix, tCopy.vn2));
                tCopy.vn3 = Vertex.normalize(model.multuplyColumn2(model.modelMatrix, tCopy.vn3));

                //Polygon tWorldCopy = tCopy.getCopy();

//                Vertex rebro1 = Vertex.vertexDifference(tCopy.v2, tCopy.v1);
//                Vertex rebro2 = Vertex.vertexDifference(tCopy.v3, tCopy.v1);
                Vertex edge1 = Vertex.vertexDifference(tCopy.v2World, tCopy.v1World);
                Vertex edge2 = Vertex.vertexDifference(tCopy.v3World, tCopy.v1World);
                Vertex normal = Vertex.normalize(Vertex.vertexMultiplication(edge1, edge2));
//                Vertex eyeView = Vertex.vertexDifference(camera.eye, tCopy.v1);
                Vertex eyeView = Vertex.normalize(Vertex.vertexDifference(camera.eye, tCopy.v1World));

                //
//                tCopy.v1 = model.multuplyColumn(camera.view, tCopy.v1);
//                tCopy.v2 = model.multuplyColumn(camera.view, tCopy.v2);
//                tCopy.v3 = model.multuplyColumn(camera.view, tCopy.v3);
//                //
//                tCopy.v1 = model.multuplyColumn(projection, tCopy.v1);
//                tCopy.v2 = model.multuplyColumn(projection, tCopy.v2);
//                tCopy.v3 = model.multuplyColumn(projection, tCopy.v3);
//
//                tCopy.v1.deformation();
//                tCopy.v2.deformation();
//                tCopy.v3.deformation();
//
//                tCopy.v1 = model.multuplyColumn(viewport, tCopy.v1);
//                tCopy.v2 = model.multuplyColumn(viewport, tCopy.v2);
//                tCopy.v3 = model.multuplyColumn(viewport, tCopy.v3);
                tCopy.v1Screen = model.multuplyColumn2(camera.view, tCopy.v1World);
                tCopy.v2Screen = model.multuplyColumn2(camera.view, tCopy.v2World);
                tCopy.v3Screen = model.multuplyColumn2(camera.view, tCopy.v3World);
                //
                tCopy.v1Screen = model.multuplyColumn2(projection, tCopy.v1Screen);
                tCopy.v2Screen = model.multuplyColumn2(projection, tCopy.v2Screen);
                tCopy.v3Screen = model.multuplyColumn2(projection, tCopy.v3Screen);

                tCopy.v1Screen.deformation();
                tCopy.v2Screen.deformation();
                tCopy.v3Screen.deformation();

                tCopy.v1Screen = model.multuplyColumn2(viewport, tCopy.v1Screen);
                tCopy.v2Screen = model.multuplyColumn2(viewport, tCopy.v2Screen);
                tCopy.v3Screen = model.multuplyColumn2(viewport, tCopy.v3Screen);

 //               normal = Vertex.normalize(normal);
 //               eyeView = Vertex.normalize(eyeView);
                //tCopy.sort();
                float decision = eyeView.x*normal.x + eyeView.y*normal.y + eyeView.z*normal.z;
                // double shade = (255 * angle);
                if (decision > 0) {
                    tCopy.sort();
                    //fillPolygon(tCopy);
                    interpolate(tCopy);
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

    public void LambertLight(Vertex normal){
        Vertex light = new Vertex(-1, -1, -1);
        light = Vertex.normalize(light);
        float angle = normal.x * -light.x + normal.y * -light.y + normal.z * -light.z;
        angle = Math.max(0, angle);
        angle = (float) Math.pow(angle, 1/2.2f) * 255;
        int lightning = (int) angle;
        g2.setColor(new Color(lightning, lightning, lightning));

    }

    public void FongLight(Vertex CurN, Vertex pointWorld){
        float modelColorRed = modelColor.getRed() / 255.f;
        float modelColorGreen = modelColor.getGreen() / 255.f;
        float modelColorBlue = modelColor.getBlue() / 255.f;

        //FongBackground
        Color backgroundColor = new Color(128, 128, 128);
        float modelBackColorRed = Math.round(modelColorRed * backgroundColor.getRed());
        float modelBackColorGreen = Math.round(modelColorGreen * backgroundColor.getGreen());
        float modelBackColorBlue = Math.round(modelColorBlue * backgroundColor.getBlue());


        //FongDiffuse
        //CurN = Vertex.normalize(CurN);
        float angle = CurN.x * -Light.x + CurN.y * -Light.y + CurN.z * -Light.z;
        angle = Math.max(0, angle);
        Color diffuseLight = new Color(255, 255, 255);
        float modelDiffColorRed = Math.round(modelColorRed * diffuseLight.getRed() * angle);
        float modelDiffColorGreen = Math.round(modelColorGreen * diffuseLight.getGreen() * angle);
        float modelDiffColorBlue = Math.round(modelColorRed * diffuseLight.getRed() * angle);

        //FongSpecular
        Color specularLight = diffuseLight;
        double modelSpecularCoefRed = 0.2;
        double modelSpecularCoefGreen = 0.2;
        double modelSpecularCoefBlue = 0.2;
        int specularCoef = 64;
        Vertex eyeView =Vertex.vertexDifference(camera.eye, pointWorld);
        eyeView = Vertex.normalize(eyeView);
        Vertex reflection = ReflectLight(CurN, Light);
        float reflectionAngle = reflection.x * eyeView.x + reflection.y * eyeView.y + reflection.z * eyeView.z;
        reflectionAngle = Math.max(0, reflectionAngle);
        reflectionAngle = (float) Math.pow(reflectionAngle, specularCoef);

        float modelReflectColorRed = Math.round(modelSpecularCoefRed * specularLight.getRed() * reflectionAngle);
        float modelReflectColorGreen = Math.round(modelSpecularCoefGreen * specularLight.getGreen() * reflectionAngle);
        float modelReflectColorBlue = Math.round(modelSpecularCoefBlue * specularLight.getBlue() * reflectionAngle);

        int Red = (int) (modelBackColorRed + modelDiffColorRed + modelReflectColorRed);
        int Green = (int) (modelBackColorGreen + modelDiffColorGreen + modelReflectColorGreen);
        int Blue = (int) (modelBackColorBlue + modelDiffColorBlue + modelReflectColorBlue);


        Color currentModelColor = new Color(Red, Green, Blue);
        g2.setColor(currentModelColor);
    }

    public Vertex ReflectLight(Vertex CurN, Vertex Light){
        float angle = CurN.x * Light.x + CurN.y * Light.y + CurN.z * Light.z;
        //angle = Math.max(0, angle);
        Vertex deltaNorm = CurN;
        deltaNorm.x = deltaNorm.x * angle * 2;
        deltaNorm.y = deltaNorm.y * angle * 2;
        deltaNorm.z = deltaNorm.z * angle * 2;
        Vertex reflection = Vertex.vertexDifference(Light, deltaNorm);
        return reflection;
    }


    public void interpolate(PolygonInSpaces t){ //for Fong's lightening
        //in screen coordinates
        float dx1 = t.v2Screen.x - t.v1Screen.x;
        float dy1 = t.v2Screen.y - t.v1Screen.y;
        float dz1 = t.v2Screen.z - t.v1Screen.z;
        float dx2 = t.v3Screen.x - t.v1Screen.x;
        float dy2 = t.v3Screen.y - t.v1Screen.y;
        float dz2 = t.v3Screen.z - t.v1Screen.z;
        float dx3 = t.v3Screen.x - t.v2Screen.x;
        float dy3 = t.v3Screen.y - t.v2Screen.y;
        if (dy1 == 0){
            dy1 = 0.000001f;
            System.out.println("aaa");
        }
        if (dy2 == 0){
            dy2 = 0.000001f;
            System.out.println("bbb");
        }
        if (dy3 == 0){
            dy3 = 0.000001f;
            System.out.println("ccc");
        }
        float dz3 = t.v3Screen.z - t.v2Screen.z;
        float kx1 = dx1/dy1;
        float kx2 = dx2/dy2;
        float kx3 = dx3/dy3;
        float kz1 = dz1/dy1;
        float kz2 = dz2/dy2;
        float kz3 = dz3/dy3;

        //in world coordinates
        Vertex dn1 = Vertex.vertexDifference(t.vn2, t.vn1);
        Vertex dn2 = Vertex.vertexDifference(t.vn3, t.vn1);
        Vertex dn3 = Vertex.vertexDifference(t.vn3, t.vn2);
        Vertex kn1 = Vertex.divisionByNumber(dn1, dy1);
        Vertex kn2 = Vertex.divisionByNumber(dn2, dy2);
        Vertex kn3 = Vertex.divisionByNumber(dn3, dy3);
        float dx1W = t.v2World.x - t.v1World.x;
        float dx2W = t.v3World.x - t.v1World.x;
        float dx3W = t.v3World.x - t.v2World.x;
        float dy1W = t.v2World.y - t.v1World.y;
        float dy2W = t.v3World.y - t.v1World.y;
        float dy3W = t.v3World.y - t.v2World.y;
        float dz1W = t.v2World.z - t.v1World.z;
        float dz2W = t.v3World.z - t.v1World.z;
        float dz3W = t.v3World.z - t.v2World.z;
        float kx1W = dx1W/dy1;
        float kx2W = dx2W/dy2;
        float kx3W = dx3W/dy3;
        float ky1W = dy1W/dy1;
        float ky2W = dy2W/dy2;
        float ky3W = dy3W/dy3;
        float kz1W = dz1W/dy1;
        float kz2W = dz2W/dy2;
        float kz3W = dz3W/dy3;

        int topY = (int) Math.max(Math.ceil(t.v1Screen.y), 0);
        int bottomY = (int) Math.min(Math.ceil(t.v3Screen.y), frame.getHeight()-1);
        //float yW = t.v1World.y;

        for (int y = topY; y < bottomY; y++){
            float crossX1 = y < t.v2Screen.y ? t.v1Screen.x + kx1 * (y - t.v1Screen.y) : t.v2Screen.x + kx3 * (y - t.v2Screen.y);
            float crossX2 = t.v1Screen.x + kx2 * (y - t.v1Screen.y);
            float crossZ1 = y < t.v2Screen.y ? t.v1Screen.z + kz1 * (y - t.v1Screen.y) : t.v2Screen.z + kz3 * (y - t.v2Screen.y);
            float crossZ2 = t.v1Screen.z + kz2 * (y - t.v1Screen.y);

            float crossX1W = y < t.v2.y ? t.v1World.x + kx1W * (y - t.v1.y) : t.v2World.x + kx3W * (y - t.v2.y);
            float crossX2W = t.v1World.x + kx2W * (y - t.v1.y);

            float crossY1W = y < t.v2.y ? t.v1World.y + ky1W * (y - t.v1.y) : t.v2World.y + ky3W * (y - t.v2.y);
            float crossY2W = t.v1World.y + ky2W * (y - t.v1.y);
            float crossZ1W = y < t.v2.y ? t.v1World.z + kz1W * (y - t.v1.y) : t.v2World.z + kz3W * (y - t.v2.y);
            float crossZ2W = t.v1World.z + kz2W * (y - t.v1.y);
            Vertex crossN1 = y < t.v2.y ? Vertex.vertexAdding(t.vn1, Vertex.multiplicationByNumber(kn1,  y - t.v1.y)) : Vertex.vertexAdding(t.vn2, Vertex.multiplicationByNumber(kn3,  y - t.v2.y));
            Vertex crossN2 = Vertex.vertexAdding(t.vn1, Vertex.multiplicationByNumber(kn2, y - t.v1.y));

            if (crossX1 > crossX2){
                float temp = crossX1;
                crossX1 = crossX2;
                crossX2 = temp;
                temp = crossZ1;
                crossZ1 = crossZ2;
                crossZ2 = temp;
                temp = crossX1W;
                Vertex temp2 = crossN1;
                crossX1W = crossX2W;
                crossX2W = temp;
                temp = crossY2W;
                crossY2W = crossY1W;
                crossY1W = temp;
                temp = crossZ1W;
                crossZ1W = crossZ2W;
                crossZ2W = temp;
                crossN1 = crossN2;
                crossN2 = temp2;
            }

//            if (crossX1W > crossX2W){
//                float temp = crossX1W;
//                Vertex temp2 = crossN1;
//                crossX1W = crossX2W;
//                crossX2W = temp;
//                temp = crossZ1W;
//                crossZ1W = crossZ2W;
//                crossZ2W = temp;
//                crossN1 = crossN2;
//                crossN2 = temp2;
//                temp = crossY2W;
//                crossY2W = crossY1W;
//                crossY1W = temp;
//
//            }

            float kz = (crossZ2 - crossZ1) / (crossX2 - crossX1);
            int leftX = (int) Math.max(Math.ceil(crossX1), 0);
            int rightX = (int) Math.min(Math.ceil(crossX2), frame.getWidth()-1);

            float kxW = (crossX2W - crossX1W) / (crossX2 - crossX1);
            float kyW = (crossY2W - crossY1W) / (crossX2 - crossX1);
            float kzW = (crossZ2W - crossZ1W) / (crossX2 - crossX1);
            if (crossX2 - crossX1 == 0){
                System.out.println("ddd");
            }
            Vertex kn = Vertex.divisionByNumber(Vertex.vertexDifference(crossN2, crossN1), crossX2 - crossX1);
            //float xW = crossX1W;


            for (int x = leftX; x < rightX; x++){
                float z = crossZ1 + kz * (x - crossX1);
                float xW = crossX1W + kxW * (x - crossX1);
                float yW = crossY1W + kyW * (x - crossX1);
                float zW = crossZ1W + kzW * (x - crossX1);
                Vertex d = Vertex.multiplicationByNumber(kn, x - crossX1);
                Vertex b = Vertex.vertexAdding(crossN1, d);
                Vertex curN = Vertex.normalize2(b);

                if ((zBuffer[y][x] > z && camera.eye.z > 0)) {
                    zBuffer[y][x] = z;
                    ///////////////////////////////////////
                    //Your calculations will be here
                    // (x, y, z) - the point in screen coordinates
                    // (xW, yW, zW) - the same point in world coordinates
                    // curN - normal vertex to this point
                    Vertex pointWorld = new Vertex(xW, yW, zW);
                    Vertex curN_copy = new Vertex(curN.x, curN.y, curN.z, curN.w);
                    FongLight(curN_copy, pointWorld);
                    //////////////////////////////////////
                    //for world coordinates debug
                    Color a = new Color(Math.max(Math.min(xW, 1), 0), Math.max(Math.min(yW, 1), 0), Math.max(Math.min(zW, 1), 0));
                    //for normals debug
                    //Color a = new Color(Math.min((curN.x + 1) * 0.5f, 1), Math.min((curN.y + 1) * 0.5f, 1), Math.min((curN.z + 1) * 0.5f, 1));
                    g2.setColor(a);
                    g2.fillRect(x, y, 1, 1);
                }
            }
        }
    }


        public void fillPolygon(Polygon t){ //for Lambert's lightening
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
            }

        }

        public void drawDDALine(float x1, float y1, float z1, float x2, float y2, float z2){
            float dx = x2 - x1;
            float dy = y2 - y1;
            float dz = z2 - z1;
            int step;
            step = Math.round(Math.max(Math.abs(dx), Math.abs(dz)));
            float xInc = dx / step;
            float yInc = dy / step;
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
                y += yInc;
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


}
