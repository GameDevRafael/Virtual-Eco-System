package tools;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

/**
 * Esta classe representa um sistema de partículas que ajuda a aumentar o realismo do jogo e a sua interatividade.
 * Estas partículas são usadas para representar efeitos visuais como movimento do jogador para o distinguir dos
 * restantes blobs, podem ser sangue quando um blob morre ou até mesmo flocos de neve.
 */
public class ParticleSystem extends Mover {
    private List<Particle> particles;

    private final int particleColor;
    private final float lifetime;
    private final float particleRadius;


    public ParticleSystem(PVector pos, PVector velocity, float mass, float radius, int color, float lifetime) {
        super(pos, velocity, mass);
        this.particleRadius = radius;
        this.particleColor = color;
        this.lifetime = lifetime;
        this.particles = new ArrayList<>();
    }

    @Override
    public void move(float dt) {
        List<Particle> aliveParticles = new ArrayList<>();

        for (Particle particle : particles) {
            particle.move(dt);
            if (!particle.isDead()) {
                aliveParticles.add(particle);
            }
        }

        particles = aliveParticles;
    }

    /**
     * Adiciona uma partícula ao sistema de partículas com uma velocidade e posição aleatória. Para garantir uma
     * maior aleatoriedade, a velocidade é calculada com base num ângulo aleatório e a posição é calculada com base
     * numa posição aleatória dentro de um raio de 0.1f.
     */
    public void addParticle() {
        float angle = (float)(Math.random() * Math.PI * 2);
        float velocity = (float)(Math.random() * 2 + 1);

        float velX = (float)Math.cos(angle) * velocity;
        float velY = (float)Math.sin(angle) * velocity;

        float offsetX = (float)(Math.random() - 0.5) * 0.1f;
        float offsetY = (float)(Math.random() - 0.5) * 0.1f;

        PVector particlePos = new PVector(position.x + offsetX, position.y + offsetY);
        PVector particleVel = new PVector(velX, velY);

        particles.add(new Particle(particlePos, particleVel, particleRadius, particleColor, lifetime));
    }

    public void display(PApplet p, SubPlot plt) {
        for (Particle particle : particles) {
            particle.display(p, plt);
        }
    }

    public boolean isAlive() {
        return !particles.isEmpty();
    }
}
