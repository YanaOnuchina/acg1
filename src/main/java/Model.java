import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;

public class Model {

    public final int ZOOM_POWER = 2;

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
        for (Vertex v: vertexes){
            SimpleMatrix vertex = new SimpleMatrix(new double[][] {
                    new double[]{v.x, v.y, v.z, 1},
            });
            SimpleMatrix result = vertex.mult(zoomMatrix);
            v.x = (float) result.get(0, 0);
            v.y = (float) result.get(0, 1);
            v.z = (float) result.get(0, 2);
        }
    }

    public void lessenModel(){
        double zoomPower = 1d/ZOOM_POWER;
        SimpleMatrix zoomMatrix = new SimpleMatrix(new double[][] {
                new double[]{zoomPower, 0, 0, 0},
                new double[]{0, zoomPower, 0, 0},
                new double[]{0, 0, zoomPower, 0},
                new double[]{0, 0, 0, 1},
        });
        for (Vertex v: vertexes){
            SimpleMatrix vertex = new SimpleMatrix(new double[][] {
                    new double[]{v.x, v.y, v.z, 1},
            });
            SimpleMatrix result = vertex.mult(zoomMatrix);
            v.x = (float) result.get(0, 0);
            v.y = (float) result.get(0, 1);
            v.z = (float) result.get(0, 2);
        }
    }

    @Override
    public String toString() {
        return vertexes.toString() + '\n' + polygons.toString();
    }
}
