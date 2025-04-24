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
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import io.Assets;
import io.Loader;
public class LoadingState extends State{

    private Thread loadingThread;

    private Font font;
    private boolean boton;
    private BotonCanvas b; 
    public LoadingState() {
        //BufferedImage bbb = Loader.ImageLoader("res2/av2.jpg");
        font = Loader.loadFont("/res/fuentes/futureFont.ttf", 38);
        b = new BotonCanvas();
        boton = false;
    }
    
    public void cargarNuevoNivel(Runnable cargaRunnable) {
        Assets.loaded = false;
        Assets.count = 0;
        loadingThread = new Thread(cargaRunnable);
        loadingThread.start();
    } 

    @Override
    public void update(float dt) {
        if (Assets.loaded) {
            boton = true;
            if (boton) {
                b.actualizar();
                if (b.click()) {
                    State.changeState(new GameState());
                }
            }
        }

    }

    @Override
    public void draw(Graphics g) {
        if(boton){
            b.dibujar(g);
        }else{
            GradientPaint gp = new GradientPaint(
                    Constants.WIDTH / 2 - Constants.LOADING_BAR_WIDTH / 2,
                    Constants.HEIGHT / 2 - Constants.LOADING_BAR_HEIGHT / 2,
                    Color.WHITE,
                    Constants.WIDTH / 2 + Constants.LOADING_BAR_WIDTH / 2,
                    Constants.HEIGHT / 2 + Constants.LOADING_BAR_HEIGHT / 2,
                    Color.BLUE
                );

            Graphics2D g2d = (Graphics2D)g;

            g2d.setPaint(gp);

            float percentage = (Assets.count / Assets.MAX_COUNT);

            g2d.fillRect(Constants.WIDTH / 2 - Constants.LOADING_BAR_WIDTH / 2,
                Constants.HEIGHT / 2 - Constants.LOADING_BAR_HEIGHT / 2,
                (int)(Constants.LOADING_BAR_WIDTH * percentage),
                Constants.LOADING_BAR_HEIGHT);

            g2d.drawRect(Constants.WIDTH / 2 - Constants.LOADING_BAR_WIDTH / 2,
                Constants.HEIGHT / 2 - Constants.LOADING_BAR_HEIGHT / 2,
                Constants.LOADING_BAR_WIDTH,
                Constants.LOADING_BAR_HEIGHT);

            Text.drawText(g2d, "SPACE SHIP GAME", new Vector2D(Constants.WIDTH / 2, Constants.HEIGHT / 2 - 50),
                true, Color.WHITE, font);

            Text.drawText(g2d, "LOADING...", new Vector2D(Constants.WIDTH / 2, Constants.HEIGHT / 2 + 40),
                true, Color.WHITE, font);
        }

    }

}
