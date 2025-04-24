/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fix;

/**
 *
 * @author pc
 */
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import java.awt.Graphics;

public class BotonCanvas{
    private Rectangle boton;
    private Image imagen;
    private boolean mouseIn;
    private boolean click;
    public BotonCanvas(){
        boton = new Rectangle(300,10,50,50);
        ImageIcon imgIcon = new ImageIcon("res/fondos/pausa.png");
        imagen = imgIcon.getImage();
        click = false;
    }

    public void actualizar(){
        if(boton.contains(MouseInput.X, MouseInput.Y)){
            mouseIn = true;
        }else{
            mouseIn = false;
        }

        if(mouseIn && MouseInput.MLB) {
            //cv.pausar();
            //Dialogo dialogo = Dialogo.getInstance(cv);
            //dialogo.setUndecorated(true);
            //dialogo.setVisible(true);
            click = true;
        }
    } 
    
    public boolean click(){
        return click;
    }

    public void dibujar(Graphics g){
        g.drawImage(imagen,boton.x,boton.y,(int)boton.getWidth(),(int)boton.getHeight(),null);
    }
}