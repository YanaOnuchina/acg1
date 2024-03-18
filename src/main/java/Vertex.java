public class Vertex {
    float x;
    float y;
    float z;
    float w;

    public Vertex(){

    }

    public Vertex(Vertex vertex){
        this.x = vertex.x;
        this.y = vertex.y;
        this.z = vertex.z;
        this.w = vertex.w;
    }

    public Vertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 1;
    }

    public Vertex(float x, float y, float z, float w) { //for normals
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
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

    public static Vertex vertexAdding(Vertex v1, Vertex v2){
        return new Vertex(v1.x + v2.x , v1.y + v2.y, v1.z + v2.z, v1.w + v2.w);
    }

    public static Vertex multiplicationByNumber(Vertex v, float n){
        return new Vertex(v.x * n, v.y * n, v.z * n, v.w * n);
    }

    public static Vertex divisionByNumber(Vertex v, float n){
        return new Vertex(v.x / n, v.y / n, v.z / n, v.w / n);
    }

    public static Vertex normalize(Vertex v){
        double length = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
        v.x = (float) (v.x/length);
        v.y = (float) (v.y/length);
        v.z = (float) (v.z/length);
        return v;

    }

    public static Vertex normalize2(Vertex v){
        double length = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
        return new Vertex((float) (v.x/length), (float) (v.y/length), (float) (v.z/length));

    }

    public void deformation(){
        this.x = this.x/this.w;
        this.y = this.y/this.w;
        this.z = this.z/this.w;
        this.w = this.w/this.w;
    }

    @Override
    public String toString() {
        return "vertex {" + x + "; " + y + "; " + z + "}";
    }
}
