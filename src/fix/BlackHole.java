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
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BlackHole {
    private final Vector2D position;
    private final BufferedImage texture;
    private final GameState gameState;

    private final int width, height;
    private final double radio, nucleo;

    private final Vector2D center;
    private double angle;

    public BlackHole(BufferedImage texture, GameState gameState) {
        this.texture = texture;
        this.gameState = gameState;
        this.position = new Vector2D(100, 100);
        this.width = texture.getWidth();
        this.height = texture.getHeight();
        this.radio = height / 2.0;
        this.nucleo = 80.0;
        this.center = new Vector2D(position.getX() + width / 2.0, position.getY() + height / 2.0);
        this.angle = 0;
    }

    public Vector2D getCenter() {
        return new Vector2D(center.getX(), center.getY()); // devolver copia para seguridad
    }

    public double getRadio() {
        return radio;
    }

    private boolean playerIsSpawning(MovingObject obj) {
        return (obj instanceof Player) && ((Player) obj).isSpawning();
    }

    public void update(float dt) {
        ArrayList<MovingObject> objects = gameState.getMovingObjects();

        for (int i = 0; i < objects.size(); i++) {
            MovingObject o = objects.get(i);
            Vector2D oCenter = o.getCenter();
            double dist = oCenter.subtract(center).getMagnitude();

            if (dist < nucleo || (dist < radio && o instanceof Laser)) {
                o.Destroy();
                if (!playerIsSpawning(o)) {
                    objects.remove(i);
                    i--;
                }
            } else if (dist < radio) {
                applyGravitationalPull(o, oCenter, dist, dt);
            }
        }

        angle -= 0.01;
    }

    private void applyGravitationalPull(MovingObject o, Vector2D oCenter, double distance, float dt) {
        final double attraction = 0.02;
        final double angularSpeed = -0.01;

        double angleActual = Math.atan2(oCenter.getY() - center.getY(), oCenter.getX() - center.getX());
        double newAngle = angleActual + angularSpeed * dt;

        double newDist = Math.max(0, distance - attraction * dt);
        double newX = center.getX() + newDist * Math.cos(newAngle);
        double newY = center.getY() + newDist * Math.sin(newAngle);

        o.blackHole(newX - o.width / 2.0, newY - o.height / 2.0);
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform at = AffineTransform.getTranslateInstance(position.getX(), position.getY());
        at.rotate(angle, width / 2.0, height / 2.0);
        g2d.drawImage(texture, at, null);
    }
}
