/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fix;

/**
 *
 * @author pc
 */
import javax.swing.JPanel;
import java.util.ArrayList;

public abstract class Lienzo extends JPanel{
    public Lienzo(){
    }
    
    public abstract void actualizar(int id);
    
    public abstract void actualizar(ArrayList<Boton> botoni);
    
    public void mostrar(){
        
    }
}
