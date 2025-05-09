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

import io.NaveDB;
import io.Assets;
import io.ProyectilDB;

public class Ufo extends MovingObject {
    private final ProyectilDB proyectil;
    private final Sound shoot;

    private long fireRate;

    public Ufo(Vector2D position, Vector2D velocity, double maxVel, NaveDB textura, GameState gameState) {
        super(position, velocity, maxVel, textura, gameState);
        this.proyectil = textura.getProyectil();
        this.shoot = new Sound(Assets.ufoShoot);
        this.fireRate = 0;
    }

    @Override
    public void update(float dt) {
        fireRate += dt;

        if (!containsBlackHole() && !isDead()) {
            atacarPorFlancos(gameState.getPlayer(), dt);
            // formarDefensiva(dt); // Habilita si quieres
        }

        if (shoot.getFramePosition() > 8500) shoot.stop();
    }

    private void disparar(Vector2D direccion) {
        Laser laser = new Laser(
            getCenter().add(direccion.scale(width)),
            direccion,
            Constants.LASER_VEL,
            angle,
            proyectil,
            gameState
        );
        gameState.getMovingObjects().add(laser);
        shoot.play();
    }

    private void atacarPorFlancos(Player jugador, float dt) {
        Vector2D ufoCenter = getCenter();
        Vector2D jugadorCenter = jugador.getCenter();

        Vector2D jugadorANave = ufoCenter.subtract(jugadorCenter);
        Vector2D lateral = new Vector2D(-jugadorANave.getY(), jugadorANave.getX()).normalize();
        double distancia = jugadorANave.getMagnitude();

        Vector2D direccionDeseada;
        if (distancia < 100) {
            direccionDeseada = jugadorANave.add(lateral).normalize();
        } else if (distancia > 150) {
            direccionDeseada = jugadorANave.scale(-1).add(lateral).normalize();
        } else {
            direccionDeseada = lateral;
        }

        velocity = direccionDeseada.scale(maxVel * 0.05f);
        position = position.add(velocity.scale(dt));

        apuntarAJugador(jugadorCenter);

        if (fireRate > Constants.UFO_FIRE_RATE && distancia < 350) {
            disparar(jugadorCenter.subtract(ufoCenter).normalize());
            fireRate = 0;
        }
    }

    private void apuntarAJugador(Vector2D jugadorCenter) {
        Vector2D direccion = jugadorCenter.subtract(getCenter()).normalize();
        angle = direccion.getAngle();
        if (direccion.getX() < 0) angle = -angle + Math.PI;
        angle += Math.PI / 2;
    }

    private void formarDefensiva(float dt) {
        Vector2D centroJuego = new Vector2D(Constants.WIDTH / 2.0, Constants.HEIGHT / 2.0);
        Vector2D ufoCenter = getCenter();
        Vector2D direccionBorde = ufoCenter.subtract(centroJuego).normalize();
        Vector2D posicionObjetivo = centroJuego.add(direccionBorde.scale(Constants.WIDTH / 2.0 - 100));

        Vector2D haciaPosicion = posicionObjetivo.subtract(ufoCenter);
        double distancia = haciaPosicion.getMagnitude();

        if (distancia > 50) {
            velocity = haciaPosicion.normalize().scale(maxVel * 0.05f);
        } else {
            velocity = new Vector2D(-direccionBorde.getY(), direccionBorde.getX()).scale(maxVel * 0.3f);
        }

        position = position.add(velocity.scale(dt));

        if (fireRate > Constants.UFO_FIRE_RATE * 1.2) {
            disparar(centroJuego.subtract(ufoCenter).normalize());
            fireRate = 0;
        }
    }

    @Override
    public void Destroy(int da) {
        resistencia -= da;
        if (resistencia < 1) {
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
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform at = AffineTransform.getTranslateInstance(position.getX(), position.getY());
        at.rotate(angle, width / 2.0, height / 2.0);
        g2d.drawImage(texture, at, null);
    }
}
