/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fix;

/**
 *
 * @author pc
 */
import java.awt.image.BufferedImage;

import io.Assets;
import io.PowerUpDB;
public enum PowerUpTypes {
    SHIELD("SHIELD", Assets.shield),
    LIFE("+1 LIFE", Assets.life),
    SCORE_X2("SCORE x2", Assets.doubleScore),
    FASTER_FIRE("FAST FIRE", Assets.fastFire),
    SCORE_STACK("+1000 SCORE", Assets.star),
    DOUBLE_GUN("DOUBLE GUN", Assets.doubleGun);

    public String text;
    public PowerUpDB texture;

    private PowerUpTypes(String text, PowerUpDB texture){
        this.text = text;
        this.texture = texture;
    }
}