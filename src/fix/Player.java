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
import io.NaveDB;
import io.Assets;
import io.ProyectilDB;
public class Player extends MovingObject{
    private ProyectilDB proyectil;

    private Vector2D heading;    
    private Vector2D acceleration;

    private boolean accelerating = false;
    private long fireRate;

    private boolean spawning, visible;

    private long spawnTime, flickerTime, shieldTime, doubleScoreTime, fastFireTime, doubleGunTime;

    private Sound shoot, loose;

    private boolean shieldOn, doubleScoreOn, fastFireOn, doubleGunOn;;

    private Animation shieldEffect;

    private long fireSpeed;

    private Vector2D PLAYER_START_POSITION;

    public Player(Vector2D position, Vector2D velocity, double maxVel, NaveDB textura, GameState gameState) {
        super(position, velocity, maxVel, textura, gameState);
        heading = new Vector2D(0, 1);
        acceleration = new Vector2D();

        PLAYER_START_POSITION = new Vector2D(Constants.WIDTH/2 - texture.getWidth()/2,
            Constants.HEIGHT/2 - texture.getHeight()/2);
        this.position = PLAYER_START_POSITION;

        fireRate = 0;
        spawnTime = 0;
        flickerTime = 0;
        shieldTime = 0;
        fastFireTime = 0;
        doubleGunTime = 0;

        shoot = new Sound(Assets.playerShoot);
        loose = new Sound(Assets.playerLoose);

        shieldEffect = new Animation(Assets.shieldEffect, 80, null);

        proyectil = textura.getProyectil();

        visible = true;
    }

    @Override
    public void update(float dt) 
    {

        fireRate += dt;
        if(shieldOn)
            shieldTime += dt;

        if(doubleScoreOn)
            doubleScoreTime += dt;

        if(fastFireOn) {
            fireSpeed = Constants.FIRERATE / 2;
            fastFireTime += dt;
        }else {
            fireSpeed = Constants.FIRERATE;
        }

        if(doubleGunOn)
            doubleGunTime += dt;

        if(shieldTime > Constants.SHIELD_TIME) {
            shieldTime = 0;
            shieldOn = false;
        }

        if(doubleScoreTime > Constants.DOUBLE_SCORE_TIME) {
            doubleScoreOn = false;
            doubleScoreTime = 0;
        }

        if(fastFireTime > Constants.FAST_FIRE_TIME) {
            fastFireOn = false;
            fastFireTime = 0;
        }

        if(doubleGunTime > Constants.DOUBLE_GUN_TIME) {
            doubleGunOn = false;
            doubleGunTime = 0;
        }

        if(spawning) {

            flickerTime += dt;
            spawnTime += dt;

            if(flickerTime > Constants.FLICKER_TIME) {

                visible = !visible;
                flickerTime = 0;
            }

            if(spawnTime > Constants.SPAWNING_TIME) {
                spawning = false;
                visible = true;
            }

        }
        
        if(!containsBlackHole()){
            if((KeyBoard.SHOOT &&  fireRate > fireSpeed && !spawning)
                || (KeyBoard.SHOOT_PRESSED && fireRate > fireSpeed && !spawning))
            {

                if(doubleGunOn) {
                    Vector2D leftGun = getCenter();
                    Vector2D rightGun = getCenter();

                    Vector2D temp = new Vector2D(heading);
                    temp.normalize();
                    temp = temp.setDirection(angle - 1.3f);
                    temp = temp.scale(width);
                    rightGun = rightGun.add(temp);

                    temp = temp.setDirection(angle - 1.9f);
                    leftGun = leftGun.add(temp);

                    Laser l = new Laser(leftGun, heading, Constants.LASER_VEL, angle, proyectil, gameState);
                    Laser r = new Laser(rightGun, heading, Constants.LASER_VEL, angle, proyectil, gameState);

                    gameState.getMovingObjects().add(l);
                    gameState.getMovingObjects().add(r);

                }else {
                    gameState.getMovingObjects().add(new Laser(
                            getCenter().add(heading.scale(width)),
                            heading,
                            Constants.LASER_VEL,
                            angle,
                            proyectil,
                            gameState
                        ));
                }

                fireRate = 0;
                shoot.play();
            }

            if(KeyBoard.RIGHT)
                angle += Constants.DELTAANGLE;
            if(KeyBoard.LEFT)
                angle -= Constants.DELTAANGLE;

            if(KeyBoard.UP)
            {
                acceleration = heading.scale(Constants.ACC);
                accelerating = true;
            }else
            {
                if(velocity.getMagnitude() != 0)
                    acceleration = (velocity.scale(-1).normalize()).scale(Constants.ACC/2);
                accelerating = false;
            }

            velocity = velocity.add(acceleration);

            velocity = velocity.limit(maxVel);

            heading = heading.setDirection(angle - Math.PI/2);

            position = position.add(velocity);
        }

        if(shoot.getFramePosition() > 8500) {
            shoot.stop();
        }
        if(shieldOn)
            shieldEffect.update(dt);
        //collidesWith();
    }

    public void setShield() {
        if(shieldOn)
            shieldTime = 0;
        shieldOn = true;
    }

    public void setDoubleScore() {
        if(doubleScoreOn)
            doubleScoreTime = 0;
        doubleScoreOn = true;
    }

    public void setFastFire() {
        if(fastFireOn)
            fastFireTime = 0;
        fastFireOn = true;
    }

    public void setDoubleGun() {
        if(doubleGunOn)
            doubleGunTime = 0;
        doubleGunOn = true;
    }

    @Override
    public void Destroy(int da) {
        spawning = true;
        gameState.playExplosion(position);
        spawnTime = 0;
        resistencia -= da;
        if(resistencia < 1){
            loose.play();
            if(!gameState.subtractLife(position)) {
                gameState.gameOver();
                super.Destroy();
            }
            resetValues();
        }

    }

    @Override
    public void Destroy() {
        spawning = true;
        
        gameState.playExplosion(position);
        spawnTime = 0;
        loose.play();
        if(!gameState.subtractLife(position)) {
            gameState.gameOver();
            super.Destroy();
        }
        resetValues();

    }

    private void resetValues() {

        angle = 0;
        velocity = new Vector2D();
        position = PLAYER_START_POSITION;
        containsBlackHole = false;
    }

    @Override
    public void draw(Graphics g) {  

        if(!visible)
            return;

        Graphics2D g2d = (Graphics2D)g;
        AffineTransform at1 = AffineTransform.getTranslateInstance(position.getX() + width/2 + 5,
                position.getY() + height/2 + 10);

        AffineTransform at2 = AffineTransform.getTranslateInstance(position.getX() + 5, position.getY() + height/2 + 10);

        at1.rotate(angle, -5, -10);
        at2.rotate(angle, width/2 -5, -10);

        if(accelerating)
        {
            g2d.drawImage(Assets.speed, at1, null);
            g2d.drawImage(Assets.speed, at2, null);
        }

        if(shieldOn) {
            BufferedImage currentFrame = shieldEffect.getCurrentFrame();
            AffineTransform at3 = AffineTransform.getTranslateInstance(
                    position.getX() - currentFrame.getWidth() / 2 + width/2,
                    position.getY() - currentFrame.getHeight() / 2 + height/2);

            at3.rotate(angle, currentFrame.getWidth() / 2, currentFrame.getHeight() / 2);

            g2d.drawImage(shieldEffect.getCurrentFrame(), at3, null);
        }

        at = AffineTransform.getTranslateInstance(position.getX(), position.getY());

        at.rotate(angle, width/2, height/2);

        if(doubleGunOn)
        //g2d.drawImage(Assets.doubleGunPlayer, at, null);
            g2d.drawImage(texture, at, null);
        else
            g2d.drawImage(texture, at, null);

    }

    public Vector2D startPosition(){
        return PLAYER_START_POSITION;
    }

    public boolean isSpawning() {return spawning;}

    public boolean isShieldOn() {return shieldOn;}

    public boolean isDoubleScoreOn() {return doubleScoreOn;}
}
