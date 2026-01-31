package com.spaceinvaders.model;

import com.jogamp.opengl.GL2;
import com.spaceinvaders.view.CubeRenderer;
import java.util.Random;

public class Particle extends GameObject {

    private float speedX, speedY, speedZ;
    private float gravity = 0.005f; // Gravité
    private float life = 0.8f; // Vie de la particule
    private float r, g, b;

    private static Random rand = new Random();

    public Particle(float x, float y, float z, float r, float g, float b) {
        super(x, y, z, 0.1f, 0.1f, 0.1f);
        this.r = r;
        this.g = g;
        this.b = b;

        // Explosion : Vitesse aléatoire dans toutes les directions
        this.speedX = (rand.nextFloat() - 0.5f) * 0.3f;
        this.speedY = (rand.nextFloat() - 0.5f) * 0.3f;
        this.speedZ = (rand.nextFloat() - 0.5f) * 0.3f;
    }

    @Override
    public void update() {
        // Mouvement
        x += speedX;
        y += speedY;
        z += speedZ;

        // Physique
        speedY -= gravity;
        life -= 0.03f;

        // Si la vie est finie ou si ça tombe trop bas on tue la particule
        if (life <= 0 || y < -10.0f) {
            isAlive = false;
        }
    }

    @Override
    public void render(GL2 gl) {
        // On utilise la méthode avec transparence (alpha = life)
        CubeRenderer.draw(gl, x, y, z, width, height, depth, r, g, b, life);
    }
}