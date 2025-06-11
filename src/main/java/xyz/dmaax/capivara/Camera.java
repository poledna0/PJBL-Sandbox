package xyz.dmaax.capivara;

import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;

public class Camera {
    private float cameraX = 0f;
    private float cameraY = 2.0f;  // Um pouco acima do chão
    private float cameraZ = 5f;
    private float movimentohorizontal = 0f;
    private float movimentovertical = 0f;

    public void mover(float dx, float dy, float dz) {
        cameraX += dx;
        cameraY += dy;
        cameraZ += dz;
    }

    public void rotacionar(double dYaw, double dPitch) {
        this.movimentohorizontal += dYaw;
        this.movimentovertical += dPitch;

        // evita quebrara cbc
        if (movimentovertical > 90.0f)
            movimentovertical = 90.0f;
        if (movimentovertical < -90.0f)
            movimentovertical = -90.0f;
    }

    public void aplicarTransformacao() {
        // mover o mundo é msm coisa q mover a camera, ent faz aq
        glRotatef(movimentovertical, 1.0f, 0.0f, 0.0f);
        glRotatef(movimentohorizontal, 0.0f, 1.0f, 0.0f);
        glTranslatef(-cameraX, -cameraY, -cameraZ);
    }

    // Getters
    public float getCameraX() { return cameraX; }
    public float getCameraY() { return cameraY; }
    public float getCameraZ() { return cameraZ; }
    public float getMovimentoHorizontal() { return movimentohorizontal; }
    public float getMovimentoVertical() { return movimentovertical; }
}
