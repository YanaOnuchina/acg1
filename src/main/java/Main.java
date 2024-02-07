public class Main {

    public static void main(String[] args){
        ObjParser parser = new ObjParser();
        Model model = parser.readFile();
        Viewer viewer = new Viewer();
        Camera camera = new Camera(new Vertex(1, 0, 10), new Vertex(0, 0, 0), new Vertex(0, 1, 0));
        viewer.addModel(model);
        viewer.addCamera(camera);
        viewer.draw();
        viewer.frame.setFocusable(true);
        viewer.frame.addKeyListener(new MyKeyListener(model, viewer.renderPanel, viewer));
    }

}
