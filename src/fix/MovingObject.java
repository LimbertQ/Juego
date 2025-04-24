/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fix;

/**
 *
 * @author pc
 */
import java.awt.geom.AffineTransform;

import io.ObjetoDB;
import io.Assets;

public abstract class MovingObject extends GameObject{

    protected Vector2D velocity;
    protected AffineTransform at;
    protected double angle;
    protected double maxVel;
    protected int width;
    protected int height;
    protected GameState gameState;

    private Sound explosion;

    protected boolean Dead;
    
    protected int danio;
    protected int resistencia;
    protected boolean containsBlackHole = false;
    public MovingObject(Vector2D position, Vector2D velocity, double maxVel, ObjetoDB texture, GameState gameState) {
        super(position, texture);
        this.velocity = velocity;
        this.maxVel = maxVel;
        this.gameState = gameState;
        width = texture.getImagen().getWidth();
        height = texture.getImagen().getHeight();
        angle = 0;
        explosion = new Sound(Assets.explosion);
        Dead = false;
        danio = texture.getDanio();
        resistencia = texture.resistencia();
    }
    
    public boolean containsBlackHole(){
        return containsBlackHole;
    } 
    
    public void blackHole(double x, double y){
        containsBlackHole = true;
        position.setX(x);
        position.setY(y);
    }
    
    protected void Destroy(int danio){
        Dead = true;
        if(!(this instanceof Laser) && !(this instanceof PowerUp))
            explosion.play();
    }

    protected void Destroy(){
        Dead = true;
        if(!(this instanceof Laser) && !(this instanceof PowerUp))
            explosion.play();
    }

    protected Vector2D getCenter(){
        return new Vector2D(position.getX() + width/2, position.getY() + height/2);
    }

    public boolean isDead() {return Dead;}

}
