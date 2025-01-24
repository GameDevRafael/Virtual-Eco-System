package core;

import model.entity.animal.Blob;
import model.entity.animal.BlobPredator;
import model.entity.animal.BlobVillager;
import model.entity.plant.LeafTree;
import model.entity.resource.Fruit;
import processing.core.PApplet;
import processing.core.PVector;
import tools.LSystem;
import tools.ParticleSystem;
import tools.SubPlot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Esta classe tem como objetivo controlar a lógica do jogo, como a passagem do tempo (incluindo a alteração entre os
 * mapas nas passagens de estações), a movimentação dos blobs e a criação de efeitos de partículas.
 */
public class Game {
    private final InteractionManager IM;
    private ParticleSystem playerSystem;
    private final GameManager GM;
    private Blob playerBlob;

    private final List<BlobVillager> blobVillagers;
    private final List<BlobPredator> blobPredators;
    private final List<Fruit>fruits;
    private final List<ParticleSystem> activeParticleSystems = new ArrayList<>();

    private long t0;
    private long t1;
    private long dayCycle = 15000;
    private final long summerDayCycle = 15000;
    private final long winterDayCycle = 10000;
    private boolean isDay = true;
    private int dayCycleCounter = 0;
    String season = "summer";

    private long beforePauseTime;
    private boolean gameWasPaused = false;
    boolean firstLaunch = true;
    boolean secondFamilyTree = false;

    int numberOfFruits = 0;
    int PARTICLE_MASS = 1;
    int PARTICLE_RADIUS = 5;


    public Game(List<BlobVillager> blobVillagers, List<BlobPredator> blobPredators,List<Fruit>fruits,
                InteractionManager IM, GameManager GM) {
        this.blobVillagers = blobVillagers;
        this.blobPredators = blobPredators;
        this.fruits=fruits;
        this.t0 = System.currentTimeMillis();
        this.IM = IM;
        this.GM=GM;
    }

    /**
     * Este método é o mais importante no nosso código inteiro, pois é responsável por atualizar o jogo a cada frame.
     * Controla o blob que representa o jogador, informações como o número do dia, a hora do ciclo de dia/noite e a
     * estação do ano.
     * Durante a noite, os blobs são movimentados para as suas casas e recuperam a sua stamina, quando de dia, os blobs
     * movimentam-se à procura de comida ou a fugir de predadores (caso sejam blobVillagers).
     * Dependendo do número do dia atual, a geração de árvores é alterada, assim como a quantidade de frutas que são
     * geradas após o primeiro dia.
     * Imprementámos um sistema de pausa que permite ao jogador pausar o jogo e continuar a partir do ponto onde parou.
     *
     * @param lSys systema de Lindenmayer que controla a geração de árvores
     * @param p PApplet
     * @param trees lista de árvores
     * @param fruits lista de frutas
     * @param plt subplot
     * @param dt tempo decorrido desde o último frame
     * @param maxNumberOfFruits número máximo de frutas que podem existir no mapa após o primeiro dia
     * @param pauseGame boolean que indica se o jogo está pausado
     * @param playerBlob blob que representa o jogador
     */
    public void update(LSystem lSys, PApplet p, List<LeafTree> trees, List<Fruit> fruits, SubPlot plt, float dt,
                       int maxNumberOfFruits, boolean pauseGame, Blob playerBlob) {
        this.playerBlob = playerBlob;
        boolean foundPlayer = false;

        for(BlobVillager blobVillager : blobVillagers){
            if (blobVillager == playerBlob) {
                foundPlayer = true;
                break;
            }
        }
        for(BlobPredator blobPredator : blobPredators){
            if (blobPredator == playerBlob) {
                foundPlayer = true;
                break;
            }
        }

        if(!foundPlayer){
            this.playerBlob = null;
        }

        if(playerBlob != null){
            createPlayerParticleEffects(p);
        } else {
            activeParticleSystems.remove(playerSystem);
        }

        p.pushStyle();
        p.textSize(19);
        p.text("Dia: " + (dayCycleCounter + 1), 15, 30);
        p.text("Estação: " + season, 15, 50);

        if (!pauseGame) {
            t1 = System.currentTimeMillis();

            if (gameWasPaused) {
                t0 = t1 - (beforePauseTime - t0);
                gameWasPaused = false;
            }

            long hora = (t1 - t0) / 1000;
            p.text("Horas: " + hora, 15, 70);
            p.stroke(255);
            p.popStyle();

            if (t1 - t0 > dayCycle) {
                changeTime();

                if (!isDay) {
                    for (BlobVillager villager : blobVillagers) {
                        villager.resetStaminaAtNight();
                    }
                    for (BlobPredator predator : blobPredators) {
                        predator.resetStaminaAtNight();
                    }
                    if (!firstLaunch) {
                        fruits.clear();
                    }
                    numberOfFruits = 0;
                    IM.reproduceBlobPredators(blobPredators, p);
                    IM.reproduceBlobVillagers(blobVillagers, p);
                } else {
                    if (numberOfFruits < maxNumberOfFruits) {
                        for (; numberOfFruits < maxNumberOfFruits; numberOfFruits++) {
                            GM.initializeFruit(p);
                        }
                    }
                }
                dayCycleCounter++;

                if (dayCycleCounter <= 3) {
                    lSys.nextGeneration();
                    for (LeafTree tree : trees) {
                        tree.scaling(1.3f);
                    }
                } else if(dayCycleCounter >= 7 && dayCycleCounter <= 10) {
                    lSys.previousGeneration(trees);
                    for (LeafTree tree : trees) {
                        tree.scaling(1/1.3f);
                    }
                } else if(dayCycleCounter >= 12 && dayCycleCounter <= 14){
                    if(!secondFamilyTree){
                        GM.initializeFruitTree(p);
                        GM.initializeFruitTree(p);
                        GM.initializeFruitTree(p);
                        GM.initializeFruitTree(p);
                        secondFamilyTree = true;
                    }
                    lSys.nextGeneration();
                    for (LeafTree tree : trees) {
                        tree.scaling(1.3f);
                    }
                }

                if (dayCycleCounter % 3 == 0 && dayCycleCounter != 0) {
                    if (season.equals("summer")) {
                        season = "winter";
                        dayCycle = winterDayCycle;
                        GM.setNumOfFruit(15);
                        updateSnow(dt, p, plt);
                    } else if (season.equals("winter")) {
                        season = "summer";
                        dayCycle = summerDayCycle;
                        GM.setNumOfFruit(30);
                        updateSnow(dt, p, plt);
                    }
                }

                t0 = t1;
            }

            moveBlobs(p, dt);
            firstLaunch = false;

            if (getIsDay()) {
                List<BlobVillager> villagers = IM.killBlob(blobVillagers, blobPredators, plt, p);
                IM.addFruitFromDeadVillagers(villagers);
            }

            IM.grabFruit(blobVillagers, fruits);
            IM.updateKillParticles(p, plt, dt);
            updateSnow(dt, p, plt);


        } else {
            beforePauseTime = t1;
            gameWasPaused = true;

            long hora = (beforePauseTime - t0) / 1000;
            p.text("Horas: " + hora, 15, 70);
            p.stroke(255);
            p.popStyle();
        }

    }

    public Blob getRandomBlobVillager(PApplet p) {
        return blobVillagers.get((int) p.random(blobVillagers.size()));
    }

    public Blob getRandomBlobPredator(PApplet p) {
        return blobPredators.get((int) p.random(blobPredators.size()));
    }


    public String getSeason() {
        return season;
    }

    /**
     * Move os blobVillagers de acordo com a presença de predadores e frutas no mapa.
     * A presença de predadores é priorizada, caso um blobVillager esteja perto de um predador, independentemente da
     * presença de frutas. Caso não haja predadores perto, o blobVillager irá apanhar frutas.
     * A maneira como os blobs sabem que têm uma entidade por perto é através do seu campo de visão.
     * Se o blobPredador for o jogador, a sua stamina é reduzida, caso contrário, a stamina é recuperada.
     *
     * @param p PApplet
     * @param dt tempo decorrido desde o último frame
     */
    public void moveVillagerBlobs(PApplet p, float dt) {
        for (BlobVillager blobVillager : blobVillagers) {
            if (blobVillager != playerBlob) {

                PVector totalForce = new PVector(0, 0);
                boolean isInDanger = false;

                for (BlobPredator blobPredator : blobPredators) {
                    float distance = PVector.dist(blobVillager.getPosition(), blobPredator.getPosition());

                    if (distance <= blobVillager.getAwareness()) {
                        isInDanger = true;
                        PVector force = blobVillager.run(blobPredator.getPosition());
                        totalForce.add(force);
                        if(blobPredator == playerBlob){
                            playerBlob.consumeStamina();
                        } else if(playerBlob != null){
                            playerBlob.recoverStamina();
                        }
                    }
                }

                if (!isInDanger) {
                    totalForce = blobVillager.wander(p);

                    for (Fruit fruit : fruits) {
                        float distance = PVector.dist(blobVillager.getPosition(), fruit.getPosition());
                        if (distance <= blobVillager.getAwareness()) {
                            totalForce = blobVillager.seek(fruit.getPosition());
                        }
                    }
                }

                blobVillager.move(dt, totalForce);
            }
        }
    }


    /**
     * A lógica de movimentação dos blobPredators é semelhante à dos blobVillagers, mas com a diferença de que os
     * blobPredators têm como objetivo perseguir os blobVillagers. Se um blobVillager estiver dentro do campo de visão
     * do blobPredator, este irá persegui-lo, caso contrário, irá vaguear pelo mapa.
     * Se o blobVillager for o player então a sua stamina é reduzida, caso contrário, a stamina é recuperada.
     * @param p PApplet
     * @param dt tempo decorrido desde o último frame
     */
    public void movePredatorBlobs(PApplet p, float dt) {
        for (BlobPredator blobPredator : blobPredators) {
            if (blobPredator != playerBlob) {

                PVector force;
                BlobVillager closestVillager = null;
                float closestDistance = Float.MAX_VALUE;

                for (BlobVillager blobVillager : blobVillagers) {
                    float distance = PVector.dist(blobVillager.getPosition(), blobPredator.getPosition());

                    if (distance <= blobPredator.getAwareness() && distance < closestDistance) {
                        closestDistance = distance;
                        closestVillager = blobVillager;
                        if(blobVillager == playerBlob){
                            playerBlob.consumeStamina();
                        } else if(playerBlob != null){
                            playerBlob.recoverStamina();
                        }
                    }
                }

                if (closestVillager != null) {
                    force = blobPredator.seek(closestVillager.getPosition());
                } else {
                    force = blobPredator.wander(p);
                }

                blobPredator.move(dt, force);
            }
        }

    }

    /**
     * Este método é responsável por mover os blobs de acordo com o tempo decorrido desde o último frame. Se for de dia,
     * os blobs movem-se à procura de comida ou a fugir de predadores, caso contrário, os blobs movem-se para as suas
     * casas.
     * @param p PApplet
     * @param dt tempo decorrido desde o último frame
     */
    public void moveBlobs(PApplet p, float dt) {
        if(isDay){
            moveVillagerBlobs(p, dt);
            movePredatorBlobs(p, dt);
        } else {
            for (BlobVillager blobVillager : blobVillagers) {
                bringVillagersHome(dt, blobVillager);
            }
            for (BlobPredator blobPredator : blobPredators) {
                bringPredatorsHome(dt, blobPredator);
            }
        }
    }

    private void bringVillagersHome(float dt, BlobVillager blobVillager) {
            PVector force = blobVillager.stopAtHome();
            blobVillager.move(dt, force);
    }

    private void bringPredatorsHome(float dt, BlobPredator blobPredator) {
            PVector force = blobPredator.stopAtHome();
            blobPredator.move(dt, force);
    }

    /**
     * Criámos um efeito de partículas que simula a queda de neve no inverno. Este efeito é criado através de várias
     * instâncias de ParticleSystem que são adicionadas a uma lista de sistemas de partículas ativas.
     * @param p PApplet
     */
    private void createSnow(PApplet p) {
        int snowCount = 15;

        for (int i = 0; i < snowCount; i++) {
            PVector position = new PVector(p.random(p.width), p.random(p.height));
            PVector velocity = new PVector((p.random(-0.1f, 0.1f)), 0);

            ParticleSystem cloudSystem = new ParticleSystem(
                    position,
                    velocity,
                    PARTICLE_MASS,
                    PARTICLE_RADIUS,
                    p.color(211, 211, 211, 128),
                    Float.MAX_VALUE
            );

            for (int j = 0; j < 100; j++) {
                cloudSystem.addParticle();
            }

            activeParticleSystems.add(cloudSystem);
        }
    }

    /**
     * Este método cria um efeito de partículas que simula a presença de jogador no mapa. Este efeito é criado através
     * de uma instância de ParticleSystem que é adicionada à lista de sistemas de partículas ativas.
     * @param p PApplet
     */
    public void createPlayerParticleEffects(PApplet p) {
        int particleCount = 100;

        if (playerBlob != null) {
            PVector position = playerBlob.getPosition();
            PVector velocity = playerBlob.getVelocity();
            playerSystem = new ParticleSystem(
                    position,
                    velocity,
                    PARTICLE_MASS,
                    PARTICLE_RADIUS,
                    p.color(255, 215, 0, 128),
                    1.0f
            );

            for (int j = 0; j < particleCount; j++) {
                playerSystem.addParticle();
            }

            activeParticleSystems.add(playerSystem);
        }
    }

    /**
     * Atualiza o efeito de partículas que simula a queda de neve no inverno. Se for verão, o efeito é
     * removido lentamente com uma chance de 10% a cada frame.
     * @param dt tempo decorrido desde o último frame
     * @param p PApplet
     * @param plt subplot
     */
    public void updateSnow(float dt, PApplet p, SubPlot plt) {
        Iterator<ParticleSystem> iterator = activeParticleSystems.iterator();

        while (iterator.hasNext()) {
            ParticleSystem cloudSystem = iterator.next();
            cloudSystem.move(dt);

            if (season.equals("summer")) {
                if (Math.random() < 0.1) {
                    iterator.remove();
                }
            }
            cloudSystem.display(p, plt);
        }

        if (season.equals("winter") && activeParticleSystems.size() < 15) {
            createSnow(p);
        }
    }


    public boolean getIsDay () {
        return isDay;
    }
    public void changeTime () {
        isDay = !isDay;
    }

    /**
     * Reiniciamos o tempo do jogo incluindo a estação do ano e a hora do dia. É chamado quando o jogador decide
     * reiniciar o jogo.
     */
    public void resetTime(LSystem lSys) {
        t0 = System.currentTimeMillis();
        t1 = System.currentTimeMillis();
        dayCycleCounter = 0;
        season = "summer";
        isDay = true;
        firstLaunch = true;
        lSys.reset();
        secondFamilyTree = false;
    }
}


