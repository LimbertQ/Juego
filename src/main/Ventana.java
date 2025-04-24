/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

/**
 *
 * @author pc
 */
import javax.swing.JFrame;
import java.awt.Dimension;
import fix.Window;
public class Ventana extends JFrame{
    private Thread thread;
    private Window lienzo;
    public Ventana()
    {
        setTitle("The Game");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        
        lienzo = new Window();
        lienzo.setPreferredSize(new Dimension(1000,600));
        lienzo.setMaximumSize(new Dimension(1000,600));
        lienzo.setMinimumSize(new Dimension(1000,600));
        add(lienzo);
        setVisible(true);
        lienzo.start();
        
    }
    public static void main(String[] args) {
        new Ventana();
    }
} 
