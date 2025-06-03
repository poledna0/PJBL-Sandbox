package xyz.dmaax.engine;

import org.joml.Vector3f;

public abstract class GameObject {
    protected Vector3f position;
    protected boolean visible;

    public GameObject() {
        this.position = new Vector3f(0, 0, 0);
        this.visible = true;
    }

    public GameObject(Vector3f position) {
        this.position = new Vector3f(position);
        this.visible = true;
    }

    // Req. 4: Métodos abstratos
    public abstract void render();
    public abstract void update(float deltaTime);

    // Getters e Setters (Req. 1: Encapsulamento)
    public Vector3f getPosition() { return new Vector3f(position); }
    public void setPosition(Vector3f position) { this.position.set(position); }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
}
