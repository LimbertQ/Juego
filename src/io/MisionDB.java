/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io;

/**
 *
 * @author pc
 */
public class MisionDB{
    private int id;
    private String nombre;
    private String musica;
    private int idNivel;
    public MisionDB(int i, String nom, String mus, int idNiv){
        this.id = i;
        this.nombre = nom;
        this.musica = mus;
        this.idNivel = idNiv;
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
    
    public String getMusica(){
        return musica;
    }
    
    public void setMusica(String f){
        musica = f;
    }
    
    public int getNivel(){
        return idNivel;
    }
    
    public void setNivel(int idNiv){
        idNivel = idNiv;
    }
}