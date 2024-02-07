public class SphericalCoordinates {
    float r;
    float theta;
    float phi;

    public SphericalCoordinates(float r, float theta, float phi) {
        this.r = r;
        this.theta = theta;
        this.phi = phi;
    }


    public Vertex convertToDecartCoordinates(){
        float x = (float) (r * Math.sin(theta) * Math.cos(phi));
        float y = (float) (r * Math.sin(theta) * Math.sin(phi));
        float z = (float) (r * Math.cos(theta));
        return new Vertex(x, y, z);
    }

}
