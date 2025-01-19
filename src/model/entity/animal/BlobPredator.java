package model.entity.animal;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import tools.SubPlot;

import java.util.ArrayList;
import java.util.List;

public class BlobPredator extends Blob {
    private final List<Blob> inventory;

    public BlobPredator(PVector pos, PVector vel, float mass, PImage blobImage, float awareness, float stamina,
                        float maxVelocity, PVector house, PImage houseBlob) {
        super(pos, vel, mass, blobImage, awareness, stamina, maxVelocity, house, houseBlob);
        this.awareness=awareness;
        this.inventory = new ArrayList<>();
    }

    /**
     * Método que faz com que o BlobPredator persiga um BlobVillager.
     * @param target posição do BlobVillager do qual o BlobPredator se quer aproximar.
     * @return vetor de força que fará com que o BlobPredator se aproxime dele.
     */
    public PVector seek(PVector target) {
        consumeStamina();

        PVector seekForce = PVector.sub(target, position);
        seekForce.normalize();
        seekForce.mult(maxVel);
        seekForce = PVector.sub(seekForce, velocity);

        return seekForce;
    }


    @Override
    public void displayInfo(PApplet p, SubPlot subPlotGame) {
        p.fill(255);
        p.textSize(15);
        p.text(getInventory().size() + " prey", subPlotGame.getPixelCoord(getPosition().x, getPosition().y)[0],
                subPlotGame.getPixelCoord(getPosition().x, getPosition().y)[1] - 20);
        p.text("S: " + String.format("%.0f", getStamina()), subPlotGame.getPixelCoord(getPosition().x, getPosition().y)[0],
                subPlotGame.getPixelCoord(getPosition().x, getPosition().y)[1] - 40);
        p.text(String.format("%.0f", getMaxVelocity()) + " px/s", subPlotGame.getPixelCoord(getPosition().x, getPosition().y)[0],
                subPlotGame.getPixelCoord(getPosition().x, getPosition().y)[1] - 60);
    }

    public List<Blob> getInventory() {
        return inventory;
    }

    public void addBlob(Blob blob){
        inventory.add(blob);
    }

    public void removeNumberOfBlobs(int value) {
        while(value != 0){
            inventory.remove(0);
            value--;
        }
    }



}
