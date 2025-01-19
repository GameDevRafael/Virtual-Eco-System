package tools;

import processing.core.PVector;

/**
 * Classe abstrata que define as físicas dos Blobs e Partículas. Esta classe é responsável por mover os objetos com base
 * na força aplicada, na sua massa, na sua posição, velocidade e aceleração.

 */
public abstract class Mover {
    protected PVector position;
    protected PVector velocity;
    private final PVector acc;

    protected float mass;

    public Mover(PVector pos, PVector velocity, float mass) {
        this.position = pos.copy();
        this.velocity = velocity;
        this.mass = mass;
        acc = new PVector();
    }

    public void move(float dt, PVector force) {
        PVector acc1 = PVector.div(force, mass);
        velocity.add(PVector.mult(acc1, dt));
        position.add(PVector.mult(velocity, dt));
    }

    public void move(float dt) {
        velocity.add(PVector.mult(acc, dt));
        position.add(PVector.mult(velocity, dt));
        acc.mult(0);
    }

    public void moveWithoutAcc(float dt, PVector force) {
        velocity.add(PVector.mult(force, dt));
        position.add(PVector.mult(velocity, dt));
    }

    public PVector getPosition() {
        return position;
    }

    public PVector getVelocity() {
        return velocity;
    }

    public void setPosition(PVector mousePosition) {
        position = mousePosition;
    }
}
