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

public class Pulsar{
    private final Vector2D position;
    private final BufferedImage texture;
    private final GameState gameState;

    private final int width, height;
    private final double radio;

    private final Vector2D center;
    private double angle;
    private double rotate;

    public Pulsar(BufferedImage texture,GameState gameState) {
        this.texture = texture;
        this.gameState = gameState;
        this.position = new Vector2D(600, 100);
        this.width = texture.getWidth();
        this.height = texture.getHeight();
        this.radio = height / 2.0;
        this.center = new Vector2D(position.getX() + width / 2.0, position.getY() + height / 2.0);
        this.angle = 0;
        this.rotate = 0.1;
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
        
        angle += rotate;
        if(Math.abs(angle) > 5){
            rotate *= -1;
            applyOnda();
        }
    }

    private void applyOnda(){
        ArrayList<MovingObject> objects = gameState.getMovingObjects();
        for(int i = 0; i < objects.size(); i++){
            MovingObject o = objects.get(i);
            Vector2D oCenter = o.getCenter();
            double dist = oCenter.subtract(center).getMagnitude();

            if (dist < radio) {
                o.Destroy();
                if (!playerIsSpawning(o)) {
                    objects.remove(i);
                    i--;
                }
            }
        }
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform at = AffineTransform.getTranslateInstance(position.getX(), position.getY());
        at.rotate(angle, width / 2.0, height / 2.0);
        g2d.drawImage(texture, at, null);
    }
}

