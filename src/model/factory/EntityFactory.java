package model.factory;

import model.entity.animal.BlobPredator;
import model.entity.animal.BlobVillager;
import model.entity.plant.LeafTree;
import model.entity.resource.Fruit;
import model.world.WorldMap;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

/**
 * Esta classe fábrica é responsável por criar entidades do jogo, como BlobVillagers, BlobPredators, LeafTrees e Frutas.
 */
public class EntityFactory {
    private final PImage blobVillagerImage;
    private final PImage blobPredatorImage;
    private final PImage fruitImage;
    private final PImage blobHouse;

    public EntityFactory(PImage blobVillagerImage, PImage blobPredatorImage, PImage fruitImage, PImage blobHouse) {
        this.blobVillagerImage = blobVillagerImage;
        this.blobPredatorImage = blobPredatorImage;
        this.fruitImage = fruitImage;
        this.blobHouse = blobHouse;
    }

    /**
     * Adiciona um BlobVillager ao mapa. Calcula a posição da sua casa e tem a possibilidade de adquirir as
     * características do seu pai.
     * @param worldMap mapa do mundo onde vai ser inserido
     * @param p objeto PApplet
     * @param parent pai que originou o novo BlobVillager
     * @param initialPosition posição inicial do novo BlobVillager
     * @return BlobVillager
     */
    public BlobVillager addBlobVillager(WorldMap worldMap, PApplet p, BlobVillager parent, PVector initialPosition) {
        PVector position;
        if (initialPosition != null) {
            position = initialPosition;
        } else {
            int randomX = (int) p.random(8, 25);
            int randomY = (int) p.random(13, 26);
            position = worldMap.getCellPosition(randomX, randomY);
        }
        PVector house = position.copy();

        if (parent != null) {
            return new BlobVillager(position, new PVector(0, 0), 1.0f, blobVillagerImage,
                    parent.getAwareness(), parent.getStaminaInit(), parent.getMaxVelocity(), house, blobHouse);
        } else {
            return new BlobVillager(position, new PVector(0, 0), 1.0f, blobVillagerImage,
                    100.0f, 2000.0f, 100.0f, house, blobHouse);
        }
    }

    /**
     * Adiciona um BlobPredator ao mapa. Calcula a posição da sua casa e tem a possibilidade de adquirir as
     * características do seu pai. Este será criado numa posição aleatória fora de um perímetro definido onde os
     * BlobVillagers se encontram.
     * @param worldMap mapa do mundo onde vai ser inserido
     * @param p objeto PApplet
     * @param parent pai que originou o novo BlobPredator
     * @param initialPosition posição inicial do novo BlobPredator
     * @return BlobPredator
     */
    public BlobPredator addBlobPredator(WorldMap worldMap, PApplet p, BlobPredator parent, PVector initialPosition) {
        boolean isTop = p.random(1) < 0.5;
        int x, y;
        if(isTop) {
            x = (int)p.random(1, 8);
        } else {
            x = (int)p.random(25, 32);
        }
        y = (int)p.random(1, 39);

        PVector position;
        if (initialPosition != null) {
            position = initialPosition;
        } else {
            position = worldMap.getCellPosition(x, y);
        }
        PVector house = position.copy();

        BlobPredator predator;
        if(parent != null) {
            if(initialPosition != null){
                predator = new BlobPredator(initialPosition, new PVector(0, 0), 1.0f, blobPredatorImage,
                        parent.getAwareness(), parent.getStaminaInit(), parent.getMaxVelocity(), initialPosition,
                        blobHouse);
            } else {
                predator = new BlobPredator(position, new PVector(0, 0), 1.0f, blobPredatorImage,
                        parent.getAwareness(), parent.getStaminaInit(), parent.getMaxVelocity(), house, blobHouse);
            }
        } else {
            predator = new BlobPredator(position, new PVector(0, 0), 1.0f, blobPredatorImage,
                    100.0f, 2000.0f, 100.0f, house, blobHouse);
        }
        return predator;
    }

    public LeafTree addFruitTree(WorldMap worldMap, PApplet p) {
        PVector position = worldMap.getCellPosition((int) p.random(10, 30), (int) p.random(4, 38));
        return new LeafTree(position);
    }

    public Fruit addFruit(WorldMap worldMap, PApplet p, PVector mousePosition) {
        PVector position = worldMap.getCellPosition((int) p.random(33), (int) p.random(40));
        Fruit fruit;

        if(mousePosition == null){
            fruit = new Fruit(position, 1f, new PVector(0, 0), fruitImage);
        } else{
            fruit = new Fruit(mousePosition, 1f, new PVector(0, 0), fruitImage);

        }
        return fruit;
    }

}
