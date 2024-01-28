public class Vertex {
    float x;
    float y;
    float z;

    public Vertex(float x, float y, float z) {
        this.x = x * 100; //scale to make model's size visible
        this.y = y * 100;
        this.z = z * 100;
    }

    @Override
    public String toString() {
        return "vertex {" + x + "; " + y + "; " + z + "}";
    }
}
