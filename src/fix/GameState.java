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
    private ArrayList<MovingObject> movingObjects = new ArrayList<>();
    private ArrayList<MovingObject> objectsToRemove = new ArrayList<>();
    private ArrayList<PowerUp> powerUps = new ArrayList<>();

    private ArrayList<Animation> explosions = new ArrayList<>();
    private ArrayList<Message> messages = new ArrayList<>();

    private int score = 0;
    private int lives = 345;

    private int meteors;
    private int waves = 1;

    private Sound backgroundMusic;
    private Sound ufoSound;
    private long gameOverTimer;
    private boolean gameOver;

    private long ufoSpawner;
    private long powerUpSpawner;

    private int contUfoSpawner;

    private BlackHole blackHole = null;
    //private PlayerFriend playerFriend;

    private final BotonCanvas boton;
    public GameState()
    {
        player = new Player(null, new Vector2D(),
            Constants.PLAYER_MAX_VEL, Assets.aliados.get(0), this);
        /*
        playerFriend = new PlayerFriend(new Vector2D(100,100), new Vector2D(),
            Constants.METEOR_MAX_VEL, Assets.aliados.get(0), this, pati);*/

        gameOver = false;
        movingObjects.add(player);

        meteors = 1;
        //startWave();
        backgroundMusic = new Sound(Assets.backgroundMusic);
        ufoSound = new Sound(Assets.ufoSound);
        backgroundMusic.loop();
        blackHole = new BlackHole(Assets.blackHole, this);
        //backgroundMusic.changeVolume(-10.0f);
        //ufoSound.changeVolume(-10.0f);
        backgroundMusic.changeVolume(6.0f);

        gameOverTimer = 0;
        ufoSpawner = 0;
        powerUpSpawner = 0;
        contUfoSpawner = 0;

        boton = new BotonCanvas();
    }

    public void addScore(int value, Vector2D position) {
        Color c;
        String text = " score";
        if(player.isDoubleScoreOn()) {
            c = Color.YELLOW;
            value = value * 2;
            text += " (X2)";
        }else{
            c = Color.WHITE;      
        }
        text = "+"+value+text;
        score += value;
        messages.add(new Message(position, true, text, c, false, Assets.fontMed));
    }

    public void divideMeteor(Meteor meteor){

        Size size = meteor.getSize();

        MeteoroDB[] textures = size.textures;

        Size newSize;

        switch(size){
            case BIG -> newSize =  Size.MED;
            case MED -> newSize = Size.SMALL;
            case SMALL -> newSize = Size.TINY;
            default -> {
                return;
            }
        }

        for(int i = 0; i < size.quantity; i++){
            movingObjects.add(new Meteor(
                    meteor.getPosition(),
                    new Vector2D(0, 1).setDirection(Math.random()*Math.PI*2),
                    Constants.METEOR_INIT_VEL*Math.random() + 1,
                    textures[(int)(Math.random()*textures.length)],
                    this,
                    newSize
                ));
        }
    }

    private void startWave(){
        messages.add(new Message(new Vector2D(Constants.WIDTH/2, Constants.HEIGHT/2), false,
                "WAVE "+waves, Color.WHITE, true, Assets.fontBig));
        waves++;
        double x, y;
        if(meteors > 3)
            meteors = 1;
        for(int i = 0; i < meteors; i++){

            x = i % 2 == 0 ? Math.random()*Constants.WIDTH : 0;
            y = i % 2 == 0 ? 0 : Math.random()*Constants.HEIGHT;

            MeteoroDB texture = Assets.bigs[(int)(Math.random()*Assets.bigs.length)];

            movingObjects.add(new Meteor(
                    new Vector2D(x, y),
                    new Vector2D(0, 1).setDirection(Math.random()*Math.PI*2),
                    Constants.METEOR_INIT_VEL*Math.random() + 1,
                    texture,
                    this,
                    Size.BIG
                ));

        }
        meteors ++;
    }

    public void playExplosion(Vector2D position){
        explosions.add(new Animation(
                Assets.exp,
                50,
                position.subtract(new Vector2D(Assets.exp[0].getWidth()/2, Assets.exp[0].getHeight()/2))
            ));
    }

    private void spawnUfo(int indexUfo){

        int rand = (int) (Math.random()*2);

        double x = rand == 0 ? (Math.random()*Constants.WIDTH): Constants.WIDTH;
        double y = rand == 0 ? Constants.HEIGHT : (Math.random()*Constants.HEIGHT);

        movingObjects.add(new Ufo(
                new Vector2D(x, y),
                new Vector2D(),
                Constants.UFO_MAX_VEL,
                Assets.enemigos.get(indexUfo),
                this
            ));

        //movingObjects.add(playerFriend);
    }

    private void spawnPowerUp() {

        final int x = (int) ((Constants.WIDTH - Assets.orb.getImagen().getWidth()) * Math.random());
        final int y = (int) ((Constants.HEIGHT - Assets.orb.getImagen().getHeight()) * Math.random());

        int index = (int) (Math.random() * (PowerUpTypes.values().length));

        PowerUpTypes p = PowerUpTypes.values()[index];

        final String text = p.text;
        Action action = null;
        Vector2D position = new Vector2D(x , y);

        switch(p) {
            case LIFE -> action = () -> {
                    lives ++;
                    messages.add(new Message(
                            position,
                            false,
                            text,
                            Color.GREEN,
                            false,
                            Assets.fontMed
                    ));
        };
            case SHIELD -> action = () -> {
                    player.setShield();
                    messages.add(new Message(
                            position,
                            false,
                            text,
                            Color.DARK_GRAY,
                            false,
                            Assets.fontMed
                    ));
        };
            case SCORE_X2 -> action = () -> {
                    player.setDoubleScore();
                    messages.add(new Message(
                            position,
                            false,
                            text,
                            Color.YELLOW,
                            false,
                            Assets.fontMed
                    ));
        };
            case FASTER_FIRE -> action = () -> {
                    player.setFastFire();
                    messages.add(new Message(
                            position,
                            false,
                            text,
                            Color.BLUE,
                            false,
                            Assets.fontMed
                    ));
        };
            case SCORE_STACK -> action = () -> {
                    score += 1000;
                    messages.add(new Message(
                            position,
                            false,
                            text,
                            Color.MAGENTA,
                            false,
                            Assets.fontMed
                    ));
        };
            case DOUBLE_GUN -> action = () -> {
                    player.setDoubleGun();
                    messages.add(new Message(
                            position,
                            false,
                            text,
                            Color.ORANGE,
                            false,
                            Assets.fontMed
                    ));
        };
            default -> {
            }
        }

        this.powerUps.add(new PowerUp(
                position,
                p.texture,
                action,
                this
            ));
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
    
    private boolean playerIsSpawning(MovingObject a){
        boolean spawning = false;
        if((a instanceof Player) && ((Player) a).isSpawning()){
                spawning = true;
                //System.out.println("el metodo funciona");
        }
        return spawning;
    }
    
    private boolean handleCollision(MovingObject a, MovingObject b){
        boolean collides = false;
        if ((a instanceof Meteor && b instanceof Meteor) ||
        (a instanceof Ufo && b instanceof Ufo)) {
            collides = true;
        }
        return collides;
    }

    private void updateCollisions() {
        // Comprobar colisiones entre pares de objetos
        for (int i = 0; i < movingObjects.size()-1; i++) {
            MovingObject a = movingObjects.get(i);

            // Omitir objetos muertos o ya marcados para eliminación
            if (!playerIsSpawning(a) && !a.isDead() && !objectsToRemove.contains(a)){
                boolean close = false;
                
                for (int j = i+1; j < movingObjects.size() && close == false; j++) {
                    MovingObject b = movingObjects.get(j);

                    // Omitir objetos muertos o ya marcados para eliminación
                    if (!playerIsSpawning(b) && !b.isDead() && !objectsToRemove.contains(b)){
                        double distance = a.getCenter().subtract(b.getCenter()).getMagnitude();

                        if (distance < a.width / 2 + b.width / 2) {
                            if(!handleCollision(a,b)){
                                a.Destroy();
                                b.Destroy();
                                if(!playerIsSpawning(a))
                                    objectsToRemove.add(a);
                                if(!playerIsSpawning(b))
                                    objectsToRemove.add(b);
                                close = true;
                            }
                        }
                    }
                }
            }
        }
        // Eliminar todos los objetos marcados para eliminación
        if (!objectsToRemove.isEmpty()) {
            movingObjects.removeAll(objectsToRemove);
            objectsToRemove.clear();
        }
    }

    private boolean limitObjects(MovingObject o){
        Vector2D position = o.getPosition();
        boolean destroy = false;
        if(o instanceof Player || o instanceof Meteor){
            if(position.getX() > Constants.WIDTH)
                position.setX(0);
            if(position.getY() > Constants.HEIGHT)
                position.setY(0);

            if(position.getX() < -o.width)
                position.setX(Constants.WIDTH);
            if(position.getY() < -o.height)
                position.setY(Constants.HEIGHT);
        }else{
            if(position.getX() > Constants.WIDTH || position.getY() > Constants.HEIGHT
            || position.getX() < 0 || position.getY() < 0) {
                o.Destroy();
                destroy = true;
            }
        }
        return destroy;
    }
    //------------------------------------------------
    @Override
    public void update(float dt){
        boton.actualizar();

        if(gameOver)
            gameOverTimer += dt;

        powerUpSpawner += dt;
        ufoSpawner += dt;
        objectsToRemove.clear();
        //Colision entre PowerUps y jugador
        blackHole.update(dt);
        
        checkPowerUpCollisions();

        for(int i=0;i < powerUps.size(); i++){
            PowerUp powerUp = powerUps.get(i);
            powerUp.update(dt);
            if(powerUp.isDead()){
                powerUps.remove(i);
                i--;
            }
        }

        boolean isMeteor = false;

        //Colision de los objetos que no sean PowerUp
        updateCollisions();
        
        // Actualizar objetos y marcar para eliminación si es necesario
        for (int i = 0; i < movingObjects.size(); i++) {
            MovingObject mo = movingObjects.get(i);
            mo.update(dt);
            if(limitObjects(mo)){
                movingObjects.remove(i);
                i--;
            } else if (mo instanceof Meteor){
                isMeteor = true;
            }
        }

        for(int i = 0; i < explosions.size(); i++){
            Animation anim = explosions.get(i);
            anim.update(dt);
            if(!anim.isRunning()){
                explosions.remove(i);
                i--; // Ajustar índice después de eliminar
            }
        }

        if(gameOverTimer > Constants.GAME_OVER_TIME) {
            backgroundMusic.stop();
            //State.changeState(new MenuState());
        }

        if(powerUpSpawner > Constants.POWER_UP_SPAWN_TIME) {
                spawnPowerUp();
            powerUpSpawner = 0;
        }
            // En el método update
        if(ufoSpawner > Constants.UFO_SPAWN_RATE) {
                // Contar cuántos UFOs hay actualmente en pantalla
            int currentUfos = 0;
            for (MovingObject mo : movingObjects) {
                if (mo instanceof Ufo && !mo.isDead()) {
                        currentUfos++;
                }
            }

                // Generar UFOs solo si no excedemos el límite
            if (currentUfos < 4) {
                if(contUfoSpawner < 2){
                    spawnUfo(0);
                    ufoSound.play();
                    contUfoSpawner++;
                } else {
                    spawnUfo(0);
                        // Solo generar el segundo UFO si no excedemos el límite
                    if (currentUfos + 1 < 4) {
                            spawnUfo(1);
                    }
                }
            }
                ufoSpawner = 0;
        }

        if(isMeteor == false)
            startWave();
        
        /*
        // Añade al final del método update
        laserCount = 0;
        ufoCount = 0;
        for (MovingObject mo : movingObjects) {
        if (mo instanceof Laser) laserCount++;
        if (mo instanceof Ufo) ufoCount++;
        }
        System.out.println("Final de update - UFOs: " + ufoCount + ", Lasers: " + laserCount);

        System.out.println(movingObjects.size());
        /*
        if(ufoSound.getFramePosition() > 20000){
        ufoSound.stop();
        }*/
        //System.out.println(movingObjects.size());
    }

    @Override
    public void draw(Graphics g)
    {    
        Graphics2D g2d = (Graphics2D)g;
        g.drawImage(Assets.fondo,0,0,1000,600,null);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        boton.dibujar(g);
        blackHole.draw(g);
        /*else if(playerFriend.isDead() == false)
            playerFriend.draw(g);*/
        for(int i = 0; i < messages.size(); i++) {
            messages.get(i).draw(g2d);
            if(messages.get(i).isDead())
                messages.remove(i);
            //   i--; // Ajustar índice después de eliminar
        }

        for(int i = 0; i < movingObjects.size(); i++){
            MovingObject mo = movingObjects.get(i);
            if(mo instanceof Laser)
                movingObjects.get(i).draw(g);
        }

        for(int i = 0; i < movingObjects.size(); i++){
            MovingObject mo = movingObjects.get(i);
            if(!(mo instanceof Laser))
                movingObjects.get(i).draw(g);
        }

        for(int i = 0; i < powerUps.size(); i++)
            powerUps.get(i).draw(g);

        for(int i = 0; i < explosions.size(); i++){
            Animation anim = explosions.get(i);
            Vector2D posAnim = anim.getPosition(); 
            g2d.drawImage(anim.getCurrentFrame(), (int)posAnim.getX(), (int)posAnim.getY(),
                null);
        }

        drawScore(g);
        drawLives(g);
    }

    private void drawNumbers(int posNumX, int posNumY, int numero,Graphics g){
        int numDigitos = (int) Math.log10(numero) + 1;
        int divisor, digito;
        int temp = numero;
        for (int i = numDigitos - 1; i >= 0; i--) {
            divisor = (int) Math.pow(10, i);
            digito = temp / divisor;
            g.drawImage(Assets.numbers[digito],
                posNumX, posNumY, null);
            temp %= divisor;
            posNumX += 20;
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

    private void drawLives(Graphics g){

        if(lives > 0){
            //dibujamos la nave que simboliza la vida
            g.drawImage(Assets.life.getImagen(), 25, 25, null);
            //dibujamos la x que simboliza la cantidad
            g.drawImage(Assets.numbers[10], 65, 30, null);

            drawNumbers(85, 30, lives, g);
        }

    }

    public ArrayList<MovingObject> getMovingObjects() {
        return movingObjects;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public Player getPlayer() {
        return player;
    }
    
    public void setLives(){
        lives++;
    }
    
    public void setScore(){
        score+=1000;
    }

    public boolean subtractLife(Vector2D position) {
        lives --;

        Message lifeLostMesg = new Message(
                position,
                false,
                "-1 LIFE",
                Color.RED,
                false,
                Assets.fontMed
            );
        messages.add(lifeLostMesg);

        return lives > 0;
    }

    public void gameOver() {
        Message gameOverMsg = new Message(
                player.startPosition(),
                true,
                "GAME OVER",
                Color.WHITE,
                true,
                Assets.fontBig);

        this.messages.add(gameOverMsg);
        gameOver = true;
    }

}
