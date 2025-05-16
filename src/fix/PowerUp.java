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

import io.PowerUpDB;
import io.Assets;
public class PowerUp extends MovingObject {

    private long duration;
    private Action action;
    private Sound powerUpPick;
    private BufferedImage typeTexture;

    public PowerUp(Vector2D position, PowerUpDB texture, Action action, GameState gameState) {
        super(position, new Vector2D(), 0, Assets.orb, gameState);

        this.action = action;
        this.typeTexture = texture.getImagen();
        duration = 0;
        powerUpPick = new Sound(Assets.powerUp);

    }

    void executeAction() {
        action.doAction();
        powerUpPick.play();
    }

    @Override
    public void update(float dt) {
        angle += 0.1;
        duration += dt;

        if(duration > Constants.POWER_UP_DURATION) {
            this.Destroy();
        }

        //hola mundo

    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        AffineTransform at = AffineTransform.getTranslateInstance(
            position.getX() + Assets.orb.getImagen().getWidth()/2 - typeTexture.getWidth()/2,
            position.getY() + Assets.orb.getImagen().getHeight()/2 - typeTexture.getHeight()/2);

        at.rotate(angle,
            typeTexture.getWidth()/2,
            typeTexture.getHeight()/2);

        g.drawImage(Assets.orb.getImagen(), (int)position.getX(), (int)position.getY(), null);

        
        g2d.drawImage(typeTexture, at, null);
    }

}
