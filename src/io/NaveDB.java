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
public class NaveDB extends ObjetoDB{
    private ProyectilDB proyectil;
    //select n.idNave, n.tipoNave, n.perfil, n.imgNave, n.estado, n.idProyectil from nave n  where n.jugador = 0 and n.estado = 1

    public NaveDB(int id, String t, BufferedImage perf, BufferedImage img, ProyectilDB pr, int dano, int resist){
        super(id, perf, img, dano, resist, t);
        proyectil = pr;
    }
    
    public ProyectilDB getProyectil(){
        return proyectil;
    }
}

