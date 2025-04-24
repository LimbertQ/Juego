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
import java.awt.Font;
import java.util.ArrayList;
import java.io.File;

import javax.sound.sampled.Clip;

import java.sql.*;

public class Assets {
    public static boolean loaded = false;
    public static float count = 0;
    public static float MAX_COUNT = 58;

    public static BufferedImage fondo;
    public static BufferedImage speed;
    public static BufferedImage[] shieldEffect = new BufferedImage[3];
    //animacion cuando explota
    public static BufferedImage[] exp = new BufferedImage[9];
    
    public static BufferedImage blackHole;
    
    public static MeteoroDB[] bigs = new MeteoroDB[4];
    public static MeteoroDB[] meds = new MeteoroDB[2];
    public static MeteoroDB[] smalls = new MeteoroDB[2];
    public static MeteoroDB[] tinies = new MeteoroDB[2];

    public static ArrayList<NaveDB> enemigos;
    public static ArrayList<NaveDB> aliados;
    //numeros para el juego
    public static BufferedImage[] numbers = new BufferedImage[11];
    //vida
    public static PowerUpDB life;
    // fonts

    public static Font fontBig;
    public static Font fontMed;

    public static Clip backgroundMusic, explosion, playerLoose, playerShoot, ufoShoot, powerUp, ufoSound;

    public static PowerUpDB orb, doubleScore, doubleGun, fastFire, shield, star;

    //nivel
    public static ArrayList<EpisodioDB> episodios;
    //subnivel
    public static ArrayList<MisionDB> misiones;
    //id del nivel actual
    public static int idSubXNiv = -1;


    
    
    
    private static DatabaseHelper db;
    public static void inicia()
    {
        fondo = loadImage("/res/fondos/fondo2.jpeg");
        episodios = new ArrayList<EpisodioDB>();
        misiones = new ArrayList<MisionDB>();

        enemigos = new ArrayList<NaveDB>();
        aliados = new ArrayList<NaveDB>();

        //bigs = new ArrayList<MeteoroDB>();
        //meds = new ArrayList<MeteoroDB>();
        //smalls = new ArrayList<MeteoroDB>();
        //tinies = new ArrayList<MeteoroDB>();
        //blackHole = loadImage("/res/agujeroNegro/blackHole20.png");

        
        speed = loadImage("/res/efectos/fire08.png");
        blackHole = loadImage("/res/agujeroNegro/blackHole20.png");
        //life = Loader.ImageLoader("/res/otros/life.png");
        life = new PowerUpDB(1, loadImage("/res/otros/life.png"), loadImage("/res/otros/life.png"), 1, 1, "poder");
        
        //musica
        backgroundMusic = Loader.loadSound("/res/sonidos/fondoMu.wav");
        explosion = Loader.loadSound("/res/sonidos/explosion.wav");
        playerLoose = Loader.loadSound("/res/sonidos/playerLoose.wav");
        playerShoot = Loader.loadSound("/res/sonidos/playerShoot.wav");
        ufoShoot = Loader.loadSound("/res/sonidos/ufoShoot.wav");
        powerUp = loadSound("/res/sonidos/powerUp.wav");
        ufoSound = loadSound("/res/sonidos/ufoSpawner1.wav");
        //powerUps
        orb = new PowerUpDB(1, loadImage("/res/poderes/orb.png"), loadImage("/res/poderes/orb.png"), 1, 1, "poder");
        doubleScore = new PowerUpDB(1, loadImage("/res/poderes/doubleScore.png"), loadImage("/res/poderes/doubleScore.png"), 1, 1, "poder");
        doubleGun = new PowerUpDB(1, loadImage("/res/poderes/doubleGun.png"), loadImage("/res/poderes/doubleGun.png"), 1, 1, "poder");
        fastFire = new PowerUpDB(1, loadImage("/res/poderes/fastFire.png"), loadImage("/res/poderes/fastFire.png"), 1, 1, "poder");
        star = new PowerUpDB(1, loadImage("/res/poderes/star.png"), loadImage("/res/poderes/star.png"), 1, 1, "poder");
        shield = new PowerUpDB(1, loadImage("/res/poderes/shield.png"), loadImage("/res/poderes/shield.png"), 1, 1, "poder");

        for(int i = 0; i < exp.length; i++)
            exp[i] = loadImage("/res/explosion/"+i+".png");

        for(int i = 0; i < numbers.length; i++)
            numbers[i] = Loader.ImageLoader("/res/numeros/"+i+".png");

        fontBig = loadFont("/res/fuentes/futureFont.ttf",42);
        fontMed = loadFont("/res/fuentes/futureFont.ttf",20);
        for(int i = 0; i < 3; i++)
            shieldEffect[i] = loadImage("/res/efectos/shield" + (i + 1) +".png"); 

        db = new DatabaseHelper();
        try {

            db.conectar("invasoresDB.db");

            
            ResultSet rs = db.leerEpisodio();

            while (rs.next()) {
                File file = new File(System.getProperty("user.dir")+""+rs.getString("apertura"));
            
                episodios.add(new EpisodioDB(rs.getInt("idNivel"), rs.getString("nombre"), loadImage(rs.getString("fondo")), file, file, rs.getInt("estado")));

            }
            rs = db.leerMision(1);
            while (rs.next()) {
                misiones.add(new MisionDB(rs.getInt("idMision"), rs.getString("nombre"), rs.getString("musica"), rs.getInt("idNivel")));
                idSubXNiv = rs.getInt("idNivel");
            }

            rs = db.leerAliados();
            while (rs.next()) {
                int idP = rs.getInt("idProyectil");
                ResultSet pr = db.leerProyectil(idP);
                ProyectilDB p = new ProyectilDB(pr.getInt("idProyectil"), pr.getString("proyectil"), loadImage(pr.getString("imgProyectil")), pr.getInt("danio"));
                aliados.add(new NaveDB(rs.getInt("idNave"), rs.getString("tipoNave"), loadImage(rs.getString("perfil")), loadImage(rs.getString("imgNave")), p,  rs.getInt("danio"), rs.getInt("resistencia")));
                //System.out.println(rs.getString("tipoNave"));
            }

            rs = db.leerEnemigos();
            while (rs.next()) {
                int idP = rs.getInt("idProyectil");
                ResultSet pr = db.leerProyectil(idP);
                ProyectilDB p = new ProyectilDB(pr.getInt("idProyectil"), pr.getString("proyectil"), loadImage(pr.getString("imgProyectil")), pr.getInt("danio"));
                enemigos.add(new NaveDB(rs.getInt("idNave"), rs.getString("tipoNave"), loadImage(rs.getString("perfil")), loadImage(rs.getString("imgNave")), p, rs.getInt("danio"), rs.getInt("resistencia")));
            }

            rs = db.leerMeteoros(1);
            while (rs.next()) {
                for(int i = 0; i < 4; i++)
                    bigs[i] = new MeteoroDB(rs.getInt("idMeteoro"), loadImage(rs.getString("imagen")+"big"+(i+1)+".png"), loadImage(rs.getString("imagen")+"big"+(i+1)+".png"), rs.getInt("danio"), rs.getInt("resistencia"), "meteoro");

                for(int i = 0; i < 2; i++){
                    meds[i] = new MeteoroDB(rs.getInt("idMeteoro"), loadImage(rs.getString("imagen")+"med"+(i+1)+".png"), loadImage(rs.getString("imagen")+"med"+(i+1)+".png"), rs.getInt("danio"), rs.getInt("resistencia"), "meteoro");
                }

                for(int i = 0; i < 2; i++){
                    smalls[i] = new MeteoroDB(rs.getInt("idMeteoro"), loadImage(rs.getString("imagen")+"small"+(i+1)+".png"), loadImage(rs.getString("imagen")+"small"+(i+1)+".png"), rs.getInt("danio"), rs.getInt("resistencia"), "meteoro");
                }

                for(int i = 0; i < 2; i++){
                    tinies[i] = new MeteoroDB(rs.getInt("idMeteoro"), loadImage(rs.getString("imagen")+"tiny"+(i+1)+".png"), loadImage(rs.getString("imagen")+"tiny"+(i+1)+".png"), rs.getInt("danio"), rs.getInt("resistencia"), "meteoro");
                }
            } 
            db.cerrarConexion();
        }catch (SQLException e) {
            System.err.println("Error al interactuar con la base de datos: " + e.getMessage());
        }
        // ===========================================================
        loaded = true;
    }
    
    public static void setBlackHole(){
        if(blackHole == null){
            blackHole = loadImage("/res/agujeroNegro/blackHole20.png");
        }else{
            blackHole = null;
        }
    }

    public static void cargar(){
        count = 0;
        loaded = false;
    }

    public static void actualizar(int idNivel){

        misiones.clear();
        try {

            db.conectar("invasoresDB.db");
            ResultSet rs = db.leerMision(idNivel+1);
            while (rs.next()) {
                misiones.add(new MisionDB(rs.getInt("idMision"), rs.getString("nombre"), rs.getString("musica"), rs.getInt("idNivel")));
                idSubXNiv = rs.getInt("idNivel");
            }
            db.cerrarConexion();
        }catch (SQLException e) {
            System.err.println("Error al interactuar con la base de datos: " + e.getMessage());
        }
    }
    public static void actualizarCanvas(int idNivel){
    }

    public static BufferedImage loadImage(String path) {
        count ++;
        return Loader.ImageLoader(path); 
    } 

    public static Font loadFont(String path, int size) {
        count ++;
        return Loader.loadFont(path, size);
    }

    public static Clip loadSound(String path) {
        count ++;
        return Loader.loadSound(path);
    }
}
