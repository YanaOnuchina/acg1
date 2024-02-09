import org.ejml.simple.SimpleMatrix;
public class Camera {
    Vertex eye;
    Vertex target;
    Vertex up;
    SimpleMatrix view;
    Boolean isVertMove;

    public final float ANGLE_MOVEMENT = (float) Math.PI / 32;


    public Camera(Vertex eye, Vertex target, Vertex up){
        this.eye = eye;
        this.target = target;
        this.up = up;
        isVertMove = false;
    }

    public void cameraNormalize(){
       Vertex zAxis = Vertex.normalize(Vertex.vertexDifference(eye, target));
       Vertex xAxis = Vertex.normalize(Vertex.vertexMultiplication(up, zAxis));
        Vertex yAxis;
       if (!isVertMove) yAxis = up;
       else yAxis = Vertex.vertexMultiplication(zAxis, xAxis);
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

    public SphericalCoordinates convertCoordinatesToSpherical(float x, float y, float z){
        float r = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        float phi = x != 0 ? (float) Math.atan(y/x) : (float) (Math.PI / 2);
        float theta = (float) Math.acos(z/(r));
        if (x < 0) theta *= -1;

        return new SphericalCoordinates(r, theta, phi);
    }

    public void moveHorizontal(float movementSize){
        SphericalCoordinates coord = convertCoordinatesToSpherical(eye.x, eye.y, eye.z);
        coord.theta += movementSize;
        eye = coord.convertToDecartCoordinates();
    }

    public void moveVertical(float movementSize) {
        SphericalCoordinates coord = convertCoordinatesToSpherical(eye.x, eye.y, eye.z);
        if (coord.phi + movementSize < (float)Math.PI / 2 - 0.1 && coord.phi + movementSize > (float)-Math.PI / 2 + 0.1) coord.phi += movementSize;
        eye = coord.convertToDecartCoordinates();

    }

}
