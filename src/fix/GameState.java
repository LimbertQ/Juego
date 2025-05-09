/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fix;

/**
 *
 * @author pc
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import io.Assets;
import io.MeteoroDB;
public class GameState extends State{

    private final Player player;
    private final BotonCanvas boton;
    private final ArrayList<MovingObject> movingObjects = new ArrayList<>();
    private final ArrayList<MovingObject> objectsToRemove = new ArrayList<>();
    private final ArrayList<PowerUp> powerUps = new ArrayList<>();
    private final ArrayList<Animation> explosions = new ArrayList<>();
    private final ArrayList<Message> messages = new ArrayList<>();

    private final Sound backgroundMusic;
    private final Sound ufoSound;

    private final BlackHole blackHole;

    private int score = 0, lives = 300, meteors = 1, waves = 1;
    private long gameOverTimer = 0, ufoSpawner = 0, powerUpSpawner = 0;
    private int contUfoSpawner = 0;
    private boolean gameOver = false;

    public GameState() {
        player = new Player(null, new Vector2D(), Constants.PLAYER_MAX_VEL, Assets.aliados.get(0), this);
        movingObjects.add(player);
        blackHole = new BlackHole(Assets.blackHole, this);

        backgroundMusic = new Sound(Assets.backgroundMusic);
        backgroundMusic.loop();
        backgroundMusic.changeVolume(6.0f);

        ufoSound = new Sound(Assets.ufoSound);
        boton = new BotonCanvas();
    }

    public void addScore(int value, Vector2D pos) {
        if (player.isDoubleScoreOn()) {
            value *= 2;
            showMessage(pos, "+" + value + " score (X2)", Color.YELLOW);
        } else {
            showMessage(pos, "+" + value + " score", Color.WHITE);
        }
        score += value;
    }

    public void divideMeteor(Meteor meteor) {
        Size size = meteor.getSize();
        if (size == Size.TINY) return;

        Size newSize = switch (size) {
            case BIG -> Size.MED;
            case MED -> Size.SMALL;
            case SMALL -> Size.TINY;
            default -> null;
        };

        MeteoroDB[] textures = size.textures;
        for (int i = 0; i < size.quantity; i++) {
            movingObjects.add(new Meteor(
                    meteor.getPosition(),
                    new Vector2D(0, 1).setDirection(Math.random() * Math.PI * 2),
                    Constants.METEOR_INIT_VEL * Math.random() + 1,
                    textures[(int) (Math.random() * textures.length)],
                    this,
                    newSize));
        }
    }

    private void startWave() {
        messages.add(new Message(new Vector2D(Constants.WIDTH / 2, Constants.HEIGHT / 2),
                false, "WAVE " + waves, Color.WHITE, true, Assets.fontBig));
        waves++;

        meteors = Math.max(1, meteors);
        for (int i = 0; i < meteors; i++) {
            double x = i % 2 == 0 ? Math.random() * Constants.WIDTH : 0;
            double y = i % 2 == 0 ? 0 : Math.random() * Constants.HEIGHT;
            MeteoroDB texture = Assets.bigs[(int) (Math.random() * Assets.bigs.length)];

            movingObjects.add(new Meteor(
                    new Vector2D(x, y),
                    new Vector2D(0, 1).setDirection(Math.random() * Math.PI * 2),
                    Constants.METEOR_INIT_VEL * Math.random() + 1,
                    texture,
                    this,
                    Size.BIG));
        }
        meteors++;
    }

    public void playExplosion(Vector2D position) {
        Vector2D adjusted = position.subtract(new Vector2D(Assets.exp[0].getWidth() / 2, Assets.exp[0].getHeight() / 2));
        explosions.add(new Animation(Assets.exp, 50, adjusted));
    }

    private void spawnUfo(int index) {
        double x = Math.random() * Constants.WIDTH;
        double y = 0;
        movingObjects.add(new Ufo(new Vector2D(x, y), new Vector2D(), Constants.UFO_MAX_VEL, Assets.enemigos.get(index), this));
    }
    
    private void showMessage(Vector2D pos, String text, Color color) {
        messages.add(new Message(pos, false, text, color, false, Assets.fontMed));
    }

    private void spawnPowerUp() {
        int x = (int) ((Constants.WIDTH - Assets.orb.getImagen().getWidth()) * Math.random());
        int y = (int) ((Constants.HEIGHT - Assets.orb.getImagen().getHeight()) * Math.random());
        Vector2D pos = new Vector2D(x, y);

        PowerUpTypes type = PowerUpTypes.values()[(int) (Math.random() * PowerUpTypes.values().length)];
        Action action = switch (type) {
            case LIFE -> () -> { lives++; showMessage(pos, "+1 LIFE", Color.GREEN); };
            case SHIELD -> () -> { player.setShield(); showMessage(pos, type.text, Color.DARK_GRAY); };
            case SCORE_X2 -> () -> { player.setDoubleScore(); showMessage(pos, type.text, Color.YELLOW); };
            case FASTER_FIRE -> () -> { player.setFastFire(); showMessage(pos, type.text, Color.BLUE); };
            case SCORE_STACK -> () -> { score += 1000; showMessage(pos, "+1000 SCORE", Color.MAGENTA); };
            case DOUBLE_GUN -> () -> { player.setDoubleGun(); showMessage(pos, type.text, Color.ORANGE); };
        };

        powerUps.add(new PowerUp(pos, type.texture, action, this));
    }

    //------------------------------------------------
    // Métodos de colisión mejorados

    private void checkPowerUpCollisions() {
        if(!player.isDead() && !player.isSpawning()){
            Vector2D pCenter = player.getCenter();
            for (int i = 0; i < powerUps.size(); i++) {
                PowerUp powerUp = powerUps.get(i);
                // Solo verificamos colisión con el jugador
                double distance = powerUp.getCenter().subtract(pCenter).getMagnitude();

                if (distance < powerUp.width / 2 + player.width / 2) {
                    powerUp.executeAction();
                    powerUps.remove(i);
                    i--;
                }
            }
        }
    }
    
    private boolean playerIsSpawning(MovingObject obj) {
        return (obj instanceof Player) && ((Player) obj).isSpawning();
    }
    
    private boolean handleCollision(MovingObject a, MovingObject b) {
        return (a instanceof Meteor && b instanceof Meteor) ||
               (a instanceof Ufo && b instanceof Ufo);
    }

    private void updateCollisions() {
        for (int i = 0; i < movingObjects.size() - 1; i++) {
            MovingObject a = movingObjects.get(i);
            if (a.isDead() || playerIsSpawning(a)) continue;

            for (int j = i + 1; j < movingObjects.size(); j++) {
                MovingObject b = movingObjects.get(j);
                if (b.isDead() || playerIsSpawning(b)) continue;

                double distance = a.getCenter().subtract(b.getCenter()).getMagnitude();
                if (distance < (a.width / 2.0 + b.width / 2.0)) {
                    if (!handleCollision(a, b)) {
                        a.Destroy();
                        b.Destroy();
                        if (!playerIsSpawning(a)) objectsToRemove.add(a);
                        if (!playerIsSpawning(b)) objectsToRemove.add(b);
                    }
                }
            }
        }

        if (!objectsToRemove.isEmpty()) {
            movingObjects.removeAll(objectsToRemove);
            objectsToRemove.clear();
        }
    }

    private boolean limitObjects(MovingObject o) {
        Vector2D pos = o.getPosition();
        if (o instanceof Player || o instanceof Meteor) {
            if (pos.getX() > Constants.WIDTH) pos.setX(0);
            if (pos.getY() > Constants.HEIGHT) pos.setY(0);
            if (pos.getX() < -o.width) pos.setX(Constants.WIDTH);
            if (pos.getY() < -o.height) pos.setY(Constants.HEIGHT);
        } else {
            if (pos.getX() > Constants.WIDTH || pos.getY() > Constants.HEIGHT ||
                pos.getX() < 0 || pos.getY() < 0) {
                o.Destroy();
                return true;
            }
        }
        return false;
    }
    
    private void updateMovingObjects(float dt) {
        for (int i = 0; i < movingObjects.size(); i++) {
            MovingObject mo = movingObjects.get(i);
            mo.update(dt);
            if (limitObjects(mo)) {
                movingObjects.remove(i);
                i--;
            }
        }
    }
    
    private void updatePowerUps(float dt) {
        for (int i = 0; i < powerUps.size(); i++) {
            PowerUp pu = powerUps.get(i);
            pu.update(dt);
            if (pu.isDead()) {
                powerUps.remove(i);
                i--;
            }
        }
    }
    
    private void updateExplosions(float dt) {
        for (int i = 0; i < explosions.size(); i++) {
            Animation anim = explosions.get(i);
            anim.update(dt);
            if (!anim.isRunning()) {
                explosions.remove(i);
                i--;
            }
        }
    }
    
    private boolean hasActiveMeteors() {
        return movingObjects.stream().anyMatch(m -> m instanceof Meteor);
    }
    
    private void spawnUfos() {
        int currentUfos = 0;
        for (MovingObject mo : movingObjects)
            if (mo instanceof Ufo && !mo.isDead()) currentUfos++;

        if (currentUfos >= 4) return;

        spawnUfo(0);
        ufoSound.play();
        contUfoSpawner++;

        if (contUfoSpawner >= 2 || currentUfos + 1 >= 4) return;
        spawnUfo(1);
    }
    //------------------------------------------------
    @Override
    public void update(float dt) {
        boton.actualizar();
        if (gameOver) gameOverTimer += dt;

        powerUpSpawner += dt;
        ufoSpawner += dt;
        objectsToRemove.clear();

        blackHole.update(dt);
        checkPowerUpCollisions();

        updatePowerUps(dt);
        updateCollisions();
        updateMovingObjects(dt);
        updateExplosions(dt);

        if (gameOverTimer > Constants.GAME_OVER_TIME) backgroundMusic.stop();
        if (powerUpSpawner > Constants.POWER_UP_SPAWN_TIME) { spawnPowerUp(); powerUpSpawner = 0; }
        if (ufoSpawner > Constants.UFO_SPAWN_RATE) { spawnUfos(); ufoSpawner = 0; }
        if (!hasActiveMeteors()) startWave();
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g.drawImage(Assets.fondo, 0, 0, Constants.WIDTH, Constants.HEIGHT, null);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        boton.dibujar(g);
        blackHole.draw(g);

        for (Message msg : messages) msg.draw(g2d);
        messages.removeIf(Message::isDead);

        for (MovingObject mo : movingObjects) mo.draw(g);
        for (PowerUp p : powerUps) p.draw(g);
        for (Animation anim : explosions) {
            g2d.drawImage(anim.getCurrentFrame(), (int) anim.getPosition().getX(), (int) anim.getPosition().getY(), null);
        }

        drawScore(g);
        drawLives(g);
    }


    private void drawNumbers(int x, int y, int number, Graphics g) {
        String str = String.valueOf(number);
        for (char c : str.toCharArray()) {
            g.drawImage(Assets.numbers[Character.getNumericValue(c)], x, y, null);
            x += 20;
        }
    }
    
    private void drawScore(Graphics g) {
        if(score == 0){
            g.drawImage(Assets.numbers[0],
                850, 25, null);
        }else{
            drawNumbers(850, 25, score, g);
        }
    }

    private void drawLives(Graphics g) {
        if (lives > 0) {
            g.drawImage(Assets.life.getImagen(), 25, 25, null);
            g.drawImage(Assets.numbers[10], 65, 30, null); // X
            drawNumbers(85, 30, lives, g);
        }
    }

    public Player getPlayer() { return player; }
    public ArrayList<MovingObject> getMovingObjects() { return movingObjects; }
    public ArrayList<Message> getMessages() { return messages; }
    public void setLives() { lives++; }
    public void setScore() { score += 1000; }

    public boolean subtractLife(Vector2D pos) {
        lives--;
        showMessage(pos, "-1 LIFE", Color.RED);
        return lives > 0;
    }

    public void gameOver() {
        messages.add(new Message(player.startPosition(), true, "GAME OVER", Color.WHITE, true, Assets.fontBig));
        gameOver = true;
    }

}