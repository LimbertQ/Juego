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
import java.io.File;

public class EpisodioDB{
    private int id;
    private String nombre;
    private BufferedImage fondo;
    private File apertura;
    private File cierre;
    private boolean estado;
    public EpisodioDB(int i, String nom, BufferedImage f, File a, File c, int e){
        this.id = i;
        this.nombre = nom;
        this.fondo = f;
        this.apertura = a;
        this.cierre = c;
        if(e == 1)
            estado = true;
        else
            estado = false;
    }

    public int getId(){
        return id;
    }

    public void setId(int i){
        id = i;
    }

    public String getNom(){
        return nombre;
    }

    public void setNombre(String n){
        nombre = n;
    }

    public BufferedImage getFondo(){
        return fondo;
    }

    public void setFondo(BufferedImage f){
        fondo = f;
    }

    public File getApertura(){
        return apertura;
    }
    
    public File getCierre(){
        return cierre;
    }

    public boolean getEstado(){
        return estado;
    }

    public void setEstado(int e){
        if(e == 1)
            estado = true;
        else
            estado = false;
    }
}
