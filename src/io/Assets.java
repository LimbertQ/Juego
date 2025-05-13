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
    public static final float MAX_COUNT = 58;

    public static BufferedImage fondo, speed, blackHole, pulsar;
    public static BufferedImage[] shieldEffect = new BufferedImage[3];
    public static BufferedImage[] exp = new BufferedImage[9];
    public static BufferedImage[] numbers = new BufferedImage[11];

    public static MeteoroDB[] bigs = new MeteoroDB[4];
    public static MeteoroDB[] meds = new MeteoroDB[2];
    public static MeteoroDB[] smalls = new MeteoroDB[2];
    public static MeteoroDB[] tinies = new MeteoroDB[2];

    public static ArrayList<NaveDB> enemigos = new ArrayList<>();
    public static ArrayList<NaveDB> aliados = new ArrayList<>();

    public static Font fontBig, fontMed;

    public static Clip backgroundMusic, explosion, playerLoose, playerShoot, ufoShoot, powerUp, ufoSound;

    public static PowerUpDB orb, doubleScore, doubleGun, fastFire, shield, star, life;

    public static ArrayList<EpisodioDB> episodios = new ArrayList<>();
    public static ArrayList<MisionDB> misiones = new ArrayList<>();
    public static int idSubXNiv = -1;

    private static DatabaseHelper db;

    public static void inicia() {
        fondo = loadImage("/res/fondos/fondo2.jpeg");
        speed = loadImage("/res/efectos/fire08.png");
        pulsar = loadImage("/res/agujeroNegro/pulsarrr.png");
        blackHole = loadImage("/res/agujeroNegro/blackHole20.png");
        backgroundMusic = loadSound("/res/sonidos/fondoMu.wav");
        explosion = loadSound("/res/sonidos/explosion.wav");
        playerLoose = loadSound("/res/sonidos/playerLoose.wav");
        playerShoot = loadSound("/res/sonidos/playerShoot.wav");
        ufoShoot = loadSound("/res/sonidos/ufoShoot.wav");
        powerUp = loadSound("/res/sonidos/powerUp.wav");
        ufoSound = loadSound("/res/sonidos/ufoSpawner1.wav");

        for (int i = 0; i < exp.length; i++)
            exp[i] = loadImage("/res/explosion/" + i + ".png");

        for (int i = 0; i < numbers.length; i++)
            numbers[i] = Loader.ImageLoader("/res/numeros/" + i + ".png");

        for (int i = 0; i < shieldEffect.length; i++)
            shieldEffect[i] = loadImage("/res/efectos/shield" + (i + 1) + ".png");

        fontBig = loadFont("/res/fuentes/futureFont.ttf", 42);
        fontMed = loadFont("/res/fuentes/futureFont.ttf", 20);

        life = new PowerUpDB(1, loadImage("/res/otros/life.png"), loadImage("/res/otros/life.png"), 1, 1, "poder");
        orb = createPowerUp("orb");
        doubleScore = createPowerUp("doubleScore");
        doubleGun = createPowerUp("doubleGun");
        fastFire = createPowerUp("fastFire");
        star = createPowerUp("star");
        shield = createPowerUp("shield");

        db = new DatabaseHelper();
        try {
            db.conectar("invasoresDB.db");

            ResultSet rs = db.leerEpisodio();
            while (rs.next()) {
                File f = new File(System.getProperty("user.dir") + rs.getString("apertura"));
                episodios.add(new EpisodioDB(rs.getInt("idNivel"), rs.getString("nombre"), loadImage(rs.getString("fondo")), f, f, rs.getInt("estado")));
            }

            cargarMisiones(1);
            cargarNaves(true);
            cargarNaves(false);
            cargarMeteoros(1);

            db.cerrarConexion();
        } catch (SQLException e) {
            System.err.println("Error al interactuar con la base de datos: " + e.getMessage());
        }
        loaded = true;
    }

    private static PowerUpDB createPowerUp(String name) {
        BufferedImage img = loadImage("/res/poderes/" + name + ".png");
        return new PowerUpDB(1, img, img, 1, 1, "poder");
    }

    private static void cargarMisiones(int nivel) throws SQLException {
        ResultSet rs = db.leerMision(nivel);
        while (rs.next()) {
            misiones.add(new MisionDB(rs.getInt("idMision"), rs.getString("nombre"), rs.getString("musica"), rs.getInt("idNivel")));
            idSubXNiv = rs.getInt("idNivel");
        }
    }

    private static void cargarNaves(boolean aliadosFlag) throws SQLException {
        ResultSet rs = aliadosFlag ? db.leerAliados() : db.leerEnemigos();
        while (rs.next()) {
            int idP = rs.getInt("idProyectil");
            ResultSet pr = db.leerProyectil(idP);
            ProyectilDB p = new ProyectilDB(pr.getInt("idProyectil"), pr.getString("proyectil"), loadImage(pr.getString("imgProyectil")), pr.getInt("danio"));
            NaveDB nave = new NaveDB(
                rs.getInt("idNave"),
                rs.getString("tipoNave"),
                loadImage(rs.getString("perfil")),
                loadImage(rs.getString("imgNave")),
                p,
                rs.getInt("danio"),
                rs.getInt("resistencia")
            );
            if (aliadosFlag) aliados.add(nave); else enemigos.add(nave);
        }
    }

    private static void cargarMeteoros(int tipo) throws SQLException {
        ResultSet rs = db.leerMeteoros(tipo);
        while (rs.next()) {
            for (int i = 0; i < 4; i++)
                bigs[i] = new MeteoroDB(rs.getInt("idMeteoro"), loadImage(rs.getString("imagen") + "big" + (i + 1) + ".png"), loadImage(rs.getString("imagen") + "big" + (i + 1) + ".png"), rs.getInt("danio"), rs.getInt("resistencia"), "meteoro");
            for (int i = 0; i < 2; i++) {
                meds[i] = new MeteoroDB(rs.getInt("idMeteoro"), loadImage(rs.getString("imagen") + "med" + (i + 1) + ".png"), loadImage(rs.getString("imagen") + "med" + (i + 1) + ".png"), rs.getInt("danio"), rs.getInt("resistencia"), "meteoro");
                smalls[i] = new MeteoroDB(rs.getInt("idMeteoro"), loadImage(rs.getString("imagen") + "small" + (i + 1) + ".png"), loadImage(rs.getString("imagen") + "small" + (i + 1) + ".png"), rs.getInt("danio"), rs.getInt("resistencia"), "meteoro");
                tinies[i] = new MeteoroDB(rs.getInt("idMeteoro"), loadImage(rs.getString("imagen") + "tiny" + (i + 1) + ".png"), loadImage(rs.getString("imagen") + "tiny" + (i + 1) + ".png"), rs.getInt("danio"), rs.getInt("resistencia"), "meteoro");
            }
        }
    }

    public static void setBlackHole() {
        blackHole = (blackHole == null) ? loadImage("/res/agujeroNegro/blackHole20.png") : null;
    }

    public static void cargar() {
        count = 0;
        loaded = false;
    }

    public static void actualizar(int idNivel) {
        misiones.clear();
        try {
            db.conectar("invasoresDB.db");
            cargarMisiones(idNivel + 1);
            db.cerrarConexion();
        } catch (SQLException e) {
            System.err.println("Error al interactuar con la base de datos: " + e.getMessage());
        }
    }

    public static void actualizarCanvas(int idNivel) {
        // Método pendiente o no utilizado
    }

    public static BufferedImage loadImage(String path) {
        count++;
        return Loader.ImageLoader(path);
    }

    public static Font loadFont(String path, int size) {
        count++;
        return Loader.loadFont(path, size);
    }

    public static Clip loadSound(String path) {
        count++;
        return Loader.loadSound(path);
    }
}
