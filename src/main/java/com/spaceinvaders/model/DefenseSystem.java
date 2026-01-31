package com.spaceinvaders.model;

import java.util.ArrayList;
import java.util.List;
import com.jogamp.opengl.GL2;

public class DefenseSystem {

    private List<DefenseTower> towers;

    public DefenseSystem() {
        towers = new ArrayList<>();
        initTowers();
    }

    private void initTowers() {
        
        float zPos = -3.5f;
        float[] xPositions = {-6.0f, -2.0f, 2.0f, 6.0f};

        for (float x : xPositions) {
            towers.add(new DefenseTower(x, 0.0f, zPos));
        }
    }

    public void render(GL2 gl) {
        for (DefenseTower tower : towers) {
            tower.render(gl);
        }
    }

    public List<DefenseTower> getTowers() {
        return towers;
    }
}