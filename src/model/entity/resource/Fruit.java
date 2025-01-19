package model.entity.resource;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import tools.Mover;
import tools.SubPlot;

/**
 * Esta classe representa um recurso do tipo fruta. Este recurso é consumido por BlobVillagers de modo a contribuir para
 * a sua reprodução.
 */
public class Fruit extends Mover {
    PImage fruitImg;
    PVector position;
    PVector vel;

    private final float mass;

    public Fruit(PVector position, float mass, PVector vel, PImage fruitImg){
        super(position,vel,mass);
        this.mass = mass;
        this.vel = vel;
        this.position = position;
        this.fruitImg = fruitImg;
    }


    public void display(PApplet p, SubPlot plt){
        p.pushStyle();
        float[] pp = plt.getPixelCoord(position.x, position.y);
        float[] hitBox = plt.getVectorCoord(30, 30);

        p.image(fruitImg, pp[0] - (hitBox[0]/2),pp[1] - (hitBox[1]/2), hitBox[0], hitBox[1]);
        p.popStyle();
    }

    public double getHitBox() {
        return mass;
    }
}
