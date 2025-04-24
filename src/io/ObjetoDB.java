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
public abstract class ObjetoDB{
    protected int id;
    protected int danio;
    protected int resistencia;
    protected BufferedImage imagen, perfil;
    protected String tipo;
    public ObjetoDB (int id, BufferedImage p, BufferedImage img, int d, int res, String t){
        this.id = id;
        danio = d;
        perfil = p;
        imagen = img;
        resistencia = res;
        tipo = t;
    }
    
    public int getId(){
        return id;
    }
    
    public BufferedImage getPerfil(){
        return perfil;
    }
    
    public BufferedImage getImagen(){
        return imagen;
    }
    
    public int getDanio(){
        return danio;
    }
    
    public int resistencia(){
        return resistencia;
    }
    
    public String getTipo(){
        return tipo;
    } 
}
