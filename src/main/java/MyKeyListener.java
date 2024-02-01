import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MyKeyListener implements KeyListener {


    Model model;
    Viewer viewer;
    JPanel renderPanel;

    public MyKeyListener(Model model, JPanel renderPanel){
        this.model = model;
        this.renderPanel = renderPanel;
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
