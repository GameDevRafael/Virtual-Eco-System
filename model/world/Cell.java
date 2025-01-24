package model.world;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.List;

public class Cell {
    public final PVector position;

    public Cell(PVector position) {
        this.position = position;
    }

    public PVector getPosition(){
        return position.copy();
    }

}