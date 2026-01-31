package com.spaceinvaders.model;

import com.jogamp.opengl.GL2;
import com.spaceinvaders.engine.InputHandler;
import com.spaceinvaders.view.CubeRenderer;

public class PlayerShip extends GameObject {

    private float speed = 0.2f;
    private InputHandler input;
    private float xLimit = 8.0f;
    
    // Rotation légère du vaisseau
    private float tiltAngle = 0.0f;
    private float maxTilt = 20.0f;
    private float tiltSpeed = 2.0f;

    // Tir
    private long lastShotTime = 0;
    private long shotCooldown = 500;

    // Animation de flottement (Idle)
    private float hoverTime = 0.0f;   // Compteur de temps
    private float hoverOffset = 0.0f; // La hauteur visuelle

    public PlayerShip(float x, float y, float z, InputHandler input) {
        super(x, y, z, 1.0f, 0.5f, 1.0f);
        this.input = input;
    }

    @Override
    public void update() {
        if (input == null) return;

        // --- Gestion du Mouvement Latéral ---
        boolean isMoving = false;
        if (input.isLeft()) {
            this.x -= speed;
            if (tiltAngle < maxTilt) tiltAngle += tiltSpeed;
            isMoving = true;
        }
        if (input.isRight()) {
            this.x += speed;
            if (tiltAngle > -maxTilt) tiltAngle -= tiltSpeed;
            isMoving = true;
        }
        if (!isMoving) {
            if (tiltAngle > 0.5f) tiltAngle -= tiltSpeed;
            else if (tiltAngle < -0.5f) tiltAngle += tiltSpeed;
            else tiltAngle = 0;
        }

        // Limites écran
        if (this.x < -xLimit) this.x = -xLimit;
        if (this.x > xLimit) this.x = xLimit;

        // --- Calcul du flottement ---
        hoverTime += 0.05f; // Vitesse de l'animation
        // Math.sin renvoie une valeur entre -1 et 1.
        // On multiplie par 0.15f pour que le mouvement soit subtil
        hoverOffset = (float) Math.sin(hoverTime) * 0.15f;
    }

    @Override
    public void render(GL2 gl) {
        gl.glPushMatrix(); 
        
        // --- MISE EN PLACE ---
        gl.glTranslatef(x, y + hoverOffset, z);
        gl.glRotatef(tiltAngle, 0.0f, 0.0f, 1.0f);
        
        // --- DESSIN DU VAISSEAU ---
        // NEZ
        CubeRenderer.draw(gl, 0.0f, 0.0f, -0.4f, 0.4f, 0.3f, 1.0f, 0.5f, 0.5f, 0.5f);

        // CORPS
        CubeRenderer.draw(gl, 0.0f, 0.1f, 0.1f, 0.5f, 0.4f, 1.0f, 0.9f, 0.9f, 0.9f);
        
        // MOTEURS & AILES
        for (float side : new float[] {-1.0f, 1.0f}) {
            
            float engineX = side * 0.6f; // Position du moteur

            // Bloc Moteur Arrière
            CubeRenderer.draw(gl, engineX, 0.0f, 0.3f, 0.4f, 0.3f, 0.6f, 0.9f, 0.9f, 0.9f);
            
            // Bloc Moteur Avant
            CubeRenderer.draw(gl, engineX, 0.0f, -0.3f, 0.4f, 0.3f, 0.2f, 0.9f, 0.9f, 0.9f);
            
            // Bande Bleue
            CubeRenderer.draw(gl, engineX, 0.0f, -0.1f, 0.42f, 0.32f, 0.2f, 0.4f, 0.5f, 1.0f);

            // Aileron Vertical
            CubeRenderer.draw(gl, engineX, 0.3f, 0.3f, 0.1f, 0.4f, 0.4f, 0.9f, 0.9f, 0.9f);
            
            // Connecteur au corps
            float connectorX = side * 0.3f; 
            CubeRenderer.draw(gl, connectorX, 0.0f, 0.1f, 0.2f, 0.1f, 0.2f, 0.4f, 0.4f, 0.4f);

            // Ailes
            float wingX = engineX + (side * 0.3f); 
            
            // aile principale
            CubeRenderer.draw(gl, 
                wingX,   // Position X
                0.0f,    // Position Y
                0.3f,    // Position Z
                0.3f,    // Largeur
                0.1f,    // Hauteur
                0.8f,    // Profondeur
                0.9f, 0.9f, 0.9f // Couleur
            );
        }

        // RÉACTEURS ARRIÈRE
        CubeRenderer.draw(gl, -0.15f, 0.1f, 0.51f, 0.15f, 0.15f, 0.05f, 0.2f, 0.2f, 0.2f);
        CubeRenderer.draw(gl,  0.15f, 0.1f, 0.51f, 0.15f, 0.15f, 0.05f, 0.2f, 0.2f, 0.2f);

        gl.glPopMatrix(); 
    }

    public Missile tryToShoot() {
        if (input.isSpace()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShotTime > shotCooldown) {
                lastShotTime = currentTime;
                
                // Le missile part de la hauteur visuelle (y + hoverOffset) pour être synchronisé avec le vaisseau
                return new Missile(this.x, this.y + hoverOffset, this.z - 0.5f, true);
            }
        }
        return null; 
    }
}