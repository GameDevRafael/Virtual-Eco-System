package tools;

import processing.core.PApplet;
import processing.core.PVector;

public class Particle extends Mover {
    private final float lifespan;
    private final int color;
    private float timer;
    private final float radius;

    public Particle(PVector pos, PVector velocity, float radius, int color, float lifespan) {
        super(pos, velocity, 0f);
        this.color = color;
        this.radius = radius;
        this.lifespan = lifespan;
        this.timer = 0;
    }

    @Override
    public void move(float dt) {
        position.x += velocity.x * dt;
        position.y += velocity.y * dt;
        timer += dt;
    }

    public boolean isDead() {
        return timer >= lifespan;
    }

    /**
     * Esta partícula é desenhada como um círculo com um raio e cor específicos. A cor da partícula é desvanecida com o
     * tempo.
     * @param p objeto PApplet para desenhar a partícula
     * @param plt objeto SubPlot para obter as coordenadas corretas
     */
    public void display(PApplet p, SubPlot plt) {
        p.pushStyle();
        float alpha = PApplet.constrain(PApplet.map(timer, 0, lifespan, 255, 0), 0, 255);
        int fadedColor = p.color(p.red(color), p.green(color), p.blue(color), alpha);
        p.fill(fadedColor);

        float[] pp = plt.getPixelCoord(position.x, position.y);
        float[] r = plt.getPixelCoord(radius, radius);
        p.noStroke();
        p.circle(pp[0], pp[1], r[0] * 2);
        p.popStyle();
    }
}