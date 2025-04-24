/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io;

/**
 *
 * @author pc
 */
import java.awt.image.BufferedImage;
public class ProyectilDB extends ObjetoDB{
    public ProyectilDB(int idP, String tipo, BufferedImage img, int d){
        super(idP, img, img, d, 1, tipo);
    }
}