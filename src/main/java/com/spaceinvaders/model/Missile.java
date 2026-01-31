package com.spaceinvaders.model;

import com.jogamp.opengl.GL2;
import com.spaceinvaders.view.CubeRenderer;

public class Missile extends GameObject {

    private float speed = 0.8f;
    
    // Pour savoir si c'est un missile du joueur (tue les aliens) ou alien (tue le joueur)
    private boolean isPlayerMissile; 

    public Missile(float x, float y, float z, boolean isPlayerMissile) {
        super(x, y, z, 0.1f, 0.1f, 0.5f);
        this.isPlayerMissile = isPlayerMissile;
        
        // Si c'est un missile ennemi, il ira vers le joueur (speed positif ou négatif)
        if (isPlayerMissile) {
            speed = -0.8f;
        } else {
            speed = 0.4f;
        }
    }

    @Override
    public void update() {
        this.z += speed;
        
        // Si le missile part trop loin on le tue pour libérer la mémoire
        if (z < -100 || z > 20) {
            this.isAlive = false;
        }
    }

    @Override
    public void render(GL2 gl) {
        if (isPlayerMissile) {
            CubeRenderer.draw(gl, x, y, z, width, height, depth, 1.0f, 1.0f, 0.0f); // Missile joueur
        } else {
            CubeRenderer.draw(gl, x, y, z, width, height, depth, 1.0f, 0.0f, 0.0f); // Missile ennemi
        }
    }

	public boolean isPlayerMissile() {
		// TODO Auto-generated method stub
		return isPlayerMissile;
	}
}