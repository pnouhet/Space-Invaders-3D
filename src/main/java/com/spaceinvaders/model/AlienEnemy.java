package com.spaceinvaders.model;

import com.jogamp.opengl.GL2;
import com.spaceinvaders.view.CubeRenderer;

public class AlienEnemy extends GameObject {

    private int type;
    
    // Animation
    private float animTime = 0.0f;
    private float randomOffset = 0.0f;

    public AlienEnemy(float x, float y, float z, int type) {
        super(x, y, z, 0.8f, 0.5f, 0.8f);
        this.type = type;
        // Décalage pour enlever la synchro
        this.randomOffset = (float) (Math.random() * 100.0f);
    }

    @Override
    public void update() {
        animTime += 0.1f;
    }

    @Override
    public void render(GL2 gl) {
        if (!isAlive) return;

        float r=1, g=1, b=1;
        switch (type) {
            case 1: r = 1.0f; g = 0.5f; b = 0.5f; break;
            case 2: r = 1.2f; g = 0.0f; b = 0.3f; break;
            case 3: r = 1.0f; g = 0.6f; b = 0.8f; break;
        }
        
        gl.glPushMatrix(); 
        
        // Position
        gl.glTranslatef(x, y, z);

        // Animation
        float totalTime = animTime + randomOffset;
        
        float hoverY = (float) Math.sin(totalTime * 0.5f) * 0.5f;      // Idle
        float wobbleAngle = (float) Math.cos(totalTime * 2.5f) * 10.0f; // Wobble
        float spinAngle = (float) Math.sin(totalTime * 0.5f) * 5.0f;    // Rotation

        gl.glTranslatef(0.0f, hoverY, 0.0f);
        gl.glRotatef(wobbleAngle, 0.0f, 0.0f, 1.0f);
        gl.glRotatef(spinAngle, 0.0f, 1.0f, 0.0f);
        
        // --- MODELING DES ALIENS ---
        
        if (type == 1) { 
            // CALAMAR
            CubeRenderer.draw(gl, 0, 0.2f, 0, 0.6f, 0.5f, 0.6f, r, g, b);
            
            float legMove = (float) Math.sin(totalTime * 0.5f) * 0.1f;

            // Jambes
            CubeRenderer.draw(gl, -0.2f, -0.2f + legMove, 0, 0.15f, 0.4f, 0.15f, r, g, b);
            CubeRenderer.draw(gl,  0.2f, -0.2f - legMove, 0, 0.15f, 0.4f, 0.15f, r, g, b);
            
            // Oeil
            CubeRenderer.draw(gl, 0, 0.2f, 0.4f, 0.2f, 0.2f, 0.1f, 0f, 0f, 0f);
            
        } else if (type == 2) {
            // CRABE
            CubeRenderer.draw(gl, 0, 0, 0, 0.8f, 0.4f, 0.6f, r, g, b); // Corps large
            // Bras
            float armMove = (float) Math.abs(Math.sin(totalTime * 0.5f)) * 0.1f;
            CubeRenderer.draw(gl, -0.5f, 0.2f + armMove, 0, 0.2f, 0.4f, 0.2f, r, g, b);
            CubeRenderer.draw(gl,  0.5f, 0.2f + armMove, 0, 0.2f, 0.4f, 0.2f, r, g, b);
            // Yeux
            CubeRenderer.draw(gl, -0.25f, 0, 0.35f, 0.15f, 0.15f, 0.1f, 0f, 0f, 0f);
            CubeRenderer.draw(gl,  0.25f, 0, 0.35f, 0.15f, 0.15f, 0.1f, 0f, 0f, 0f);

        } else {
            // CLASSIQUE
            CubeRenderer.draw(gl, 0, 0, 0, 0.7f, 0.5f, 0.7f, r, g, b); // Corps
            // Oreilles
            CubeRenderer.draw(gl, -0.25f, 0.3f, 0, 0.15f, 0.2f, 0.15f, r, g, b);
            CubeRenderer.draw(gl,  0.25f, 0.3f, 0, 0.15f, 0.2f, 0.15f, r, g, b);
            // Pieds
            float legKicK = (float) Math.sin(totalTime * 0.8f) * 0.2f;
            CubeRenderer.draw(gl, -0.25f, -0.3f, legKicK, 0.2f, 0.2f, 0.4f, r, g, b);
            CubeRenderer.draw(gl,  0.25f, -0.3f, -legKicK, 0.2f, 0.2f, 0.4f, r, g, b);
            // Yeux
            CubeRenderer.draw(gl, -0.2f, 0, 0.4f, 0.15f, 0.15f, 0.1f, 0f, 0f, 0f);
            CubeRenderer.draw(gl,  0.2f, 0, 0.4f, 0.15f, 0.15f, 0.1f, 0f, 0f, 0f);
        }
        

        gl.glPopMatrix();
    }
}