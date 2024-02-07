import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MyKeyListener implements KeyListener {


    Model model;
    Viewer viewer;
    JPanel renderPanel;

    public MyKeyListener(Model model, JPanel renderPanel, Viewer viewer){
        this.model = model;
        this.renderPanel = renderPanel;
        this.viewer = viewer;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char key = e.getKeyChar();
        switch (key) {
            case '+':
                model.enlargeModel();
                break;
            case '-':
                model.lessenModel();
                break;
            case 'd':
                model.transformX(model.MOVEMENT);
                break;
            case 'a':
                model.transformX(-model.MOVEMENT);
                break;
            case 'w':
                model.transformY(model.MOVEMENT);
                break;
            case 's':
                model.transformY(-model.MOVEMENT);
                break;
            case 'k':
                viewer.camera.moveHorizontal(viewer.camera.ANGLE_MOVEMENT);
                break;
            case 'j':
                viewer.camera.moveHorizontal(-viewer.camera.ANGLE_MOVEMENT);
                break;
            case 'i':
                viewer.camera.moveVertical(viewer.camera.ANGLE_MOVEMENT);
                viewer.camera.isVertMove = true;
                break;
            case 'm':
                viewer.camera.moveVertical(-viewer.camera.ANGLE_MOVEMENT);
                viewer.camera.isVertMove = true;
                break;
        }
        renderPanel.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
