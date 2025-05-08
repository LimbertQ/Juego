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
import io.MeteoroDB;

public class Meteor extends MovingObject {
    private Size size;

    public Meteor(Vector2D position, Vector2D velocity, double maxVel, MeteoroDB texture, GameState gameState, Size size) {
        super(position, velocity, maxVel, texture, gameState);
        this.size = size;
        this.velocity = velocity.scale(maxVel);
    }

    @Override
    public void update(float dt) {
        if (!containsBlackHole()) {

            Vector2D center = getCenter();
            Vector2D playerCenter = gameState.getPlayer().getCenter();
            double distanceToPlayer = playerCenter.subtract(center).getMagnitude();

            // Comprobar si está dentro del rango del escudo
            double shieldRadius = Constants.SHIELD_DISTANCE * 0.5 + width * 0.5;
            if (distanceToPlayer < shieldRadius) {
                if (gameState.getPlayer().isShieldOn()) {
                    Vector2D fleeForce = fleeForce(center, playerCenter);
                    velocity = velocity.add(fleeForce);
                }
            }

            // Limitar la velocidad si excede la máxima
            if (velocity.getMagnitudeSq() >= maxVel * maxVel) {
                velocity = velocity.add(velocity.normalize().scale(-0.01f)); // ligera desaceleración
            }

            velocity = velocity.limit(Constants.METEOR_MAX_VEL);
            position = position.add(velocity);
        }

        angle += Constants.DELTAANGLE / 2;
    }

    private Vector2D fleeForce(Vector2D meteorCenter, Vector2D playerCenter) {
        Vector2D desiredVelocity = playerCenter.subtract(meteorCenter).normalize().scale(Constants.METEOR_MAX_VEL);
        return velocity.subtract(desiredVelocity);
    }

    // Sobrecarga para compatibilidad si no se pasan parámetros
    private Vector2D fleeForce() {
        return fleeForce(getCenter(), gameState.getPlayer().getCenter());
    }

    @Override
    public void Destroy(int da) {
        resistencia -= da;
        if (resistencia <= 0) {
            gameState.divideMeteor(this);
            gameState.playExplosion(position);
            gameState.addScore(Constants.METEOR_SCORE, position);
            super.Destroy();
        }
    }

    @Override
    public void Destroy() {
        gameState.divideMeteor(this);
        gameState.playExplosion(position);
        gameState.addScore(Constants.METEOR_SCORE, position);
        super.Destroy();
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform at = AffineTransform.getTranslateInstance(position.getX(), position.getY());
        at.rotate(angle, width / 2.0, height / 2.0);
        g2d.drawImage(texture, at, null);
    }

    public Size getSize() {
        return size;
    }
}