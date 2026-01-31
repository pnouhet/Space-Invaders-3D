package com.spaceinvaders.model;

import com.jogamp.opengl.GL2;
import com.spaceinvaders.view.CubeRenderer;

public class DefenseTower extends GameObject {

    private int maxHealth = 4;
    private int currentHealth;

    public DefenseTower(float x, float y, float z) {
        super(x, y, z, 1.5f, 1.0f, 0.5f);
        this.currentHealth = maxHealth;
    }

    public void takeDamage() {
        currentHealth--;
        if (currentHealth <= 0) {
            this.isAlive = false;
        }
    }

    @Override
    public void update() {
    	//La tour esst immobile, inutile de update sa position
    }

    @Override
    public void render(GL2 gl) {
        if (!isAlive) return;

        // Gestion de la couleur
        float r = 0, g = 0, b = 0;
        float shieldAlpha = 0.4f;

        switch (currentHealth) {
            case 4: r=0f; g=0f; b=1f; break;
            case 3: r=0f; g=0.5f; b=1f; break;
            case 2: r=0f; g=1.0f; b=1f; break;
            case 1: r=1f; g=0f; b=0f; shieldAlpha=0.2f; break;
        }

        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        
        // PYLONES
        CubeRenderer.draw(gl, -0.8f, 0.1f, 0, 0.15f, 1.0f, 0.4f, 0.5f, 0.5f, 0.5f);
        CubeRenderer.draw(gl,  0.8f, 0.1f, 0, 0.15f, 1.0f, 0.4f, 0.5f, 0.5f, 0.5f);
        
        // CHAMP DE FORCE (transparent)
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        
        // Le champ est entre les pylones
        CubeRenderer.draw(gl, 0, 0.1f, 0, 1.4f, 0.8f, 0.1f, r, g, b, shieldAlpha);
        
        // Barre de vie
        for(int i=0; i<currentHealth; i++) {
             float px = -0.5f + (i * 0.35f);
             CubeRenderer.draw(gl, px, 0.6f, 0, 0.2f, 0.1f, 0.1f, r, g, b, 1.0f);
        }

        gl.glDisable(GL2.GL_BLEND);
        gl.glPopMatrix();
    }
}