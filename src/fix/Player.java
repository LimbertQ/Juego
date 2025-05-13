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

public class Player extends MovingObject {

    private final ProyectilDB proyectil;
    private Vector2D heading;
    private Vector2D acceleration;

    private boolean accelerating = false;
    private long fireRate;
    private long fireSpeed;

    private boolean spawning = false;
    private boolean visible = true;

    private long spawnTime, flickerTime, shieldTime, doubleScoreTime, fastFireTime, doubleGunTime;
    private boolean shieldOn, doubleScoreOn, fastFireOn, doubleGunOn;

    private final Sound shoot, loose;
    private final Animation shieldEffect;

    private final Vector2D PLAYER_START_POSITION;

    public Player(Vector2D position, Vector2D velocity, double maxVel, NaveDB textura, GameState gameState) {
        super(position, velocity, maxVel, textura, gameState);

        heading = new Vector2D(0, 1);
        acceleration = new Vector2D();
        proyectil = textura.getProyectil();

        PLAYER_START_POSITION = new Vector2D(Constants.WIDTH / 2 - texture.getWidth() / 2,
                Constants.HEIGHT / 2 - texture.getHeight() / 2);
        this.position = PLAYER_START_POSITION;

        shoot = new Sound(Assets.playerShoot);
        loose = new Sound(Assets.playerLoose);

        shieldEffect = new Animation(Assets.shieldEffect, 80, null);
    }

    @Override
    public void update(float dt) {
        fireRate += dt;
        fireSpeed = fastFireOn ? Constants.FIRERATE / 2 : Constants.FIRERATE;

        updatePowerUpTimers(dt);
        updateSpawningState(dt);

        if (!containsBlackHole()) {
            handleInput();

            if ((KeyBoard.SHOOT || KeyBoard.SHOOT_PRESSED) && fireRate > fireSpeed && !spawning) {
                fire();
                fireRate = 0;
            }

            velocity = velocity.add(acceleration).limit(maxVel);
            heading = heading.setDirection(angle - Math.PI / 2);
            position = position.add(velocity);
        }

        if (shoot.getFramePosition() > 8500) shoot.stop();
        if (shieldOn) shieldEffect.update(dt);
    }

    private void updatePowerUpTimers(float dt) {
        if (shieldOn && (shieldTime += dt) > Constants.SHIELD_TIME) {
            shieldOn = false;
            shieldTime = 0;
        }

        if (doubleScoreOn && (doubleScoreTime += dt) > Constants.DOUBLE_SCORE_TIME) {
            doubleScoreOn = false;
            doubleScoreTime = 0;
        }

        if (fastFireOn && (fastFireTime += dt) > Constants.FAST_FIRE_TIME) {
            fastFireOn = false;
            fastFireTime = 0;
        }

        if (doubleGunOn && (doubleGunTime += dt) > Constants.DOUBLE_GUN_TIME) {
            doubleGunOn = false;
            doubleGunTime = 0;
        }
    }

    private void updateSpawningState(float dt) {
        if (spawning) {
            flickerTime += dt;
            spawnTime += dt;

            if (flickerTime > Constants.FLICKER_TIME) {
                visible = !visible;
                flickerTime = 0;
            }

            if (spawnTime > Constants.SPAWNING_TIME) {
                spawning = false;
                visible = true;
            }
        }
    }

    private void handleInput() {
        if (KeyBoard.RIGHT) angle += Constants.DELTAANGLE;
        if (KeyBoard.LEFT) angle -= Constants.DELTAANGLE;

        if (KeyBoard.UP) {
            acceleration = heading.scale(Constants.ACC);
            accelerating = true;
        } else {
            accelerating = false;
            if (velocity.getMagnitudeSq() > 0)
                acceleration = velocity.normalize().scale(-Constants.ACC / 2);
        }
    }

    private void fire() {
        Vector2D center = getCenter();

        if (doubleGunOn) {
            Vector2D left = center.add(heading.copy().setDirection(angle - 1.9f).scale(width));
            Vector2D right = center.add(heading.copy().setDirection(angle - 1.3f).scale(width));
gameState.getMovingObjects().add(new Laser(left, heading, Constants.LASER_VEL, angle, proyectil, gameState));
            gameState.getMovingObjects().add(new Laser(right, heading, Constants.LASER_VEL, angle, proyectil, gameState));
        } else {
            Vector2D muzzle = center.add(heading.scale(width));
            gameState.getMovingObjects().add(new Laser(muzzle, heading, Constants.LASER_VEL, angle, proyectil, gameState));
        }

        shoot.play();
    }

    @Override
    public void Destroy(int da) {
        spawning = true;
        spawnTime = 0;
        gameState.playExplosion(position);
        resistencia -= da;

        if (resistencia < 1) {
            loose.play();
            if (!gameState.subtractLife(position)) {
                gameState.gameOver();
                super.Destroy();
            }
            resetValues();
        }
    }

    @Override
    public void Destroy() {
        spawning = true;
        spawnTime = 0;
        gameState.playExplosion(position);
        loose.play();
        if (!gameState.subtractLife(position)) {
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
        if (!visible) return;

        Graphics2D g2d = (Graphics2D) g;

        if (accelerating) {
            AffineTransform at1 = AffineTransform.getTranslateInstance(position.getX() + width / 2 + 5,
                    position.getY() + height / 2 + 10);
            AffineTransform at2 = AffineTransform.getTranslateInstance(position.getX() + 5,
                    position.getY() + height / 2 + 10);
            at1.rotate(angle, -5, -10);
            at2.rotate(angle, width / 2 - 5, -10);
            g2d.drawImage(Assets.speed, at1, null);
            g2d.drawImage(Assets.speed, at2, null);
        }

        if (shieldOn) {
            BufferedImage frame = shieldEffect.getCurrentFrame();
            AffineTransform atShield = AffineTransform.getTranslateInstance(
                    position.getX() - frame.getWidth() / 2 + width / 2,
                    position.getY() - frame.getHeight() / 2 + height / 2);
            atShield.rotate(angle, frame.getWidth() / 2.0, frame.getHeight() / 2.0);
            g2d.drawImage(frame, atShield, null);
        }

        AffineTransform at = AffineTransform.getTranslateInstance(position.getX(), position.getY());
        at.rotate(angle, width / 2.0, height / 2.0);
        g2d.drawImage(texture, at, null);
    }

    // Métodos públicos para power-ups y estado

    public void setShield() {
        shieldTime = 0;
        shieldOn = true;
    }

    public void setDoubleScore() {
        doubleScoreTime = 0;
        doubleScoreOn = true;
    }

    public void setFastFire() {
        fastFireTime = 0;
        fastFireOn = true;
    }

    public void setDoubleGun() {
        doubleGunTime = 0;
        doubleGunOn = true;
    }

    public boolean isSpawning() {
        return spawning;
    }

    public boolean isShieldOn() {
        return shieldOn;
    }

    public boolean isDoubleScoreOn() {
        return doubleScoreOn;
    }

    public Vector2D startPosition() {
        return PLAYER_START_POSITION;
    }
}
