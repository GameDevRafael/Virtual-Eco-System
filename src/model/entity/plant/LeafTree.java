package model.entity.plant;

import model.entity.resource.Fruit;
import processing.core.PApplet;
import processing.core.PVector;
import tools.LSystem;
import tools.Mover;
import tools.SubPlot;

import java.util.ArrayList;
import java.util.List;

/**
 * Esta classe representa uma árvore. Esta árvore adiciona realismo à simulação, mudando de cor de acordo com
 * a estação do ano. Esta é feita através de um sistema L-System que desenha a árvore.
 */
public class LeafTree extends Mover {
    private float len = 3.6f;
    private float[] lenPix;

    public LeafTree(PVector pos) {
        super(pos, new PVector(0, 0), 1f);
        position = pos;
    }

    public void setPose(float orientation, PApplet p, SubPlot plt) {
        float[] pp = plt.getPixelCoord(position.x, position.y);
        p.translate(pp[0], pp[1]);
        p.rotate(-orientation);
    }

    private void drawLeaf(PApplet p, String season) {
        if(season.equals("summer")){
            p.pushStyle();
            p.noStroke();
            p.fill(34, 139, 34);
            p.ellipse(0, 0, lenPix[0] / 2, lenPix[1] / 2);
            p.popStyle();
        }
    }

    public void scaling(float s) {
        len *= s;
    }

    /**
     * Renderiza a árvore no ecrã. A árvore é desenhada com a cor branca ou castanha dependendo da estação do ano.
     * Dependendo da sequência do sistema L, a árvore é desenhada de forma diferente podendo variar a sua forma e
     * desenhar as folhas.
     * @param lSys sistema de L que desenha a árvore
     * @param p objeto PApplet para desenhar a árvore
     * @param plt objeto SubPlot para obter as coordenadas corretas
     * @param season estação do ano para mudar a cor da árvore
     */
    public void render(LSystem lSys, PApplet p, SubPlot plt, String season) {
        lenPix = plt.getVectorCoord(len, len);

        for(int i = 0; i < lSys.getSequence().length(); i++) {
            char c = lSys.getSequence().charAt(i);
            float angle = 3.14f / 4f;
            if(c == 'F' || c == 'G') {
                if(season.equals("winter"))
                    p.stroke(225, 220, 220);
                else
                    p.stroke(139, 69, 19);
                p.strokeWeight(3);
                p.line(0, 0, lenPix[0], 0);
                p.translate(lenPix[0], 0);
            }
            else if(c == 'f') p.translate(lenPix[0], 0);
            else if(c == '+') p.rotate(angle);
            else if(c == '-') p.rotate(-angle);
            else if(c == '[') p.pushMatrix();
            else if(c == ']') p.popMatrix();
            else if(c == 'L') drawLeaf(p, season);
        }
    }
}
