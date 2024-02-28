public class Polygon {
    Vertex v1;
    Vertex v2;
    Vertex v3;
    Vertex vn1;
    Vertex vn2;
    Vertex vn3;

    public Polygon() {

    }

    public Polygon(Model model, int v1, int v2, int v3, int vn1, int vn2, int vn3) {
        this.v1 = model.vertexes.get(v1 - 1); //because numeration in file starts with 1
        this.v2 = model.vertexes.get(v2 - 1);
        this.v3 = model.vertexes.get(v3 - 1);
        this.vn1 = model.normals.get(vn1 - 1);
        this.vn2 = model.normals.get(vn2 - 1);
        this.vn3 = model.normals.get(vn3 - 1);
    }

    public Polygon(Vertex v1, Vertex v2, Vertex v3, Vertex vn1, Vertex vn2, Vertex vn3){
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.vn1 = vn1;
        this.vn2 = vn2;
        this.vn3 = vn3;
    }

    public Polygon(Vertex v1, Vertex v2, Vertex v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

//    public Polygon getCopy(){
//        return new Polygon(new Vertex(v1.x, v1.y, v1.z),
//                        new Vertex(v2.x, v2.y, v2.z),
//                        new Vertex(v3.x, v3.y, v3.z),
//                        new Vertex(vn1.x, vn1.y, vn1.z),
//                        new Vertex(vn2.x, vn2.y, vn2.z),
//                        new Vertex(vn3.x, vn3.y, vn3.z));
//    }

    public PolygonInSpaces getCopyInSpaces(){
        return new PolygonInSpaces(this);
    }

    @Override
    public String toString() {
        return "polygon {" + v1 + ", " + v2 + ", " + v3 + "}";
    }

    public void sort(){
        Vertex maxVertex = v1;
        Vertex mediumVertex = v2;
        Vertex minVertex = v3;
        Vertex maxNormal = vn1;
        Vertex mediumNormal = vn2;
        Vertex minNormal = vn3;
        Vertex temp;
        Vertex tempNormal;
        if (maxVertex.y < mediumVertex.y){
            temp = maxVertex;
            tempNormal = maxNormal;
            maxVertex = mediumVertex;
            maxNormal = mediumNormal;
            mediumVertex = temp;
            mediumNormal = tempNormal;
        }
        if (minVertex.y > maxVertex.y){
            temp = maxVertex;
            tempNormal = maxNormal;
            maxVertex = minVertex;
            maxNormal = minNormal;
            minVertex = mediumVertex;
            minNormal = mediumNormal;
            mediumVertex = temp;
            mediumNormal = tempNormal;
        }
        else if (minVertex.y > mediumVertex.y){
            temp = minVertex;
            tempNormal = minNormal;
            minVertex = mediumVertex;
            minNormal = mediumNormal;
            mediumVertex = temp;
            mediumNormal = tempNormal;
        }
        v1 = minVertex;
        v2 = mediumVertex;
        v3 = maxVertex;
        vn1 = minNormal;
        vn2 = mediumNormal;
        vn3 = maxNormal;
    }

}
