public class Vertex {
    float x;
    float y;
    float z;

    public Vertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Vertex vertexDifference(Vertex v1, Vertex v2){
        Vertex difference = new Vertex(0,0,0);
        difference.x = v1.x - v2.x;
        difference.y = v1.y - v2.y;
        difference.z = v1.z - v2.z;
        return difference;
    }

    public static Vertex vertexMultiplication(Vertex v1, Vertex v2){
        Vertex multiplication = new Vertex(0, 0, 0);
        multiplication.x = v1.y * v2.z - v1.z * v2.y;
        multiplication.y = v1.z * v2.x - v1.x * v2.z;
        multiplication.z = v1.x * v2.y - v1.y * v2.x;
        return multiplication;

    }

    public static Vertex normalize(Vertex v){
        double length = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
        v.x = (float) (v.x/length);
        v.y = (float) (v.y/length);
        v.z = (float) (v.z/length);
        return v;

    }

    @Override
    public String toString() {
        return "vertex {" + x + "; " + y + "; " + z + "}";
    }
}
