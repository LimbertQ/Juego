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
import io.MeteoroDB;
public enum Size {

    BIG(2, Assets.meds), MED(2, Assets.smalls), SMALL(2, Assets.tinies), TINY(0, null);

    public int quantity;

    public MeteoroDB[] textures;

    private Size(int quantity, MeteoroDB[] textures){
        this.quantity = quantity;
        this.textures = textures;
    }

}
