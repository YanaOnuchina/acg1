import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;

public class Model {

    public final int ZOOM_POWER = 2;
    public final int MOVEMENT = 10;
    public final

    ArrayList<Vertex> vertexes;
    ArrayList<Polygon> polygons;

    public Model() {
        vertexes = new ArrayList<Vertex>();
        polygons = new ArrayList<Polygon>();
    }

    public void addVertex(Vertex vertex){
        vertexes.add(vertex);
    }

    public void addPolygon(Polygon polygon){
        polygons.add(polygon);
    }

    public void enlargeModel(){
        SimpleMatrix zoomMatrix = new SimpleMatrix(new double[][] {
                new double[]{ZOOM_POWER, 0, 0, 0},
                new double[]{0, ZOOM_POWER, 0, 0},
                new double[]{0, 0, ZOOM_POWER, 0},
                new double[]{0, 0, 0, 1},
        });
        multuplyString(zoomMatrix);
    }

    public void lessenModel(){
        double zoomPower = 1d/ZOOM_POWER;
        SimpleMatrix zoomMatrix = new SimpleMatrix(new double[][] {
                new double[]{zoomPower, 0, 0, 0},
                new double[]{0, zoomPower, 0, 0},
                new double[]{0, 0, zoomPower, 0},
                new double[]{0, 0, 0, 1},
        });
        multuplyString(zoomMatrix);
    }

    public void rotateX(double rotation){
        SimpleMatrix transformMatrix = new SimpleMatrix(new double[][] {
                new double[]{1, 0, 0, 0},
                new double[]{0, Math.cos(rotation), -Math.sin(rotation), 0},
                new double[]{0, Math.sin(rotation), Math.cos(rotation), 0},
                new double[]{0, 0, 0, 1},
        });
        multuplyString(transformMatrix);
    }

    public void rotateY(double rotation){
        SimpleMatrix transformMatrix = new SimpleMatrix(new double[][] {
                new double[]{Math.cos(rotation), 0, Math.sin(rotation), 0},
                new double[]{0, 1, 0, 0},
                new double[]{-Math.sin(rotation), 0, Math.cos(rotation), 0},
                new double[]{0, 0, 0, 1},
        });
        multuplyString(transformMatrix);

    }

    public void transformX(int movement){
        SimpleMatrix transformMatrix = new SimpleMatrix(new double[][] {
                new double[]{1, 0, 0, movement},
                new double[]{0, 1, 0, 0},
                new double[]{0, 0, 1, 0},
                new double[]{0, 0, 0, 1},
        });
        multuplyColumn(transformMatrix);
    }

    public void transformY(int movement){
        SimpleMatrix transformMatrix = new SimpleMatrix(new double[][] {
                new double[]{1, 0, 0, 0},
                new double[]{0, 1, 0, movement},
                new double[]{0, 0, 1, 0},
                new double[]{0, 0, 0, 1},
        });
        multuplyColumn(transformMatrix);
    }

    public void multuplyString(SimpleMatrix currentMatrix){
        for (Vertex v: vertexes){
            SimpleMatrix vertex = new SimpleMatrix(new double[][] {
                    new double[]{v.x, v.y, v.z, 1},
            });
            SimpleMatrix result = vertex.mult(currentMatrix);
            v.x = (float) result.get(0, 0);
            v.y = (float) result.get(0, 1);
            v.z = (float) result.get(0, 2);
        }
    }

    public void multuplyColumn(SimpleMatrix currentMatrix){
        for (Vertex v: vertexes) {
            SimpleMatrix vertex = new SimpleMatrix(new double[][]{
                    new double[]{v.x},
                    new double[]{v.y},
                    new double[]{v.z},
                    new double[]{1},
            });
            SimpleMatrix helpMatrix = currentMatrix;
            SimpleMatrix result = helpMatrix.mult(vertex);
            v.x = (float) result.get(0, 0);
            v.y = (float) result.get(1, 0);
            v.z = (float) result.get(2, 0);
        }
    }

    @Override
    public String toString() {
        return vertexes.toString() + '\n' + polygons.toString();
    }
}
