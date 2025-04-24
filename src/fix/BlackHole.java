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
import io.Assets;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.util.ArrayList;
public class BlackHole{
    private Vector2D position;
    private BufferedImage texture;
    private GameState gameState;
    private double angle;
    protected int width;
    protected int height;
    protected double radio;
    protected double nucleo;
    protected AffineTransform at;
    protected Vector2D center;
    private long absor;
    //private ArrayList<MovingObject> objectsToRemove = new ArrayList<>();
    public BlackHole(BufferedImage texture, GameState gameState){
        position = new Vector2D(100,100);
        angle = 0;
        this.texture = texture;
        width = texture.getWidth();
        height = texture.getHeight();
        radio = height/2;
        nucleo = 80;
        absor = 0;
        this.gameState = gameState;
        center = new Vector2D(position.getX() + width/2, position.getY() + height/2);
    }

    public Vector2D getCenter(){
        return new Vector2D(position.getX() + width/2, position.getY() + height/2);
    }

    public double getRadio(){
        return radio;
    }
    
    private boolean playerIsSpawning(MovingObject a){
        boolean spawning = false;
        if((a instanceof Player) && ((Player) a).isSpawning()){
                spawning = true;
        }
        return spawning;
    }

    public void update(float dt) {
        
            //objectsToRemove.clear();
            ArrayList<MovingObject> movingObjects = gameState.getMovingObjects();
            for (int i = 0; i < movingObjects.size(); i++) {
                MovingObject o = movingObjects.get(i);
                Vector2D oCenter = o.getCenter();
                double distanceToNode = oCenter.subtract(center).getMagnitude();

                if (distanceToNode < nucleo || (distanceToNode < radio && o instanceof Laser)) {
                    o.Destroy();
                    if(!playerIsSpawning(o)){
                        movingObjects.remove(i);
                        i--;
                    }
                } else if (distanceToNode < radio) {
                    
                        double fuerzaAtraccion = 0.01; // Atracción extremadamente lenta
                        double velocidadAngular = -0.01;  // Rotación rápida
                        
                        double anguloActual = Math.atan2(oCenter.getY() - center.getY(), oCenter.getX() - center.getX());
                        double nuevoAngulo = anguloActual + velocidadAngular * dt;

                        double nuevaDistancia = distanceToNode - fuerzaAtraccion * dt;
                        if (nuevaDistancia < 0) nuevaDistancia = 0;

                        double nuevoX = center.getX() + nuevaDistancia * Math.cos(nuevoAngulo);
                        double nuevoY = center.getY() + nuevaDistancia * Math.sin(nuevoAngulo);

                        //o.getPosition().set(nuevoX - o.width / 2, nuevoY - o.height / 2);
                        o.blackHole(nuevoX - o.width / 2,nuevoY - o.height / 2);
                        //o.setContainsBlackHole(true); // Marcar AL MENOS UNA VEZ que entra al radio
                }

            }
            
        
        angle -= 0.01;
        //return objectsToRemove;
    }

    public void draw(Graphics g){
        Graphics2D g2d = (Graphics2D)g;

        at = AffineTransform.getTranslateInstance(position.getX(), position.getY());

        at.rotate(angle, width/2, height/2);

        g2d.drawImage(texture, at, null);
    }
}
