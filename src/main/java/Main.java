public class Main {

    public static void main(String[] args){
        ObjParser parser = new ObjParser();
        Model model = parser.readFile();
        Viewer viewer = new Viewer();
        viewer.addModel(model);
        viewer.draw();
        viewer.frame.setFocusable(true);
        viewer.frame.addKeyListener(new MyKeyListener(model, viewer.renderPanel));
    }

}
