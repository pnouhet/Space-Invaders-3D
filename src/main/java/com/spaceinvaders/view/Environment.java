package com.spaceinvaders.view;

import com.jogamp.opengl.GL2;

public class Environment {

    private static float zOffset = 0.0f;
    private static float speed = 0.1f; 
    private static float gridSize = 5.0f; 

    public static void update() {
        zOffset += speed;
        if (zOffset > gridSize) {
            zOffset -= gridSize;
        }
    }

    public static void render(GL2 gl) {
        drawCurvedGrid(gl);
    }

    private static void drawCurvedGrid(GL2 gl) {
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glLineWidth(1.5f); 
        
        gl.glColor3f(0.4f, 0.0f, 0.8f);

        float startZ = 20.0f;   
        float endZ = -80.0f;    
        float startX = -30.0f;
        float endX = 30.0f;
        
        // --- Lignes Horizontales ---
        for (float x = startX; x <= endX; x += gridSize) {
            gl.glBegin(GL2.GL_LINE_STRIP);
            for (float z = startZ; z >= endZ; z -= gridSize) {
                
                // Calcul de la position réelle avec le défilement
                float currentZ = z + zOffset; 
                
                float curveY = getCurveY(x, currentZ);
                
                // Gestion de la transparence
                float alpha = getAlpha(currentZ);
                gl.glColor4f(0.4f * alpha, 0.0f, 0.8f * alpha, alpha);

                gl.glVertex3f(x, curveY, currentZ);
            }
            gl.glEnd();
        }

        // --- Lignes Verticales ---
        for (float z = startZ; z >= endZ; z -= gridSize) {
            float currentZ = z + zOffset;
            
            gl.glBegin(GL2.GL_LINE_STRIP);
            for (float x = startX; x <= endX; x += gridSize) { 
                
                float curveY = getCurveY(x, currentZ);
                float alpha = getAlpha(currentZ);
                gl.glColor4f(0.4f * alpha, 0.0f, 0.8f * alpha, alpha);
                
                gl.glVertex3f(x, curveY, currentZ);
            }
            gl.glEnd();
        }
    }

    // Fonction utilitaire pour calculer la courbe
    private static float getCurveY(float x, float z) {
        return -2.0f - (z * z * 0.001f) - (x * x * 0.01f);
    }

    // Fonction utilitaire pour le brouillard
    private static float getAlpha(float z) {
        float distance = Math.abs(z);
        float alpha = 1.0f;
        if (distance > 40.0f) {
            alpha = 1.0f - ((distance - 40.0f) / 40.0f);
        }
        return Math.max(0.0f, alpha);
    }
}