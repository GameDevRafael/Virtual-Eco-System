package core;

import model.entity.animal.BlobPredator;
import model.entity.animal.BlobVillager;
import model.entity.resource.Fruit;
import model.factory.EntityFactory;
import model.world.WorldMap;
import processing.core.PApplet;
import processing.core.PVector;
import tools.ParticleSystem;
import tools.SubPlot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Esta classe organiza todas as interações entre os diferentes tipos de entidades do jogo. Tais interações incluem as
 * reproduções entre blobs. Se um blobVillager tiver pelo menos 2 frutas, ele pode se reproduzir e criar 2 novos blobs.
 * Se um blobPredator tiver pelo menos 2 blobs, ele pode se reproduzir uma vez. Todos os blobs reproduzidos são mutados
 * para terem características diferentes mas que originem dos pais.
 * Esta classe também organiza a morte de blobs (e os sistemas de particulas que originam dessa morte) e a apanha de
 * frutas por parte dos blobsVillagers.
 */
public class InteractionManager {
    private final SubPlot plt;
    private final EntityFactory entityFactory;
    private final WorldMap worldMap;

    private final List<Fruit> fruits;
    private final List<ParticleSystem> killParticleSystem;

    private static final float PARTICLE_LIFETIME = 2.0f;
    private static final float PARTICLE_RADIUS = 3.0f;
    private static final float PARTICLE_MASS = 1.0f;

    public InteractionManager(List<Fruit> fruits, SubPlot plt, EntityFactory entityFactory, WorldMap worldMap) {
        this.fruits = fruits;
        this.plt = plt;
        this.entityFactory = entityFactory;
        this.worldMap = worldMap;
        this.killParticleSystem = new ArrayList<>();
    }

    /**
     * Este método organiza a reprodução dos blobsVillagers. Se um blobVillager tiver pelo menos 2 frutas pode
     * reproduzir-se e criar no máximo 2 novos blobs.
     * Todos os blobs reproduzidos são mutados para terem características diferentes mas que originem dos pais.
     * @param blobsVillagers lista dos blobVillagers vivos
     * @param p instância da classe PApplet
     */
    public void reproduceBlobVillagers(List<BlobVillager> blobsVillagers, PApplet p) {
        List<BlobVillager> newVillagers = new ArrayList<>();
        List<BlobVillager> villagersToRemove = new ArrayList<>();

        for (BlobVillager blobVillager : blobsVillagers) {
            boolean dontDie = true;
            int foodCount = blobVillager.getInventory().size();
            int possibleReproductions = Math.min(foodCount / 2, 2);

            for (int i = 0; i < possibleReproductions; i++) {
                newVillagers.add(entityFactory.addBlobVillager(worldMap, p, blobVillager, null));
                blobVillager.removeNumberOfFruits(2);
                dontDie = false;
            }

            foodCount = blobVillager.getInventory().size();
            if (foodCount == 0 && dontDie) {
                villagersToRemove.add(blobVillager);
                createKillEffect(blobVillager.getPosition(), p);
            }
        }

        mutateBlobVillagers(p, newVillagers);
        blobsVillagers.addAll(newVillagers);
        blobsVillagers.removeAll(villagersToRemove);
    }

    /**
     * Este método organiza a reprodução dos blobsPredators. Se um blobPredator tiver pele menos 2 blobs no seu
     * inventário, este pode reproduzir-se uma vez e criar um novo blob.
     * Todos os blobs reproduzidos são mutados para terem características diferentes mas que originem dos pais.
     * @param blobsPredators lista dos blobPredators vivos
     * @param p instância da classe PApplet
     */
    public void reproduceBlobPredators(List<BlobPredator> blobsPredators,PApplet p) {
        List<BlobPredator> newPredators = new ArrayList<>();
        List<BlobPredator> predatorsToRemove = new ArrayList<>();

        for (BlobPredator blobPredator : blobsPredators) {
            if (blobPredator.getInventory().size() >= 2 ) {
                newPredators.add(entityFactory.addBlobPredator(worldMap, p, blobPredator, null));
                blobPredator.removeNumberOfBlobs(2);

            } else if (blobPredator.getInventory().size() == 1) {
                blobPredator.removeNumberOfBlobs(1);
            } else if (blobPredator.getInventory().isEmpty()) {
                predatorsToRemove.add(blobPredator);
                createKillEffect(blobPredator.getPosition(), p);
            }
        }

        mutateBlobPredators(p,newPredators);

        blobsPredators.addAll(newPredators);
        blobsPredators.removeAll(predatorsToRemove);
    }

    private void mutateBlobPredators(PApplet p,List<BlobPredator> newPredators) {
        for(BlobPredator blobPredator : newPredators){
            blobPredator.mutate(p);
        }
    }

    private void mutateBlobVillagers(PApplet p,List<BlobVillager> newVillagers) {
        for(BlobVillager blobVillager : newVillagers){
            blobVillager.mutate(p);
        }
    }

    /**
     * Este método organiza a morte dos blobs. Se um blobVillager estiver dentro da hitbox de um blobPredator,
     * este é morto e removido da lista de blobsVillagers. O blobPredator que matou o blobVillager adiciona-o ao seu
     * inventário.
     * @param blobVillagers lista dos blobVillagers vivos
     * @param blobs lista dos blobPredators vivos
     * @param plt instância da classe SubPlot
     * @param p instância da classe PApplet
     * @return lista dos blobVillagers que morreram
     */
    public List<BlobVillager> killBlob(List<BlobVillager> blobVillagers, List<BlobPredator> blobs, SubPlot plt,PApplet p) {
        List<BlobVillager> villagersToRemove = new ArrayList<>();
        List<BlobVillager> villagersToAdd = new ArrayList<>();
        float[] hitBox;

        for (BlobPredator blobPredator : blobs) {
            hitBox = plt.getVectorCoord(blobPredator.getHitBox(), blobPredator.getHitBox());

            for (BlobVillager blobVillager : blobVillagers) {
                if ((blobPredator.getPosition().dist(blobVillager.getPosition()) < hitBox[0] ||
                        blobPredator.getPosition().dist(blobVillager.getPosition()) < hitBox[1]) ) {

                    villagersToRemove.add(blobVillager);
                    villagersToAdd.add(blobVillager);
                    createKillEffect(blobVillager.getPosition(), p);
                }
            }

            for (BlobVillager villager : villagersToAdd) {
                blobPredator.addBlob(villager);
            }
            villagersToAdd.clear();
        }

        blobVillagers.removeAll(villagersToRemove);
        return villagersToRemove;
    }

    /**
     * Dos blobVillagers que morreram iremos adicionar metade das frutas que tinham no inventário à lista de frutas do
     * mapa.
     * @param villagers lista dos blobVillagers que morreram
     */
    public void addFruitFromDeadVillagers(List<BlobVillager> villagers) {
        for (BlobVillager villager : villagers) {
            List<Fruit> inventory = villager.getInventory();
            if (!inventory.isEmpty()) {
                int halfFruits = inventory.size() / 2;
                for (int i = 0; i < halfFruits; i++) {
                    fruits.add(inventory.get(i));
                }
            }
        }
    }

    /**
     * Este método organiza a apanha de frutas por parte dos blobVillagers. Se um blobVillager estiver dentro da hitbox
     * de uma fruta, este apanha-o e adiciona-o ao seu inventário.
     * @param blobVillagers lista dos blobVillagers vivos
     * @param fruits lista das frutas no mapa
     */
    public void grabFruit(List<BlobVillager> blobVillagers, List<Fruit> fruits) {
        List<Fruit> fruitsToRemove = new ArrayList<>();
        float[] villagerHitBox;
        float[] fruitHitBox;

        for (BlobVillager blobVillager : blobVillagers) {
            villagerHitBox = plt.getVectorCoord(blobVillager.getHitBox(), blobVillager.getHitBox());
            for (Fruit fruit : fruits) {
                fruitHitBox = plt.getVectorCoord(fruit.getHitBox(), fruit.getHitBox());
                float distance = blobVillager.getPosition().dist(fruit.getPosition());
                if (distance <= villagerHitBox[0] + fruitHitBox[0] || distance <= villagerHitBox[1] + fruitHitBox[1]) {
                    blobVillager.addFruit(fruit);
                    fruitsToRemove.add(fruit);
                }
            }
        }

        fruits.removeAll(fruitsToRemove);
    }

    /**
     * Este método cria um sistema de partículas que representa a morte de um blob.
     * @param position posição do blob que morreu
     * @param p instância da classe PApplet
     */
    private void createKillEffect(PVector position, PApplet p) {
        ParticleSystem ps = new ParticleSystem(
                position.copy(),
                new PVector(0, 0),
                PARTICLE_MASS,
                PARTICLE_RADIUS,
                p.color(122, 0, 0, 128),
                PARTICLE_LIFETIME
        );

        for(int i = 0; i < 20; i++) {
            ps.addParticle();
        }
        killParticleSystem.add(ps);
    }

    /**
     * Este método atualiza e desenha os sistemas de partículas que representam a morte dos blobs.
     * @param p instância da classe PApplet
     * @param plt instância da classe SubPlot
     * @param dt tempo desde o último frame
     */
    public void updateKillParticles(PApplet p, SubPlot plt, float dt) {
        Iterator<ParticleSystem> iterator = killParticleSystem.iterator();
        while (iterator.hasNext()) {
            ParticleSystem ps = iterator.next();
            ps.move(dt);
            ps.display(p, plt);

            if (!ps.isAlive()) {
                iterator.remove();
            }
        }
    }

}