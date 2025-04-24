/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fix;

/**
 *
 * @author pc
 */
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import io.NaveDB;
import io.Assets;
import io.ProyectilDB;
public class Ufo extends MovingObject{
    private final ProyectilDB proyectil;

    private long fireRate;
    private int totalLaserCount = 0;

    private Sound shoot;

    public Ufo(Vector2D position, Vector2D velocity, double maxVel, NaveDB textura,
           GameState gameState) {
        super(position, velocity, maxVel, textura, gameState);
        fireRate = 0;
        shoot = new Sound(Assets.ufoShoot);

        proyectil = textura.getProyectil();
    }

    

    @Override
    public void update(float dt) {

        fireRate += dt;
        
        if(!containsBlackHole() && !isDead()){
            //atacarFrontal(gameState.getPlayer(), dt);
            atacarPorFlancos(gameState.getPlayer(), dt);
            //formarDefensiva(dt);
            }

            if(shoot.getFramePosition() > 8500) {
                shoot.stop();
            }
            //angle += 0.05;
        //}
    }
    
    private void disparar(Vector2D direccion) {
        //System.out.println("disparo");
        /*
        double currentAngle = direccion.getAngle();
        
        if(direccion.getX() < 0)
           currentAngle = -currentAngle + Math.PI;*/
        Laser laser = new Laser(
            getCenter().add(direccion.scale(width)),
            direccion,
            Constants.LASER_VEL,
            angle,
            proyectil,
            gameState
        );
            
        gameState.getMovingObjects().add(laser);    
    }
    
    /**
     * Implementación de táctica: ataque directo hacia el jugador
     */
    private void atacarFrontal(Player jugador, float dt) {
        // Calcular dirección hacia el jugador
        Vector2D direccionJugador = jugador.getCenter().subtract(getCenter()).normalize();
        
        // Ajustar velocidad hacia el jugador
        velocity = direccionJugador.scale(maxVel * 0.05f);
        
        // Actualizar posición
        position = position.add(velocity.scale(dt));
        
        // Apuntar hacia el jugador (ajustar ángulo)
        angle = velocity.getAngle();
        if(velocity.getX() < 0)
           angle = -angle+Math.PI;
        angle+=Math.PI/2;
        // Disparar si estamos apuntando al jugador y ha pasado suficiente tiempo
        if (fireRate > Constants.UFO_FIRE_RATE * 0.8) {
            disparar(direccionJugador);
            fireRate = 0;
        }
    }
    
    /**
     * Implementación de táctica: atacar desde los laterales del jugador
     */
    private void atacarPorFlancos(Player jugador, float dt) {
        // Vector desde jugador a nave
        Vector2D jugadorANave = getCenter().subtract(jugador.getCenter());
        
        // Vector perpendicular (lateral) al jugador
        Vector2D lateral = new Vector2D(-jugadorANave.getY(), jugadorANave.getX()).normalize();
        
        // Si estamos demasiado cerca, alejarnos
        double distancia = jugadorANave.getMagnitude();
        Vector2D direccionDeseada;
        
        
        if (distancia < 100) {
            // Alejarse un poco
            direccionDeseada = jugadorANave.add(lateral).normalize();
        } else if (distancia > 150) {
            // Acercarse desde el lateral
            direccionDeseada = jugadorANave.scale(-1).add(lateral).normalize();
        } else {
            // Mantener posición lateral
            direccionDeseada = lateral;
        }
        
        // Actualizar velocidad y posición
        velocity = direccionDeseada.scale(maxVel * 0.05f);
        position = position.add(velocity.scale(dt));
        
        // Apuntar hacia el jugador (ajustar ángulo)
        Vector2D an = jugador.getCenter().subtract(getCenter()).normalize().scale(maxVel * 0.05f);;
        angle = an.getAngle();
        if(an.getX() < 0)
           angle = -angle+Math.PI;
        angle+=Math.PI/2;
        
        // Disparar cuando tengamos línea de visión clara
        if (fireRate > Constants.UFO_FIRE_RATE && distancia < 350) {
            
            disparar(jugador.getCenter().subtract(getCenter()).normalize());
            fireRate = 0;
        }
    }
    
    /**
     * Implementación de táctica: mantener distancia y posición defensiva
     */
    private void formarDefensiva(float dt) {
        // Buscar posición segura (lejos del jugador, cerca del borde)
        Vector2D centroJuego = new Vector2D(Constants.WIDTH/2, Constants.HEIGHT/2);
        Vector2D direccionBorde = getCenter().subtract(centroJuego).normalize();
        
        // Determinar distancia óptima del borde
        float distanciaBorde = 100;
        Vector2D posicionObjetivo = centroJuego.add(direccionBorde.scale(Constants.WIDTH/2 - distanciaBorde));
        
        // Vector hacia la posición objetivo
        Vector2D haciaPosicion = posicionObjetivo.subtract(getCenter());
        double distancia = haciaPosicion.getMagnitude();
        
        // Si estamos lejos de la posición objetivo, movernos hacia ella
        if (distancia > 50) {
            velocity = haciaPosicion.normalize().scale(maxVel * 0.05f);
        } else {
            // De lo contrario, movernos en círculos
            velocity = new Vector2D(
                -direccionBorde.getY(),
                direccionBorde.getX()
            ).scale(maxVel * 0.3f);
        }
        
        // Actualizar posición
        position = position.add(velocity.scale(dt));
        
        // Disparar ocasionalmente hacia el centro del juego
        if (fireRate > Constants.UFO_FIRE_RATE * 1.2) {
            Vector2D direccionDisparo = centroJuego.subtract(getCenter()).normalize();
            disparar(direccionDisparo);
            fireRate = 0;
        }
    }

    @Override
    public void Destroy(int da) {
        resistencia -= da;
        if(resistencia < 1){
            gameState.addScore(Constants.UFO_SCORE, position);
            gameState.playExplosion(position);
            super.Destroy();
        }
    }

    @Override
    public void Destroy() {
        gameState.addScore(Constants.UFO_SCORE, position);
        gameState.playExplosion(position);
        super.Destroy();
    }

    @Override
    public void draw(Graphics g) {

        Graphics2D g2d = (Graphics2D)g;

        at = AffineTransform.getTranslateInstance(position.getX(), position.getY());

        at.rotate(angle, width/2, height/2);

        g2d.drawImage(texture, at, null);

    }

}
