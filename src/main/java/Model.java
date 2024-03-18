import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;

public class Model {

    public final int ZOOM_POWER = 2;
    public final double MOVEMENT = 10;
    public final

    ArrayList<Vertex> vertexes;
    ArrayList<Polygon> polygons;
    ArrayList<Vertex> normals;

    public SimpleMatrix modelMatrix = new SimpleMatrix(new double[][] {
                new double[]{1, 0, 0, 0},
                new double[]{0, 1, 0, 0},
                new double[]{0, 0, 1, 0},
                new double[]{0, 0, 0, 1},
    });

    public Model() {
        vertexes = new ArrayList<Vertex>();
        polygons = new ArrayList<Polygon>();
        normals = new ArrayList<Vertex>();
    }

    public void addVertex(Vertex vertex){
        vertexes.add(vertex);
    }

    public void addPolygon(Polygon polygon){
        polygons.add(polygon);
    }

    public void addNormal(Vertex normal){ normals.add(normal); }

    public void enlargeModel(){
        SimpleMatrix zoomMatrix = new SimpleMatrix(new double[][] {
                new double[]{ZOOM_POWER, 0, 0, 0},
                new double[]{0, ZOOM_POWER, 0, 0},
                new double[]{0, 0, ZOOM_POWER, 0},
                new double[]{0, 0, 0, 1},
        });
        SimpleMatrix result = modelMatrix.mult(zoomMatrix);
        modelMatrix = result;
    }

    public void lessenModel(){
        double zoomPower = 1d/ZOOM_POWER;
        SimpleMatrix zoomMatrix = new SimpleMatrix(new double[][] {
                new double[]{zoomPower, 0, 0, 0},
                new double[]{0, zoomPower, 0, 0},
                new double[]{0, 0, zoomPower, 0},
                new double[]{0, 0, 0, 1},
        });
        SimpleMatrix result = modelMatrix.mult(zoomMatrix);
        modelMatrix = result;
    }

    public void rotateX(double rotation){
        SimpleMatrix transformMatrix = new SimpleMatrix(new double[][] {
                new double[]{1, 0, 0, 0},
                new double[]{0, Math.cos(rotation), -Math.sin(rotation), 0},
                new double[]{0, Math.sin(rotation), Math.cos(rotation), 0},
                new double[]{0, 0, 0, 1},
        });
        SimpleMatrix result = modelMatrix.mult(transformMatrix);
        modelMatrix = result;
    }

    public void rotateY(double rotation){
        SimpleMatrix transformMatrix = new SimpleMatrix(new double[][] {
                new double[]{Math.cos(rotation), 0, Math.sin(rotation), 0},
                new double[]{0, 1, 0, 0},
                new double[]{-Math.sin(rotation), 0, Math.cos(rotation), 0},
                new double[]{0, 0, 0, 1},
        });
        SimpleMatrix result = transformMatrix.mult(modelMatrix);
        modelMatrix = result;

    }

    public void rotateZ(double rotation){
        SimpleMatrix transformMatrix = new SimpleMatrix(new double[][] {
                new double[]{Math.cos(rotation), -Math.sin(rotation), 0, 0},
                new double[]{Math.sin(rotation), Math.cos(rotation), 0, 0},
                new double[]{0, 0, 1, 0},
                new double[]{0, 0, 0, 1},
        });
        SimpleMatrix result = transformMatrix.mult(modelMatrix);
        modelMatrix = result;
    }

    public void transformX(double movement){
        double currentPosition = modelMatrix.get(0, 3);
        modelMatrix.set(0, 3, currentPosition+movement);
    }

    public void transformY(double movement){
        double currentPosition = modelMatrix.get(1, 3);
        modelMatrix.set(1, 3, currentPosition+movement);
    }

    public Vertex multuplyColumn(SimpleMatrix currentMatrix, Vertex v){
        SimpleMatrix vertex = new SimpleMatrix(new double[][]{
                new double[]{v.x},
                new double[]{v.y},
                new double[]{v.z},
                new double[]{v.w},
        });
            SimpleMatrix helpMatrix = currentMatrix;
            SimpleMatrix result = helpMatrix.mult(vertex);
            v.x = (float) result.get(0, 0);
            v.y = (float) result.get(1, 0);
            v.z = (float) result.get(2, 0);
            v.w = (float) result.get(3, 0);
        return v;
    }

    public Vertex multuplyColumn2(SimpleMatrix currentMatrix, Vertex v){
        SimpleMatrix vertex = new SimpleMatrix(new double[][]{
                new double[]{v.x},
                new double[]{v.y},
                new double[]{v.z},
                new double[]{v.w},
        });
        SimpleMatrix helpMatrix = currentMatrix;
        SimpleMatrix result = helpMatrix.mult(vertex);
        return new Vertex((float) result.get(0, 0), (float) result.get(1, 0), (float) result.get(2, 0), (float) result.get(3, 0));
    }

    public double getModelDepth(){
//        double zMin = 10.0;
//        double zMax = 100.0;
        double zMin = Double.MAX_VALUE;
        double zMax = Double.MIN_VALUE;
        for(Vertex v: vertexes){
            if (v.z > zMax)
                zMax = v.z;
            else if (v.z < zMin)
                zMin = v.z;
        }
        return zMax - zMin;
    }

    @Override
    public String toString() {
        return vertexes.toString() + '\n' + polygons.toString();
    }
}
