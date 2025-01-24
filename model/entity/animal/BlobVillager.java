package model.entity.animal;

import model.entity.resource.Fruit;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import tools.SubPlot;

import java.util.ArrayList;
import java.util.List;

/**
 * Esta classe representa um Blob do tipo BlobVillager. Este Blob é um Blob que tem a capacidade de armazenar frutas,
 * fugir de BlobPredators, vaguearem e reproduzir-se.
 */
public class BlobVillager extends Blob {
    private final List<Fruit> inventory;

    public BlobVillager(PVector pos, PVector vel, float mass, PImage blobImage, float awareness,
                        float stamina, float maxVelocity, PVector house, PImage blobHouse){
        super(pos, vel, mass, blobImage, awareness, stamina, maxVelocity, house, blobHouse);
        this.inventory = new ArrayList<>();

    }

    @Override
    public void displayInfo(PApplet p, SubPlot subPlotGame) {
        p.fill(255);
        p.textSize(15);
        p.text(getInventory().size() + " fruits", subPlotGame.getPixelCoord(getPosition().x, getPosition().y)[0],
                subPlotGame.getPixelCoord(getPosition().x, getPosition().y)[1] - 20);
        p.text("S: " + String.format("%.0f", getStamina()), subPlotGame.getPixelCoord(getPosition().x, getPosition().y)[0],
                subPlotGame.getPixelCoord(getPosition().x, getPosition().y)[1] - 40);
        p.text(String.format("%.0f", getMaxVelocity()) + " px/s", subPlotGame.getPixelCoord(getPosition().x, getPosition().y)[0],
                subPlotGame.getPixelCoord(getPosition().x, getPosition().y)[1] - 60);
    }

    /**
     * Método que faz com que o BlobVillager apanhe uma fruta.
     * @param target posição da fruta que o BlobVillager quer coletar.
     * @return vetor de força que fará com que o BlobVillager se aproxime da fruta.
     */
    public PVector seek(PVector target) {
        PVector seekForce = PVector.sub(target, position);
        seekForce.normalize();
        seekForce.mult(maxVel);
        seekForce = PVector.sub(seekForce, velocity);

        return seekForce;
    }

    /**
     * Método que faz com que o BlobVillager fuja de um BlobPredator.
     * @param blobPredatorPosition posição do BlobPredator do qual o BlobVillager quer fugir.
     * @return vetor de força que fará com que o BlobVillager se afaste do BlobPredator.
     */
    public PVector run(PVector blobPredatorPosition) {
        consumeStamina();

        PVector run = PVector.sub(position, blobPredatorPosition);
        run.normalize();
        run.mult(maxVel);
        run = PVector.sub(run, velocity);

        return run;
    }

    public List<Fruit> getInventory() {
        return inventory;
    }

    public void addFruit(Fruit fruit){
        inventory.add(fruit);
    }

    public void removeNumberOfFruits(int value) {
        while(value != 0){
            inventory.remove(inventory.get(0));
            value--;
        }
    }



}
