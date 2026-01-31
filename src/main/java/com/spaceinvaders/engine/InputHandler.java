package com.spaceinvaders.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {

    private boolean left = false;
    private boolean right = false;
    private boolean space = false;
    private boolean enter = false;
    private boolean escape = false;

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_LEFT) left = true;
        if (code == KeyEvent.VK_RIGHT) right = true;
        if (code == KeyEvent.VK_SPACE) space = true;
        if (code == KeyEvent.VK_ENTER) enter = true;
        if (code == KeyEvent.VK_ESCAPE) escape = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_LEFT) left = false;
        if (code == KeyEvent.VK_RIGHT) right = false;
        if (code == KeyEvent.VK_SPACE) space = false;
        if (code == KeyEvent.VK_ENTER) enter = false;
        if (code == KeyEvent.VK_ESCAPE) escape = false;
    }

    // --- Getters pour que les entités consultent l'état ---
    public boolean isLeft() { return left; }
    public boolean isRight() { return right; }
    public boolean isSpace() { return space; }
    public boolean isEnter() { return enter; }
    public boolean isEscape() { return escape; }

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}