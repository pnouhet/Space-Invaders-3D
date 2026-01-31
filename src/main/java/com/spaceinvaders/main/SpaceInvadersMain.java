package com.spaceinvaders.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;

import com.spaceinvaders.engine.InputHandler;
import com.spaceinvaders.engine.SoundManager;
import com.spaceinvaders.model.AlienEnemy;
import com.spaceinvaders.model.DefenseSystem;
import com.spaceinvaders.model.DefenseTower;
import com.spaceinvaders.model.EnemyWave;
import com.spaceinvaders.model.Missile;
import com.spaceinvaders.model.Particle;
import com.spaceinvaders.model.PlayerShip;
import com.spaceinvaders.view.Environment;

public class SpaceInvadersMain implements GLEventListener {

    private static String TITLE = "Space Invaders 3D - PN";
    private static int CANVAS_WIDTH = 1280;
    private static int CANVAS_HEIGHT = 720;

    private FPSAnimator animator;
    private GLU glu;
    private float cameraX = 0.0f;
    
    private int score = 0;
    private TextRenderer textRenderer;

    // --- ENTITÉS ---
    private PlayerShip player;
    private InputHandler inputHandler;
    private List<Missile> missiles;
    private EnemyWave wave;
    private DefenseSystem defenseSystem;
    private List<Particle> particles;
    
    // --- GESTION DES ÉTATS ---
    private enum GameState {
        MENU,
        PLAYING,
        PAUSED,
        GAME_OVER
    }
    
    private GameState currentState = GameState.MENU; 
    
    // Anti-rebond pour la touche ECHAP
    private boolean wasEscPressed = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SpaceInvadersMain().setup());
    }

    public void setup() {
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        // --- ANTI-ALIASING ---
        capabilities.setSampleBuffers(true);
        capabilities.setNumSamples(4);
        
        GLCanvas glCanvas = new GLCanvas(capabilities);
        glCanvas.addGLEventListener(this);
        glCanvas.setSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        
        inputHandler = new InputHandler();
        glCanvas.addKeyListener(inputHandler); 
        glCanvas.setFocusable(true); 
        glCanvas.requestFocus();

        animator = new FPSAnimator(glCanvas, 60);

        JFrame frame = new JFrame(TITLE);
        frame.getContentPane().add(glCanvas);
        frame.setSize(frame.getContentPane().getPreferredSize());
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Thread(() -> {
                    if (animator.isStarted()) animator.stop();
                    System.exit(0);
                }).start();
            }
        });

        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();

        gl.glClearColor(0.0f, 0.0f, 0.1f, 1.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        
        // OPTIMISATION
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glCullFace(GL2.GL_BACK);
        
        textRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 36));
        
        // Initialisation
        currentState = GameState.MENU;
        SoundManager.playMusic("spaceinvaders_maintheme.wav");
        
        restartGameData(); 
    }
    
    private void restartGameData() {
        missiles = new ArrayList<>();
        particles = new ArrayList<>();
        player = new PlayerShip(0.0f, 0.0f, 0.0f, inputHandler);
        wave = new EnemyWave();
        defenseSystem = new DefenseSystem();
        score = 0;
        cameraX = 0.0f;
    }

    @Override
    public void dispose(GLAutoDrawable drawable) { }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        int width = drawable.getSurfaceWidth();
        int height = drawable.getSurfaceHeight();
        
        // Configuration de la transparence
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        // --- MACHINE À ÉTATS ---
        switch (currentState) {
            case MENU:
                updateMenuCamera(); 
                renderGameScene(gl);
                
                drawOverlay(gl, width, height, 0.85f);
                
                renderMenuUI(width, height);
                break;

            case PLAYING:
                updateGameCamera(); 
                updateGameLogic();  
                renderGameScene(gl); 
                renderGameUI(width, height);
                handlePauseInput(); 
                break;

            case PAUSED:
                updateGameCamera(); 
                renderGameScene(gl); 
                
                drawOverlay(gl, width, height, 0.7f);
                
                renderPauseUI(width, height);
                handlePauseInput(); 
                break;

            case GAME_OVER:
                updateGameCamera();
                renderGameScene(gl);
                
                drawOverlay(gl, width, height, 0.7f);
                
                renderGameOverUI(width, height);
                
                if (inputHandler.isEnter()) {
                    restartGameData();
                    currentState = GameState.PLAYING;
                    SoundManager.playMusic("spaceinvaders_maintheme.wav");
                }
                break;
        }
    }

    // --- LOGIQUE CAMÉRA ---

    private void updateGameCamera() {
        float targetX = player.getX();
        float smoothness = 0.1f; 
        cameraX = cameraX + (targetX - cameraX) * smoothness;

        float camY = player.getY() + 4.0f;
        float camZ = player.getZ() + 8.0f; 
        
        glu.gluLookAt(cameraX, camY, camZ, cameraX, player.getY(), -5.0f, 0.0, 1.0, 0.0);
    }
    
    private void updateMenuCamera() {
        glu.gluLookAt(0, 5, 10, 0, 0, -10, 0, 1, 0);
        Environment.update(); 
    }

    // --- LOGIQUE JEU & RENDU SCÈNE ---

    private void renderGameScene(GL2 gl) {
        Environment.render(gl);
        defenseSystem.render(gl);
        player.render(gl);       
        wave.render(gl);          

        for (Missile m : missiles) {
             if (m.isAlive()) m.render(gl);
        }
        
        for (Particle p : particles) {
            p.render(gl);
        }
    }

    private void updateGameLogic() {
        Environment.update();
        player.update();
        wave.update(missiles);
        
        Missile newMissile = player.tryToShoot();
        if (newMissile != null) {
            missiles.add(newMissile);
            SoundManager.playSound("spaceinvaders_shootsoundfx.wav");
        }

        for (int i = 0; i < missiles.size(); i++) {
            Missile m = missiles.get(i);
            m.update();
            if (!m.isAlive()) {
                missiles.remove(i);
                i--;
            }
        }
        
        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);
            p.update();
            if (!p.isAlive()) {
                particles.remove(i);
                i--;
            }
        }

        checkCollisions();
    }

    private void handlePauseInput() {
        if (inputHandler.isEscape()) {
            if (!wasEscPressed) {
                if (currentState == GameState.PLAYING) {
                    currentState = GameState.PAUSED;
                } else if (currentState == GameState.PAUSED) {
                    currentState = GameState.PLAYING;
                }
                wasEscPressed = true; 
            }
        } else {
            wasEscPressed = false; 
        }
    }

    private void checkCollisions() {
        for (Missile m : missiles) {
            if (!m.isAlive()) continue;

            if (m.isPlayerMissile()) {
                for (AlienEnemy alien : wave.getAliens()) {
                    if (alien.isAlive() && m.isColliding(alien)) {
                        alien.setAlive(false);
                        m.setAlive(false);
                        score += 100;
                        SoundManager.playSound("spaceinvaders_explodesoundfx.wav");
                        createExplosion(alien.getX(), alien.getY(), alien.getZ());
                        break;
                    }
                }
                if (m.isAlive()) {
                    for (DefenseTower tower : defenseSystem.getTowers()) {
                        if (tower.isAlive() && m.isColliding(tower)) {
                            m.setAlive(false);
                            break;
                        }
                    }
                }
            } else {
                if (m.isColliding(player)) {
                    m.setAlive(false);
                    triggerGameOver();
                }
                if (m.isAlive()) {
                    for (DefenseTower tower : defenseSystem.getTowers()) {
                        if (tower.isAlive() && m.isColliding(tower)) {
                            m.setAlive(false);
                            tower.takeDamage();
                            break;
                        }
                    }
                }
            }
        }
        
        for (AlienEnemy alien : wave.getAliens()) {
            if (!alien.isAlive()) continue;
            if (alien.isColliding(player)) {
                triggerGameOver();
            }
            for (DefenseTower tower : defenseSystem.getTowers()) {
                if (tower.isAlive() && alien.isColliding(tower)) {
                    triggerGameOver();
                }
            }
        }
        
        boolean allDead = true;
        for (AlienEnemy alien : wave.getAliens()) {
            if (alien.isAlive()) {
                allDead = false;
                break;
            }
        }
        if (allDead) {
            System.out.println("VICTOIRE !");
            restartGameData(); 
        }
    }

    private void triggerGameOver() {
        currentState = GameState.GAME_OVER;
        SoundManager.stopMusic();
        SoundManager.playSound("spaceinvaders_gameoversoundfx.wav");
    }
    
    // --- INTERFACES ---

    private void renderMenuUI(int width, int height) {
        textRenderer.beginRendering(width, height);
        textRenderer.setColor(Color.CYAN);
        drawCenteredText("SPACE INVADERS 3D", width, height, 100, 1.0f);
        
        textRenderer.setColor(Color.LIGHT_GRAY);
        drawCenteredText("Pierre Nouhet", width, height, 50, 1.0f);
        
        textRenderer.setColor(Color.WHITE);
        drawCenteredText("Appuyez sur ENTRÉE pour commencer", width, height, -50, 1.0f);
        
        if (inputHandler.isEnter()) {
            restartGameData();
            currentState = GameState.PLAYING;
        }
        textRenderer.endRendering();
    }

    private void renderGameUI(int width, int height) {
        textRenderer.beginRendering(width, height);
        textRenderer.setColor(Color.WHITE);
        textRenderer.draw("SCORE: " + score, 20, height - 50);
        textRenderer.endRendering();
    }

    private void renderPauseUI(int width, int height) {
        textRenderer.beginRendering(width, height);
        textRenderer.setColor(Color.YELLOW);
        drawCenteredText("PAUSE", width, height, 50, 1.0f);
        
        textRenderer.setColor(Color.WHITE);
        drawCenteredText("Appuyez sur ECHAP pour reprendre", width, height, -20, 1.0f);
        textRenderer.endRendering();
    }

    private void renderGameOverUI(int width, int height) {
        textRenderer.beginRendering(width, height);
        textRenderer.setColor(Color.RED);
        drawCenteredText("GAME OVER", width, height, 50, 1.0f);
        textRenderer.setColor(Color.WHITE);
        drawCenteredText("Appuyez sur la touche ENTRÉE", width, height, -20, 1.0f);
        drawCenteredText("pour rejouer", width, height, -60, 1.0f);
        textRenderer.endRendering();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        if (height <= 0) height = 1;
        float aspect = (float) width / height;
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0, aspect, 0.1, 100.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    
    private void createExplosion(float x, float y, float z) {
        for (int i = 0; i < 15; i++) {
            particles.add(new Particle(x, y, z, 1.0f, 0.0f, 0.6f));
        }
    }
    
    private void drawCenteredText(String text, int width, int height, int yOffset, float scale) {
        int textWidth = (int) textRenderer.getBounds(text).getWidth();
        int x = (width / 2) - (textWidth / 2);
        int y = (height / 2) + yOffset;
        textRenderer.draw(text, x, y);
    }
    
    //Méthode overlay pour les states machines (menu, pause, gameover)
    private void drawOverlay(GL2 gl, int width, int height, float alpha) {
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        glu.gluOrtho2D(0, width, 0, height);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_LIGHTING); 
        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_BLEND); 

        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor4f(0.0f, 0.0f, 0.0f, alpha);

        gl.glBegin(GL2.GL_QUADS);
            gl.glVertex2f(0, 0);
            gl.glVertex2f(width, 0);
            gl.glVertex2f(width, height);
            gl.glVertex2f(0, height);
        gl.glEnd();

        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDisable(GL2.GL_BLEND);
        
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPopMatrix();
    }
}