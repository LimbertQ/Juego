/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fix;

/**
 *
 * @author pc
 */
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import javax.swing.JPanel;
import java.util.ArrayList;

import javax.swing.JFrame;
import io.Assets;

public class Window extends Lienzo implements Runnable{ 
    private static final long serialVersionUID = 666L;
    private Canvas canvas;
    private Thread thread;
    private boolean running = false;

    private BufferStrategy bs;
    private Graphics g;

    private final int FPS = 60;
    private double TARGETTIME = 1000000000/FPS;
    private double delta = 0;
    private int AVERAGEFPS = FPS;

    private KeyBoard keyBoard;
    private MouseInput mouseInput;

    private int estado = 0;
    private long lastTime;

    private long timeFrames = 0;

    public Window()
    {
        /*
        setTitle("Los Invasores planetarios");
        setSize(Constants.WIDTH, Constants.HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);*/

        canvas = new Canvas();
        keyBoard = new KeyBoard();
        mouseInput = new MouseInput();

        canvas.setPreferredSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));
        canvas.setMaximumSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));
        canvas.setMinimumSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));
        canvas.setFocusable(true);

        add(canvas);
        canvas.addKeyListener(keyBoard);
        canvas.addMouseListener(mouseInput);
        canvas.addMouseMotionListener(mouseInput);
    }

    private void update(float dt){
        keyBoard.update();
        State.getCurrentState().update(dt);

    }

    private void draw(){
        bs = canvas.getBufferStrategy();

        if(bs == null)
        {
            canvas.createBufferStrategy(3);
            return;
        }

        g = bs.getDrawGraphics();

        //-----------------------

        g.setColor(Color.BLACK);

        g.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);

        State.getCurrentState().draw(g);

        g.setColor(Color.WHITE);

        g.drawString(""+AVERAGEFPS, 10, 20);

        //---------------------

        g.dispose();
        bs.show();
    }

    private void init()
    {
        Assets.cargar();
        /*
        Thread loadingThread = new Thread(new Runnable() {

        @Override
        public void run() {
        Assets.inicia();
        }
        });*/
        LoadingState loadingState = new LoadingState(); 
        loadingState.cargarNuevoNivel(() ->{
                Assets.inicia();
                Assets.loaded = true;
            });
        State.changeState(loadingState);
    }

    @Override
    public void run() {

        long now = 0;
        lastTime = System.nanoTime();
        int frames = 0;
        long time = 0;

        init();

        while(running)
        {
            if(estado == 0){
                now = System.nanoTime();
                delta += (now - lastTime)/TARGETTIME;
                time += (now - lastTime);
                lastTime = now;

                if(delta >= 1)
                {    
                    update((float) (delta * TARGETTIME * 0.000001f));
                    draw();
                    delta --;
                    frames ++;
                }
                if(time >= 1000000000)
                {

                    AVERAGEFPS = frames;
                    frames = 0;
                    time = 0;

                }
            }else{
                lastTime = System.nanoTime();
                if(estado == 1){
                    estado = 0;
                }else if(estado == 3){
                    init();
                    estado = 0;
                }
            }
        }
        stop();
    }

    public void actualizar(int i){

    }

    public void actualizar(ArrayList<Boton> b){

    }

    public void start(){

        thread = new Thread(this);
        thread.start();
        running = true;

    }

    private void stop(){
        try {
            thread.join();
            running = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
