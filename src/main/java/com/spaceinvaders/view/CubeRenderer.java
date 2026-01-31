package com.spaceinvaders.view;

import com.jogamp.opengl.GL2;

public class CubeRenderer {

    // --- MÉTHODE 1 Sans Alpha ---
    // Cette méthode est appelée par le Vaisseau, les Ennemis, etc. Elle redirige vers la méthode 2 en mettant l'alpha à 1.0f 
    public static void draw(GL2 gl, float x, float y, float z, float width, float height, float depth, float r, float g, float b) {
        // On appelle la version complète avec alpha = 1.0f
        draw(gl, x, y, z, width, height, depth, r, g, b, 1.0f);
    }


    // MÉTHODE 2 Avec Alpha
    // Cette méthode est appelée par les Tours de Défense (transparence) et par la méthode 1 ci-dessus.
    public static void draw(GL2 gl, float x, float y, float z, float width, float height, float depth, float r, float g, float b, float alpha) {
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        
        // Alpha
        gl.glColor4f(r, g, b, alpha); 

        gl.glBegin(GL2.GL_QUADS);
        
        float w = width / 2;
        float h = height / 2;
        float d = depth / 2;

        // Face Avant
        gl.glVertex3f(-w, -h, d);
        gl.glVertex3f( w, -h, d);
        gl.glVertex3f( w,  h, d);
        gl.glVertex3f(-w,  h, d);

        // Face Arrière
        gl.glVertex3f(-w, -h, -d);
        gl.glVertex3f(-w,  h, -d);
        gl.glVertex3f( w,  h, -d);
        gl.glVertex3f( w, -h, -d);

        // Face Gauche
        gl.glVertex3f(-w, -h, -d);
        gl.glVertex3f(-w, -h,  d);
        gl.glVertex3f(-w,  h,  d);
        gl.glVertex3f(-w,  h, -d);

        // Face Droite
        gl.glVertex3f( w, -h, -d);
        gl.glVertex3f( w,  h, -d);
        gl.glVertex3f( w,  h,  d);
        gl.glVertex3f( w, -h,  d);

        // Face Haut
        gl.glVertex3f(-w,  h, -d);
        gl.glVertex3f(-w,  h,  d);
        gl.glVertex3f( w,  h,  d);
        gl.glVertex3f( w,  h, -d);

        // Face Bas
        gl.glVertex3f(-w, -h, -d);
        gl.glVertex3f( w, -h, -d);
        gl.glVertex3f( w, -h,  d);
        gl.glVertex3f(-w, -h,  d);

        gl.glEnd();
        gl.glPopMatrix();
    }
}