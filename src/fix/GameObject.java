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
import java.awt.image.BufferedImage;
import io.ObjetoDB;

public abstract class GameObject {
    protected BufferedImage texture;
    protected Vector2D position;

    public GameObject(Vector2D position, ObjetoDB texture)
    {
        this.position = position;
        this.texture = texture.getImagen();
    }

    public abstract void update(float dt);

    public abstract void draw(Graphics g);

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

}