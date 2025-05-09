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

import io.Assets;
import io.ProyectilDB;

public class Laser extends MovingObject{
    private Vector2D heading;
    public Laser(Vector2D position, Vector2D velocity, double maxVel, double angle, ProyectilDB texture, GameState gameState) {
        super(position, velocity, maxVel, texture, gameState);
        this.angle = angle;
        this.velocity = velocity.scale(maxVel);
        heading = new Vector2D(0, 1);
    }

    @Override
    public void update(float dt) {
        if(!containsBlackHole()){
            position = position.add(velocity);
            /*
            if(position.getX() < 0 || position.getX() > Constants.WIDTH ||
            position.getY() < 0 || position.getY() > Constants.HEIGHT){
                Destroy();
            }*/
        }

        //collidesWith();

    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;

        AffineTransform at = AffineTransform.getTranslateInstance(position.getX() - width/2, position.getY());

        at.rotate(angle, width/2, 0);

        g2d.drawImage(texture, at, null);

    }

    @Override
    public Vector2D getCenter(){
        return new Vector2D(position.getX() + width/2, position.getY() + width/2);
    }

}
