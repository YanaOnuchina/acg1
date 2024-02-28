public class PolygonInSpaces extends Polygon{
    Vertex v1World;
    Vertex v2World;
    Vertex v3World;
    Vertex v1Screen;
    Vertex v2Screen;
    Vertex v3Screen;

    public PolygonInSpaces(Polygon polygon) {
        this.v1 = new Vertex(polygon.v1);
        this.v2 = new Vertex(polygon.v2);
        this.v3 = new Vertex(polygon.v3);
        this.vn1 = new Vertex(polygon.vn1);
        this.vn2 = new Vertex(polygon.vn2);
        this.vn3 = new Vertex(polygon.vn3);
        v1World = new Vertex();
        v2World = new Vertex();
        v3World = new Vertex();
        v1Screen = new Vertex();
        v2Screen = new Vertex();
        v3Screen = new Vertex();
    }

    @Override
    public void sort(){
        Vertex maxVertex = v1;
        Vertex mediumVertex = v2;
        Vertex minVertex = v3;
        Vertex maxVertexWorld = v1World;
        Vertex mediumVertexWorld = v2World;
        Vertex minVertexWorld = v3World;
        Vertex maxVertexScreen = v1Screen;
        Vertex mediumVertexScreen = v2Screen;
        Vertex minVertexScreen = v3Screen;
        Vertex maxNormal = vn1;
        Vertex mediumNormal = vn2;
        Vertex minNormal = vn3;
        Vertex temp;
        Vertex tempNormal;
        Vertex tempWorld;
        Vertex tempScreen;
        if (maxVertexScreen.y < mediumVertexScreen.y){
            temp = maxVertex;
            tempNormal = maxNormal;
            tempWorld = maxVertexWorld;
            tempScreen = maxVertexScreen;
            maxVertex = mediumVertex;
            maxNormal = mediumNormal;
            maxVertexWorld = mediumVertexWorld;
            maxVertexScreen = mediumVertexScreen;
            mediumVertex = temp;
            mediumNormal = tempNormal;
            mediumVertexWorld = tempWorld;
            mediumVertexScreen = tempScreen;
        }
        if (minVertexScreen.y > maxVertexScreen.y){
            temp = maxVertex;
            tempNormal = maxNormal;
            tempWorld = maxVertexWorld;
            tempScreen = maxVertexScreen;
            maxVertex = minVertex;
            maxNormal = minNormal;
            maxVertexWorld = minVertexWorld;
            maxVertexScreen = minVertexScreen;
            minVertex = mediumVertex;
            minNormal = mediumNormal;
            minVertexWorld = mediumVertexWorld;
            minVertexScreen = mediumVertexScreen;
            mediumVertex = temp;
            mediumNormal = tempNormal;
            mediumVertexWorld = tempWorld;
            mediumVertexScreen = tempScreen;
        }
        else if (minVertexScreen.y > mediumVertexScreen.y){
            temp = minVertex;
            tempNormal = minNormal;
            tempWorld = minVertexWorld;
            tempScreen = minVertexScreen;
            minVertex = mediumVertex;
            minNormal = mediumNormal;
            minVertexWorld = mediumVertexWorld;
            minVertexScreen = mediumVertexScreen;
            mediumVertex = temp;
            mediumNormal = tempNormal;
            mediumVertexWorld = tempWorld;
            mediumVertexScreen = tempScreen;
        }
        v1 = minVertex;
        v2 = mediumVertex;
        v3 = maxVertex;
        vn1 = minNormal;
        vn2 = mediumNormal;
        vn3 = maxNormal;
        v1World = minVertexWorld;
        v2World = mediumVertexWorld;
        v3World = maxVertexWorld;
        v1Screen = minVertexScreen;
        v2Screen = mediumVertexScreen;
        v3Screen = maxVertexScreen;
    }
}
