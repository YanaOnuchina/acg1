public class Polygon {
    Vertex v1;
    Vertex v2;
    Vertex v3;

    public Polygon(Model model, int v1, int v2, int v3) {
        this.v1 = model.vertexes.get(v1 - 1); //because numeration in file starts with 1
        this.v2 = model.vertexes.get(v2 - 1);
        this.v3 = model.vertexes.get(v3 - 1);
    }

    public Polygon(Vertex v1, Vertex v2, Vertex v3){
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    @Override
    public String toString() {
        return "polygon {" + v1 + ", " + v2 + ", " + v3 + "}";
    }

    public void sort(){
        Vertex maxVertex = v1;
        Vertex mediumVertex = v2;
        Vertex minVertex = v3;
        Vertex temp;
        if (maxVertex.y < mediumVertex.y){
            temp = maxVertex;
            maxVertex = mediumVertex;
            mediumVertex = temp;
        }
        if (minVertex.y > maxVertex.y){
            temp = maxVertex;
            maxVertex = minVertex;
            minVertex = mediumVertex;
            mediumVertex = temp;
        }
        else if (minVertex.y > mediumVertex.y){
            temp = minVertex;
            minVertex = mediumVertex;
            mediumVertex = temp;
        }
        v1 = minVertex;
        v2 = mediumVertex;
        v3 = maxVertex;
    }

}
