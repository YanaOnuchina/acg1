import org.ejml.simple.SimpleMatrix;
public class Camera {
    Vertex eye;
    Vertex target;
    Vertex up;
    SimpleMatrix view;


    public Camera(Vertex eye, Vertex target, Vertex up){
        this.eye = eye;
        this.target = target;
        this.up = up;
    }

    public void cameraNormalize(){
       Vertex zAxis = Vertex.normalize(Vertex.vertexDifference(eye, target));
       Vertex xAxis = Vertex.normalize(Vertex.vertexMultiplication(up, zAxis));
       Vertex yAxis = up;
       double x1 = xAxis.x * eye.x + xAxis.y * eye.y + xAxis.z * eye.z;
       double y1 = yAxis.x * eye.x + yAxis.y * eye.y + yAxis.z * eye.z;
       double z1 = zAxis.x * eye.x + zAxis.y * eye.y + zAxis.z * eye.z;
       view = new SimpleMatrix(new double[][] {
               new double[]{xAxis.x, xAxis.y, xAxis.z, -x1},
               new double[]{yAxis.x, yAxis.y, yAxis.z, -y1},
               new double[]{zAxis.x, zAxis.y, zAxis.z, -z1},
               new double[]{0, 0, 0, 1},
       });
    }

}
