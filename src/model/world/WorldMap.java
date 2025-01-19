package model.world;

import core.Game;
import core.InteractionManager;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import tools.SubPlot;

/**
 * Basicamente esta classe divide o mundo numa grelha de células onde vão estar os blobs e as arvores
 * o mapa está dividido em colunas e linhas e cada célula tem uma area de 25px por 25px
 */
public class WorldMap {
    private final Cell[][] grid;
    private final SubPlot subPlot;
    private Game game;

    private final PImage background_day;
    private final PImage background_night;
    private final PImage background_winter;
    private final PImage background_night_winter;

    private final int nRows, nColumns;
    private float transitionDay;

    /**
     * Esta classe representa o mapa do mundo onde se encontra o ecossistema do jogo.
     *
     * @param p objeto PApplet
     * @param nRows número de linhas para a grelha mapa
     * @param nColumns número de colunas para a grelha mapa
     * @param subPlot objeto SubPlot para obtermos as fronteiras do mapa
     */
    public WorldMap(PApplet p, int nRows, int nColumns, SubPlot subPlot) {
        this.nRows = nRows;
        this.nColumns = nColumns;
        this.grid = new Cell[nRows][nColumns];
        this.subPlot = subPlot;
        transitionDay = 0;
        background_day= p.loadImage("data/background.png");
        background_night= p.loadImage("data/background_night.png");
        background_winter= p.loadImage("data/background_Winter.png");
        background_night_winter= p.loadImage("data/background_night_winter.png");

        // para cada linha e coluna criamos uma celula com a sua posição e tamanho de 25px por 25px
        for(int row = 0; row < nRows; row++) {
            for(int column = 0; column < nColumns; column++) {
                int cellSize = 25;
                double worldX = column * cellSize;
                double worldY = row * cellSize;
                float[] pixelPos = subPlot.getPixelCoord(worldX, worldY);
                PVector fixedPosition = new PVector(pixelPos[0], pixelPos[1]);
                grid[row][column] = new Cell(fixedPosition);
            }
        }
    }

    public void setGame(Game game){
        this.game = game;
    }

    public PVector getCellPosition(int row, int col) {
        Cell cell = getCell(row, col);
        if (cell != null) {
            return cell.getPosition().copy();
        }
        return null;
    }

    public Cell getCell(int row, int col) {
        if (row >= 0 && row < nRows && col >= 0 && col < nColumns) {
            return grid[row][col];
        }
        return null;
    }

    public void draw(PApplet p) {
        float[] box = subPlot.getBoundingBox(); // as fronteiras do mapa
        PImage background;
        if(game.getSeason().equals("winter")){
            background = blendBackground(p, background_winter, background_night_winter);
        } else{
            background = blendBackground(p, background_day, background_night);
        }
        p.image(background, box[0], box[1], box[2], box[3]);

       /* for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nColumns; col++) {
                Cell cell = grid[row][col];
                if (cell != null) {

                    //p.fill(128, 128);
                    //p.rect(cell.position.x, cell.position.y, cellSize, cellSize);
                }
            }
        }*/
    }

    /**
     * Para ocorrer uma transição entre o dia e a noite, é necessário fazer um blend entre as duas imagens de fundo.
     * @param p objeto PApplet
     * @param background1 imagem de fundo do dia
     * @param background2 imagem de fundo da noite
     * @return imagem de fundo com a transição entre o dia e a noite
     */
    private PImage blendBackground(PApplet p, PImage background1, PImage background2) {
        PImage blendedImage = p.createImage(background1.width, background1.height, PApplet.RGB);
        blendedImage.loadPixels();
        background1.loadPixels();
        background2.loadPixels();

        for (int i = 0; i < blendedImage.pixels.length; i++) {
            int dayColor = background1.pixels[i];
            int nightColor = background2.pixels[i];

            float r = PApplet.lerp(p.red(dayColor), p.red(nightColor), transitionDay);
            float g = PApplet.lerp(p.green(dayColor), p.green(nightColor), transitionDay);
            float b = PApplet.lerp(p.blue(dayColor), p.blue(nightColor), transitionDay);

            blendedImage.pixels[i] = p.color(r, g, b);
        }

        blendedImage.updatePixels();
        return blendedImage;
    }

    /**
     * Atualiza a transição entre o dia e a noite.
     * @param isDay booleano que indica se é dia ou noite
     */
    public void updateTransition(boolean isDay) {
        float transitionSpeed = 0.01f;
        if (isDay) {
            transitionDay = PApplet.max(0, transitionDay - transitionSpeed);
        } else {
            transitionDay = PApplet.min(1, transitionDay + transitionSpeed);
        }
    }
}
