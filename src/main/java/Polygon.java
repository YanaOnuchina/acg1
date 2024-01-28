public class Polygon {
    Vertex v1;
    Vertex v2;
    Vertex v3;

    public Polygon(Model model, int v1, int v2, int v3) {
        this.v1 = model.vertexes.get(v1 - 1); //because numeration in file starts with 1
        this.v2 = model.vertexes.get(v2 - 1);
        this.v3 = model.vertexes.get(v3 - 1);
    }

    @Override
    public String toString() {
        return "polygon {" + v1 + ", " + v2 + ", " + v3 + "}";
    }
}
