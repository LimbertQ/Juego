/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fix;

/**
 *
 * @author pc
 */
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseInput extends MouseAdapter{

    public static int X, Y;
    public static boolean MLB;

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            MLB = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            MLB = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        X = e.getX();
        Y = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        X = e.getX();
        Y = e.getY();
    }

}
