/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fix;

/**
 *
 * @author pc
 */
import io.ObjetoDB;
import io.Assets;
public abstract class MovingObject extends GameObject {

    //protected AffineTransform at;
    protected Vector2D velocity;
    protected double angle;
    protected double maxVel;
    protected int width;
    protected int height;
    protected GameState gameState;

    protected boolean dead = false;
    protected boolean containsBlackHole = false;

    protected int danio;
    protected int resistencia;

    private final Sound explosion;

    public MovingObject(Vector2D position, Vector2D velocity, double maxVel, ObjetoDB texture, GameState gameState) {
        super(position, texture);
        this.velocity = velocity;
        this.maxVel = maxVel;
        this.gameState = gameState;

        this.width = texture.getImagen().getWidth();
        this.height = texture.getImagen().getHeight();
        this.angle = 0;

        this.explosion = new Sound(Assets.explosion);
        this.danio = texture.getDanio();
        this.resistencia = texture.resistencia();
    }

    public boolean isDead() {
        return dead;
    }

    public boolean containsBlackHole() {
        return containsBlackHole;
    }

    public void blackHole(double x, double y) {
        containsBlackHole = true;
        position.setX(x);
        position.setY(y);
    }

    protected void Destroy(int danio) {
        destroyInternal();
    }

    protected void Destroy() {
        destroyInternal();
    }

    private void destroyInternal() {
        dead = true;
        if (!(this instanceof Laser) && !(this instanceof PowerUp)) {
            explosion.play();
        }
    }

    protected Vector2D getCenter() {
        return new Vector2D(position.getX() + width / 2.0, position.getY() + height / 2.0);
    }
}
