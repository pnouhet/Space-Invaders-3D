package com.spaceinvaders.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jogamp.opengl.GL2;

public class EnemyWave {

    private List<AlienEnemy> aliens;
    
    // Paramètres de mouvement de la vague d'ennemis
    private float moveSpeed = 0.02f;
    private float moveDirection = 1.0f; // 1 = Droite, -1 = Gauche
    private float xLimit = 8.0f; // Limite du bord de l'écran
    private float descentStep = 1.0f; // De combien ils avancent quand ils touchent le bord de l'écran
    
    // Gestion des tirs ennemis
    private Random random = new Random();
    private int shootChance = 60; // 1 chance sur 60 par frame (~1 tir par sec)

    public EnemyWave() {
        aliens = new ArrayList<>();
        initWave();
    }

    private void initWave() {
        // On crée 3 rangées de 11 ennemis
        // Espacement entre les ennemis
        float gapX = 1.0f; 
        float gapZ = 1.0f;
        
        // Point de départ Z = -20
        float startX = -((11 * gapX) / 2); // Centrer la vague d'ennemis sur X
        float startZ = -20.0f; 

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 11; col++) {
                
                float x = startX + (col * gapX);
                float y = 0.0f; 
                float z = startZ - (row * gapZ); // Reculer chaque ranger d'ennemis
                
                // Type 1 (Haut), 2 (Milieu), 3 (Bas)
                int type = row + 1; 

                aliens.add(new AlienEnemy(x, y, z, type));
            }
        }
    }

    public void update(List<Missile> activeMissiles) {
        boolean hitEdge = false;

        // Déplacement Latéral
        for (AlienEnemy alien : aliens) {
            if (!alien.isAlive()) continue;

            alien.update();
            alien.x += moveSpeed * moveDirection;

            // Vérification si un alien touche le bord
            if (moveDirection > 0 && alien.x > xLimit) hitEdge = true;
            if (moveDirection < 0 && alien.x < -xLimit) hitEdge = true;
        }

        // 2. Gestion du demi-tour et descente
        if (hitEdge) {
            moveDirection *= -1; // On change de sens
            for (AlienEnemy alien : aliens) {
                alien.z += descentStep;
                // Debug pour que l'ennemi ne reste pas coincé dans le mur
                alien.x += moveSpeed * moveDirection; 
            }
        }

        // Gestion du Tir Ennemi aléatoire
        if (random.nextInt(shootChance) == 0 && !aliens.isEmpty()) {
            shootMissile(activeMissiles);
        }
    }

    private void shootMissile(List<Missile> activeMissiles) {
        List<AlienEnemy> livingAliens = new ArrayList<>();
        for (AlienEnemy a : aliens) {
            if (a.isAlive()) livingAliens.add(a);
        }

        if (!livingAliens.isEmpty()) {
            AlienEnemy shooter = livingAliens.get(random.nextInt(livingAliens.size()));
            
            // Création d'un missile alien, isPlayerMissile = false
            Missile m = new Missile(shooter.x, shooter.y, shooter.z + 0.5f, false);
            activeMissiles.add(m);
        }
    }

    public void render(GL2 gl) {
        for (AlienEnemy alien : aliens) {
            alien.render(gl);
        }
    }

    // Gérer les collisions
    public List<AlienEnemy> getAliens() {
        return aliens;
    }
}