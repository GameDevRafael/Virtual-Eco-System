package processing;

import core.GameManager;
import core.GraphManager;
import processing.core.PApplet;

/**
 * Esta classe é responsável por criar a janela do jogo e as janelas dos gráficos com as estatísticas do jogo.
 */
public class ProcessingSetup extends PApplet {

    private GameManager gameManager;
    private GraphManager graphManager;

    private int lastUpdateTime;

    private final boolean isGameWindow;
    private final int windowType;

    /**
     * Aqui criamos a janela do jogo e as janelas dos gráficos. A janela do jogo é criada com dimensões de 1000x800
     * e as janelas dos gráficos são criadas com dimensões de 400x400 onde cada uma representa um gráfico diferente.
     * Para tal, cada janela de gráfico vai ter um valor int para determinar a sua função.
     * @param args Argumentos passados para o método main.
     */
    public static void main(String[] args) {
        ProcessingSetup gameWindow = new ProcessingSetup(true, -1);
        PApplet.runSketch(new String[]{"Game Manager"}, gameWindow);
        gameWindow.setWindowTitle("Game Window");

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 4; i++) {
            ProcessingSetup graphWindow = new ProcessingSetup(false, i);
            PApplet.runSketch(new String[]{"Graph Manager " + i}, graphWindow);

            switch (i) {
                case 0:
                    graphWindow.setWindowTitle("Population ao Longo do Tempo");
                    break;
                case 1:
                    graphWindow.setWindowTitle("Campo de Visão");
                    break;
                case 2:
                    graphWindow.setWindowTitle("Velocidade Máxima");
                    break;
                case 3:
                    graphWindow.setWindowTitle("Stamina");
                    break;
            }
        }
    }

    public void setWindowTitle(String title) {
        surface.setTitle(title);
    }

    public ProcessingSetup(boolean isGameWindow, int windowType) {
        this.isGameWindow = isGameWindow;
        this.windowType = windowType;
    }

    @Override
    public void settings() {
        if (isGameWindow) {
            size(1000, 800);
        } else {
            size(400, 400);
        }
    }

    @Override
    public void setup() {
        if (isGameWindow) {
            gameManager = new GameManager();
            gameManager.setup(this);
        } else {
            graphManager = new GraphManager(windowType);
            graphManager.setup(this);
        }


        lastUpdateTime = millis();
    }

    @Override
    public void draw() {
        int now = millis();
        float deltaT = (now - lastUpdateTime) / 1000f;
        lastUpdateTime = now;

        if (isGameWindow) {
            gameManager.draw(this, deltaT);

        } else {
            graphManager.draw(this, deltaT);
        }

    }

    @Override
    public void keyReleased() {
        if (isGameWindow) {
            gameManager.keyReleased(this);
        } else {
            graphManager.keyReleased(this);
        }
    }

    @Override
    public void mousePressed() {
        if (isGameWindow) {
            gameManager.mousePressed(this);
        } else {
            graphManager.mousePressed(this);
        }
    }


    @Override
    public void keyPressed() {
        if (isGameWindow) {
            gameManager.keyPressed(this);
        } else {
            graphManager.keyPressed(this);
        }
    }
}