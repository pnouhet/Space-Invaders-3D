package com.spaceinvaders.model;

import com.jogamp.opengl.GL2;

public abstract class GameObject {
    
    // Position
    protected float x, y, z;
    
    // Dimensions
    protected float width, height, depth;
    
    protected boolean isAlive = true;

    public GameObject(float x, float y, float z, float width, float height, float depth) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }
    
    public boolean isColliding(GameObject other) {
        // Si un des deux objets est déjà mort alors pas de collision
        if (!this.isAlive || !other.isAlive) return false;

        // On calcule les moitiés de taille pour savoir où sont les bords
        float w1 = this.width / 2;
        float h1 = this.height / 2;
        float d1 = this.depth / 2;

        float w2 = other.width / 2;
        float h2 = other.height / 2;
        float d2 = other.depth / 2;

        // Test de chevauchement sur les 3 axes (X, Y, Z)
        boolean collisionX = Math.abs(this.x - other.x) < (w1 + w2);
        boolean collisionY = Math.abs(this.y - other.y) < (h1 + h2);
        boolean collisionZ = Math.abs(this.z - other.z) < (d1 + d2);

        return collisionX && collisionY && collisionZ;
    }

    public abstract void update();

    public abstract void render(GL2 gl);

    // --- Getters ---
    public float getX() { return x; }
    public float getY() { return y; }
    public float getZ() { return z; }
    public boolean isAlive() { return isAlive; }
    public void setAlive(boolean alive) { isAlive = alive; }
}