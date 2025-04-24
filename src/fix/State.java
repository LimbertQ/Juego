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

public abstract class State {

    private static State currentState = null;

    public static State getCurrentState() {return currentState;}

    public static void changeState(State newState) {
        currentState = newState;
    }

    public abstract void update(float dt);
    public abstract void draw(Graphics g);
}