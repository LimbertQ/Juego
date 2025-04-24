/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fix;

/**
 *
 * @author pc
 */
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;


public class KeyBoard implements KeyListener{
    
    private boolean[] keys = new boolean[256];
    private boolean[] pressedKeys = new boolean[256]; // Para teclas recién presionadas
    
    public static boolean UP, LEFT, RIGHT, SHOOT;
    public static boolean SHOOT_PRESSED; // Tecla recién presionada
    
    public KeyBoard(){ 
        UP    = false;
        LEFT  = false;
        RIGHT = false;
        SHOOT = false;
        SHOOT_PRESSED = false;
    } 
    
    public void update(){ 
        UP = keys[KeyEvent.VK_UP];
        LEFT = keys[KeyEvent.VK_LEFT];
        RIGHT = keys[KeyEvent.VK_RIGHT];
        SHOOT = keys[KeyEvent.VK_P];
        
         // Detectar pulsación única
        if(keys[KeyEvent.VK_P] && !pressedKeys[KeyEvent.VK_P]) {
            SHOOT_PRESSED = true;
            pressedKeys[KeyEvent.VK_P] = true;
        } else {
            SHOOT_PRESSED = false;
        }
        
        // Si la tecla se soltó, restablecer el estado
        if(!keys[KeyEvent.VK_P]) {
            pressedKeys[KeyEvent.VK_P] = false;
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e){
        keys[e.getKeyCode()] = true;
    }
    
    @Override
    public void keyReleased(KeyEvent e){
        keys[e.getKeyCode()] = false;
    }
    
    @Override
    public void keyTyped(KeyEvent e){
        
    }
}
