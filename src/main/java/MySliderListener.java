import javax.swing.*;

public class MySliderListener {

    Model model;
    Viewer viewer;
    JPanel renderPanel;

    public MySliderListener(Model model, JPanel renderPanel, Viewer viewer){
        this.model = model;
        this.renderPanel = renderPanel;
        this.viewer = viewer;
    }

    public void pitchListener(int value){
        double heading = Math.toRadians(value) / 10;
        model.rotateX(heading);
        renderPanel.repaint();
        viewer.frame.requestFocus();
    }

    public void headingListener(int value){
        double heading = Math.toRadians(value) / 10;
        model.rotateY(heading);
        renderPanel.repaint();
        viewer.frame.requestFocus();
    }

    public void zListener(int value){
        double heading = Math.toRadians(value) / 10;
        model.rotateZ(heading);
        renderPanel.repaint();
        viewer.frame.requestFocus();
    }

}
