package model.entity.animal;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import tools.Mover;
import tools.SubPlot;

public abstract class Blob extends Mover {
    PVector wanderTarget;
    private final PImage blobImage;
    private final PImage blobHouse;
    private final PVector house;
    private final PVector playerForce;

    float maxX = 1000;
    float maxY = 800;
    float wanderRadius = 100;
    float wanderDistance = 150;
    float wanderJitter = 10;

    float maxVel;
    private float maxVelInit;
    float stamina;
    float staminaInit;
    float awareness;

    private long t0;
    private long t1;

    public Blob(PVector pos, PVector vel, float mass, PImage blobImage, float awareness, float stamina,
                float maxVelocity, PVector house, PImage houseImage) {
        super(pos, vel, mass);
        this.blobImage = blobImage;
        this.wanderTarget = new PVector();
        this.awareness = awareness;
        this.house = house;
        this.staminaInit = stamina;
        this.stamina = staminaInit;
        this.maxVelInit = maxVelocity;
        this.maxVel = maxVelInit;
        this.blobHouse = houseImage;
        this.playerForce = new PVector();
    }

    public float getMaxVelocity() {
        return maxVelInit;
    }

    public abstract void displayInfo(PApplet p, SubPlot subPlotGame);

    public float getStamina() {
        return stamina;
    }

    public float getStaminaInit() {
        return staminaInit;
    }

    /**
     * O método mutate procura saber se o Blob irá sofrer uma mutação. Primeiro averigua saber se irá ocorrer uma
     * mutação. Para tal, é necessário que o valor aleatório gerado esteja abaixo de 0.5. Caso se verifique, é
     * necessário saber se a mutação será positiva ou negativa. Para tal, é gerado um novo valor aleatório entre 0 e 1.
     * Se o valor gerado for inferior a 0.5, a mutação será positiva, caso contrário, será negativa.
     * @param p objeto da classe PApplet
     */
    public void mutate(PApplet p) {
        if (p.random(0, 1) <= 0.5f) {
            if (p.random(0, 1) <= 0.5f) {
                mutatePositive(p);
            } else {
                mutateNegative(p);
            }
        }
        maxVel = maxVelInit;
        stamina = staminaInit;

    }

    /**
     * O método mutatePositive é responsável por realizar mutações positivas no Blob. Para tal, é gerado um número
     * aleatório entre 1 e 4, que irá determinar o número de mutações a serem realizadas. De seguida, é gerado um novo
     * número aleatório entre 0 e 3, que irá determinar o tipo de mutação a ser realizada. Dependendo do valor, a
     * característica irá ser aumentada em 20%.
     * @param p objeto da classe PApplet
     */
    public void mutatePositive(PApplet p){
        int numberOfMutations = (int) p.random(1, 4);

        for(int n = 0; n < numberOfMutations; n++){
            int typeOfMutation = (int) p.random(0, 3);
            switch(typeOfMutation){
                case 0:
                    awareness += awareness * 0.2f;
                    break;
                case 1:
                    staminaInit += staminaInit * 0.2f;
                    break;
                case 2:
                    maxVelInit += maxVelInit * 0.2f;
                    break;
            }
        }
    }

    /**
     * O método mutateNegative é responsável por realizar mutações negativas no Blob. Para tal, é gerado um número
     * aleatório entre 1 e 4, que irá determinar o número de mutações a serem realizadas. De seguida, é gerado um novo
     * número aleatório entre 0 e 3, que irá determinar o tipo de mutação a ser realizada. Dependendo do valor, a
     * característica irá ser diminuída em 20%.
     * @param p objeto da classe PApplet
     */
    public void mutateNegative(PApplet p){
        int numberOfMutations = (int) p.random(1, 4);

        for(int n = 0; n < numberOfMutations; n++){
            int typeOfMutation = (int) p.random(0, 3);
            switch(typeOfMutation){
                case 0:
                    awareness -= awareness * 0.2f;
                    break;
                case 1:
                    staminaInit -= staminaInit * 0.2f;
                    break;
                case 2:
                    maxVelInit -= maxVelInit * 0.2f;
                    break;
            }
        }
    }

    public abstract PVector seek(PVector target);

    /**
     * Método responsável por desenhar o Blob no ecrã.
     * Dependendo da direção em que o Blob se está a mover, a imagem é espelhada para corresponder corretamente à
     * direção do movimento.
     * Também desenha o campo de visao do Blob para termos uma noção do quão longe ele consegue ver.
     * Por último, desenha a casa do Blob e verifica se o Blob está fora dos limites do ecrã. Caso estiver, transporta-o
     * para o lado oposto do ecrã.
     * @param p
     * @param plt
     */
    public void display(PApplet p, SubPlot plt) {
        p.pushStyle();
        float[] pp = plt.getPixelCoord(position.x, position.y);
        float[] hitBox = plt.getVectorCoord(getHitBox(), getHitBox());

        p.pushMatrix();
        p.translate(pp[0], pp[1]);

        if (velocity.x > 0) {
            p.scale(-1, 1);
        }

        p.image(blobImage, -hitBox[0]/2, -hitBox[1]/2, hitBox[0], hitBox[1]);
        p.popMatrix();

        p.noFill();
        p.stroke(255);
        p.strokeWeight(1);
        float[] awarenessPixelRadius = plt.getVectorCoord(awareness, 0);
        p.circle(pp[0], pp[1], awarenessPixelRadius[0] * 2);

        float[] ppHouse = plt.getPixelCoord(house.x, house.y);
        float[] hitBoxHouse = plt.getVectorCoord(getHitBox() + 10, getHitBox() + 10);
        p.image(blobHouse, ppHouse[0] - (hitBoxHouse[0]/2), ppHouse[1] - (hitBoxHouse[1]/2),
                hitBoxHouse[0], hitBoxHouse[1]);

        p.popStyle();

        double[] window = plt.getWindow();
        if (position.x > window[1]) position.x = (float) window[0];
        if (position.x < window[0]) position.x = (float) window[1];
        if (position.y > window[3]) position.y = (float) window[2];
        if (position.y < window[2]) position.y = (float) window[3];
    }

    /**
     * Para aumentar o realismo do jogo, foi implementado um sistema de energia (stamina) para os Blobs.
     * A stamina é consumida quando um predador persegue um villager dentro do seu campo de visão ou quando um villager
     * foge de um predador.
     * Esta é recuperada quando o Blob não está envolvido nestas atividades.
     */
    public void consumeStamina() {
        stamina -= 10f;
        t0 = System.currentTimeMillis();
        stamina = PApplet.constrain(stamina, 0, staminaInit);

        if (stamina == 0 && maxVel == maxVelInit) {
            maxVel *= 0.5f;
        }
    }

    /**
     * Para recuperar a stamina, é necessário que o Blob, à adição de não estar envolvido em atividades que a consomem,
     * permaneça fora das mesmas durante 2.5 segundos.
     */
    public void recoverStamina() {
        if (stamina < staminaInit) {
            t1 = System.currentTimeMillis();
            if (t1 - t0 >= 2500) {
                stamina = staminaInit;
                maxVel = maxVelInit;
            }
        }
    }

    public void resetStaminaAtNight() {
        stamina = staminaInit;
        maxVel = maxVelInit;
    }

    /**
     * Caso o jogador possua um Blob, este método é responsável por lidar com o movimento do mesmo.
     * @param key tecla de movimento pressionada que irá determinar a velocidade do blob
     * @param isPressed boolean que indica se a tecla está pressionada ou não
     */
    public void handlePlayerMovement(char key, boolean isPressed) {
        switch(Character.toLowerCase(key)) {
            case 'w':
                playerForce.y = isPressed ? maxVel : 0;
                break;
            case 's':
                playerForce.y = isPressed ? -maxVel : 0;
                break;
            case 'a':
                playerForce.x = isPressed ? -maxVel : 0;
                break;
            case 'd':
                playerForce.x = isPressed ? maxVel : 0;
                break;
        }
    }

    public PVector getPlayerMovementForce() {
        playerForce.normalize();
        playerForce.mult(maxVel);

        PVector desiredVelocity = playerForce.copy().normalize().mult(maxVel);
        return PVector.sub(desiredVelocity, velocity);
    }

    public float getAwareness(){
        return awareness;
    }

    /**
     * Move o blob para a sua casa e pára se estiver perto o suficiente da mesma.
     * @return vetor que aproxima o blob da sua casa, se já estiver perto o suficiente, devolve um vetor nulo
     */
    public PVector stopAtHome() {
        float distanceToHome = position.dist(house);

        if (distanceToHome < 10) {
            position.set(house);
            velocity.set(0, 0);
            return new PVector(0, 0);
        }

        return arrive(house);
    }

    /**
     * Este método faz com que o Blob se mova em direção a um alvo, mas desacelera à medida que se aproxima do mesmo.
     * @param target alvo para onde o Blob se deve mover
     * @return vetor que aproxima o Blob do alvo
     */
    public PVector arrive(PVector target) {
        PVector desired = PVector.sub(target, position);
        float distance = desired.mag();
        desired.normalize();

        float desiredSpeed;
        if (distance >= awareness) {
            desiredSpeed = maxVel;

        } else {
            float k = 1.0f;
            desiredSpeed = maxVel * (float)Math.pow(distance / awareness, k);
        }

        desired.mult(desiredSpeed);

        return PVector.sub(desired, velocity);
    }

    /**
     * Este método faz com que o Blob se mova aleatoriamente pelo mapa e adiciona um pequeno ruído ao movimento para
     * torná-lo mais natural.
     * @param p objeto da classe PApplet
     * @return vetor que aproxima o Blob do alvo
     */
    public PVector wander(PApplet p) {
        recoverStamina();

        wanderTarget.add(new PVector(
                p.random(-1, 1) * wanderJitter,
                p.random(-1, 1) * wanderJitter
        ));

        wanderTarget.normalize();
        wanderTarget.mult(wanderRadius);

        PVector ahead = velocity.copy();
        ahead.normalize();
        ahead.mult(wanderDistance);

        PVector targetWorld = PVector.add(position, ahead);
        targetWorld.add(wanderTarget);

        return arrive(targetWorld);
    }

    public double getHitBox() {
        return mass * 30;
    }

}