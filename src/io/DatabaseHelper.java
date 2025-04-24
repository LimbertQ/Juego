/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io;

/**
 *
 * @author pc
 */
import java.sql.*;

public class DatabaseHelper {

    private static Connection conn;

    public static void conectar(String dbFilePath) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:"+ dbFilePath);
    }
    
    public static void crearTablaNivel() throws SQLException {
        String nivel = "CREATE TABLE IF NOT EXISTS nivel (idNivel INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, fondo TEXT NOT NULL, apertura TEXT NOT NULL, cierre TEXT NOT NULL, estado INTEGER NOT NULL)";
        String mision = "CREATE TABLE IF NOT EXISTS mision (idMision INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, musica TEXT NOT NULL, idNivel INTEGER NOT NULL, FOREIGN KEY (idNivel) REFERENCES nivel(idNivel))";
        String meteoro = "CREATE TABLE IF NOT EXISTS meteoro(idMeteoro INTEGER PRIMARY KEY AUTOINCREMENT, imagen TEXT NOT NULL, resistencia INTEGER NOT NULL)";
        String meteoro_has_mision = "CREATE TABLE IF NOT EXISTS meteoro_has_mision (idMision INTEGER, idMeteoro INTEGER, PRIMARY KEY (idMision, idMeteoro), FOREIGN KEY (idMision) REFERENCES mision(idMision), FOREIGN KEY (idMeteoro) REFERENCES meteoro(idMeteoro))";
        String proyectil = "CREATE TABLE IF NOT EXISTS proyectil (idProyectil INTEGER PRIMARY KEY AUTOINCREMENT, proyectil TEXT NOT NULL, imgProyectil TEXT NOT NULL, danio INTEGER NOT NULL)";
        String civilizacion = "CREATE TABLE IF NOT EXISTS civilizacion (idCivilizacion INTEGER PRIMARY KEY AUTOINCREMENT, civilizacion TEXT NOT NULL)";
        String nave = "CREATE TABLE IF NOT EXISTS nave (idNave INTEGER PRIMARY KEY AUTOINCREMENT, tipoNave TEXT NOT NULL, imgNave TEXT NOT NULL, perfil TEXT NOT NULL, estado INTEGER NOT NULL, jugador INTEGER NOT NULL, idProyectil INTEGER NOT NULL, idCivilizacion INTEGER NOT NULL, FOREIGN KEY (idProyectil) REFERENCES proyectil(idProyectil), FOREIGN KEY (idCivilizacion) REFERENCES civilizacion(idCivilizacion))";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(nivel);
            stmt.execute(mision);
            stmt.execute(meteoro);
            stmt.execute(meteoro_has_mision);
            stmt.execute(proyectil);
            stmt.execute(civilizacion);
            stmt.execute(nave);
        }
    } 


    public static void insertarDatos(String nombre, String imagenfondo) throws SQLException {
        String nivel = "INSERT INTO nivel (nombre, fondo, apertura, cierre, estado) VALUES ('La guerra en el espacio', '/res/fondos/fondo2.jpeg', '/res/videos/historia1.mp4', '/res/videos/historia1.mp4',  1),('La doble alianza', '/res/fondos/pista1.jpeg', '/res/videos/historia2.mp4', '/res/videos/historia2.mp4', 0),('Invasion de Nibiru', '/res/fondos/fondo2.jpeg', '/res/videos/historia1.mp4', '/res/videos/historia1.mp4', 0),('Las ofensivas finales', '/res/fondos/pista1.jpeg', '/res/videos/historia2.mp4', '/res/videos/historia2.mp4', 0)";        
        String mision1 = "INSERT INTO mision (nombre, musica, idNivel) VALUES('Los cazadores', 'musica_mision1_1.mp3', 1),('Operacion dark space', 'musica_mision1_2.mp3', 1),('Artilleria enemiga', 'musica_mision1_3.mp3', 1),('Replegando al enemigo', 'musica_mision1_4.mp3', 1)";
        String mision2 = "INSERT INTO mision (nombre, musica, idNivel) VALUES('Mas alla del sol', 'musica_mision2_1.mp3', 2),('La trinchera', 'musica_mision2_2.mp3', 2),('Delitos en coma', 'musica_mision2_3.mp3', 2),('Viendo la marea', 'musica_mision2_4.mp3', 2)";
        String proyectil = "INSERT INTO proyectil (proyectil, imgProyectil, danio) VALUES ('laser verde', '/res/proyectil/laserGreen01.png', 1),('laser azul', '/res/proyectil/laserBlue01.png', 5),('laser rojo', '/res/proyectil/laserRed01.png', 10)";
        String civilizacion = "INSERT INTO civilizacion (civilizacion) VALUES ('humanos'), ('anunakis'), ('reptiliano')";
        String nave = "INSERT INTO nave (tipoNave, imgNave, perfil, estado, jugador, idProyectil, idCivilizacion) VALUES ('caza 1', '/res/naves/player.png', '/res/naves/player.png', 1, 1, 1, 1),('ovni 1', '/res/naves/ufo.png', '/res/naves/ufo.png', 1, 0, 2, 2)";
        
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(nivel);
            stmt.execute(mision1);
            stmt.execute(mision2);
            stmt.execute(proyectil);
            stmt.execute(civilizacion);
            stmt.execute(nave);
        }
    }

    public static void modificarDatos(int id, String nombre, String imagenfondo) throws SQLException {
        String sql = "UPDATE nivel SET nombre = ?, imagenfondo = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, imagenfondo);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        }
    }

    public static ResultSet leerEpisodio() throws SQLException {
        String sql = "SELECT idNivel, nombre, fondo, apertura, cierre, estado FROM nivel";
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }
    
    public static ResultSet leerMision(int idNivel) throws SQLException {
        String sql = "SELECT * FROM mision INNER JOIN nivel ON mision.idNivel = nivel.idNivel WHERE nivel.idNivel = "+idNivel;
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }
    
    public static ResultSet leerProyectil(int id) throws SQLException {
        String sql = "select * from proyectil p where p.idProyectil = "+id;
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }
    
    public static ResultSet leerNave() throws SQLException {
        String sql = "SELECT idNave, tipoNave, imgNave, perfil, estado, jugador, idProyectil, idCivilizacion, danio, resistencia FROM nave";
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }
    
    public static ResultSet leerAliados() throws SQLException {
        String sql = "select * from nave n  where n.jugador = 1 and n.estado = 1";
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }
    
    public static ResultSet leerEnemigos() throws SQLException {
        String sql = "select * from nave n  where n.jugador = 0";
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }
    
    public static ResultSet leerCivilizacion() throws SQLException {
        String sql = "SELECT idCivilizacion, civilizacion FROM civilizacion";
        Statement stmt = conn.createStatement();
        //System.out.println("hola mola");
        return stmt.executeQuery(sql);
    }
    
    public static ResultSet leerMeteoros(int idMision) throws SQLException {
        String sql = "SELECT * FROM meteoro m JOIN mision_has_meteoro mhm on m.idMeteoro = mhm.idMeteoro  WHERE mhm.idMision = "+idMision;
        Statement stmt = conn.createStatement();
        //System.out.println("hola mola");
        return stmt.executeQuery(sql);
    }

    public static void cerrarConexion() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }
}
