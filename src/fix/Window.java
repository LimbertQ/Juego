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
import javax.swing.JFrame;
import java.util.ArrayList;

import io.Assets;

public class Window extends Lienzo implements Runnable {
    private static final long serialVersionUID = 666L;

    private final Canvas canvas;
    private Thread thread;
    private boolean running = false;

    private BufferStrategy bs;
    private Graphics g;

    private static final int FPS = 60;
    private static final double TARGET_TIME = 1_000_000_000.0 / FPS;
    private int averageFps = FPS;

    private final KeyBoard keyBoard;
    private final MouseInput mouseInput;

    private int estado = 0;
    private long lastTime;

    public Window() {
        canvas = new Canvas();
        keyBoard = new KeyBoard();
        mouseInput = new MouseInput();

        Dimension size = new Dimension(Constants.WIDTH, Constants.HEIGHT);
        canvas.setPreferredSize(size);
        canvas.setMaximumSize(size);
        canvas.setMinimumSize(size);
        canvas.setFocusable(true);

        add(canvas);
        canvas.addKeyListener(keyBoard);
        canvas.addMouseListener(mouseInput);
        canvas.addMouseMotionListener(mouseInput);
    }

    private void update(float dt) {
        keyBoard.update();
        State.getCurrentState().update(dt);
    }

    private void draw() {
        bs = canvas.getBufferStrategy();
        if (bs == null) {
            canvas.createBufferStrategy(3);
            return;
        }

        g = bs.getDrawGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);

        State.getCurrentState().draw(g);

        g.setColor(Color.WHITE);
        g.drawString("" + averageFps, 10, 20);

        g.dispose();
        bs.show();
    }

    private void init() {
        Assets.cargar();
        LoadingState loadingState = new LoadingState();
        loadingState.cargarNuevoNivel(() -> {
            Assets.inicia();
            Assets.loaded = true;
        });
        State.changeState(loadingState);
    }

    @Override
    public void run() {
        long now;
        lastTime = System.nanoTime();
        int frames = 0;
        long timeAccumulator = 0;
        double delta = 0;

        init();

        while (running) {
            if (estado == 0) {
                now = System.nanoTime();
                delta += (now - lastTime) / TARGET_TIME;
                timeAccumulator += (now - lastTime);
                lastTime = now;

                if (delta >= 1) {
                    update((float) (delta * TARGET_TIME * 0.000001f));
                    draw();
                    delta--;
                    frames++;
                }

                if (timeAccumulator >= 1_000_000_000) {
                    averageFps = frames;
                    frames = 0;
                    timeAccumulator = 0;
                }
            } else {
                lastTime = System.nanoTime();
                if (estado == 1 || estado == 3) {
                    if (estado == 3) init();
                    estado = 0;
                }
            }
        }
        stop();
    }

    public void actualizar(int i) {}
    public void actualizar(ArrayList<Boton> botones) {}

    public void start() {
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    private void stop() {
        try {
            thread.join();
            running = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
