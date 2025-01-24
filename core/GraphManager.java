package core;

import model.entity.animal.BlobPredator;
import model.entity.animal.BlobVillager;
import processing.IProcessingApp;
import processing.core.PApplet;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Esta classe é responsável por gerir as janelas dos gráficos e coordena a recolha e visualização das estatísticas do
 * jogo ao longo do tempo. Cada janela de gráfico vai recolher uma estatística específica de acordo com o valor do
 * windowType. As estatísticas recolhidas são guardadas em listas de pontos de dados que são atualizadas a cada segundo.
 */
public class GraphManager implements IProcessingApp {
    private final int windowType;
    private float timeSinceLastUpdate = 0;
    private final List<Float> villagerDataPoints = new CopyOnWriteArrayList<>();
    private final List<Float> predatorDataPoints = new CopyOnWriteArrayList<>();

    private static final int BACKGROUND_COLOR = 240;
    private static final int AXIS_COLOR = 50;
    private static final int GRID_COLOR = 200;
    private static final int VILLAGER_COLOR = 0xFF1E88E5; // azul
    private static final int PREDATOR_COLOR = 0xFFF44336; // vermelho
    private static final float POINT_SIZE = 8;
    private static final int AXIS_WEIGHT = 2;
    private static final int GRID_WEIGHT = 1;
    private static final int LINE_WEIGHT = 3;
    private static final int TEXT_SIZE = 12;
    private static final int TITLE_SIZE = 18;
    private static final int LEGEND_SIZE = 14;

    public GraphManager(int windowType) {
        this.windowType = windowType;
    }

    @Override
    public void setup(PApplet p) {
        p.background(BACKGROUND_COLOR);
        p.frameRate(60);
        p.textSize(TEXT_SIZE);
    }

    @Override
    public void draw(PApplet p, float deltaTime) {
        timeSinceLastUpdate += deltaTime;
        float updateInterval = 1.0f;
        if (timeSinceLastUpdate >= updateInterval) {
            collectData();
            timeSinceLastUpdate = 0;
        }
        displayGraph(p);
    }

    /**
     * Para cada janela de gráfico vai ser recolhida uma estatística específica de acordo com o valor do windowType.
     * Se as informações recolhidas forem superiores a 100, as informações mais antigas são removidas para manter
     * a janela de gráfico atualizada e não sobrecarregada.
     */
    private void collectData() {
        switch (windowType) {
            case 0 -> collectPopulationData();
            case 1 -> collectAwarenessData();
            case 2 -> collectVelocityData();
            case 3 -> collectStaminaData();
        }

        while (villagerDataPoints.size() > 100) villagerDataPoints.remove(0);
        while (predatorDataPoints.size() > 100) predatorDataPoints.remove(0);
    }

    private void collectPopulationData() {
        villagerDataPoints.add((float) GameManager.getBlobVillagers().size());
        predatorDataPoints.add((float) GameManager.getBlobPredators().size());
    }

    private void collectAwarenessData() {
        villagerDataPoints.add(calculateAverageAwareness(GameManager.getBlobVillagers()));
        predatorDataPoints.add(calculateAverageAwareness(GameManager.getBlobPredators()));
    }

    private void collectVelocityData() {
        villagerDataPoints.add(calculateAverageVelocity(GameManager.getBlobVillagers()));
        predatorDataPoints.add(calculateAverageVelocity(GameManager.getBlobPredators()));
    }

    private void collectStaminaData() {
        villagerDataPoints.add(calculateAverageStamina(GameManager.getBlobVillagers()));
        predatorDataPoints.add(calculateAverageStamina(GameManager.getBlobPredators()));
    }

    /**
     * Calcula a média da estatística de awareness de todos os blobs.
     * @param blobs Lista de blobs
     * @return Média da estatística de awareness
     */
    private float calculateAverageAwareness(List<?> blobs) {
        if (blobs.isEmpty()) return 0;
        float total = 0;
        for (Object blob : blobs) {
            if (blob instanceof BlobVillager) {
                total += ((BlobVillager) blob).getAwareness();
            } else if (blob instanceof BlobPredator) {
                total += ((BlobPredator) blob).getAwareness();
            }
        }
        return total / blobs.size();
    }

    /**
     * Calcula a média da velocidade máxima de todos os blobs.
     * @param blobs Lista de blobs
     * @return Média da velocidade máxima
     */
    private float calculateAverageVelocity(List<?> blobs) {
        if (blobs.isEmpty()) return 0;
        float total = 0;
        for (Object blob : blobs) {
            if (blob instanceof BlobVillager) {
                total += ((BlobVillager) blob).getMaxVelocity();
            } else if (blob instanceof BlobPredator) {
                total += ((BlobPredator) blob).getMaxVelocity();
            }
        }
        return total / blobs.size();
    }

    /**
     * Calcula a média da stamina de todos os blobs.
     * @param blobs Lista de blobs
     * @return Média da stamina
     */
    private float calculateAverageStamina(List<?> blobs) {
        if (blobs.isEmpty()) return 0;
        float total = 0;
        for (Object blob : blobs) {
            if (blob instanceof BlobVillager) {
                total += ((BlobVillager) blob).getStaminaInit();
            } else if (blob instanceof BlobPredator) {
                total += ((BlobPredator) blob).getStaminaInit();
            }
        }
        return total / blobs.size();
    }

    /**
     * Desenha o gráfico com as estatísticas recolhidas.
     * Caso não haja estatísticas recolhidas, é apresentada uma mensagem a informar que não há estatísticas.
     * @param p PApplet
     */
    private void displayGraph(PApplet p) {
        p.background(BACKGROUND_COLOR);

        float margin = 50;
        float graphWidth = p.width - 2 * margin;
        float graphHeight = p.height - 2 * margin;

        drawTitle(p, margin);

        if (villagerDataPoints.isEmpty() && predatorDataPoints.isEmpty()) {
            p.textAlign(PApplet.CENTER, PApplet.CENTER);
            p.text("Sem estastíticas por enquanto...", p.width / 2f, p.height / 2f);
            return;
        }

        float maxValue = Math.max(getMaxValue(villagerDataPoints), getMaxValue(predatorDataPoints));
        drawGrid(p, margin, graphWidth, graphHeight, maxValue);
        drawAxes(p, margin);
        drawLegend(p, margin);

        drawDataLine(p, margin, villagerDataPoints, VILLAGER_COLOR, maxValue);
        drawDataLine(p, margin, predatorDataPoints, PREDATOR_COLOR, maxValue);
    }

    /**
     * Desenha o título do gráfico.
     * @param p PApplet
     * @param margin margem que separa o gráfico da borda da janela
     */
    private void drawTitle(PApplet p, float margin) {
        p.textSize(TITLE_SIZE);
        p.textAlign(PApplet.CENTER, PApplet.TOP);
        p.fill(AXIS_COLOR);
        String title = switch (windowType) {
            case 0 -> "População ao Longo do Tempo";
            case 1 -> "Campo de Visão";
            case 2 -> "Velocidade Máxima";
            case 3 -> "Stamina";
            default -> "Título do Gráfico";
        };
        p.text(title, p.width / 2f, margin / 2f);
    }

    /**
     * Desenha as legendas no gráfico e os respetivos círculos coloridos que representam quando cada linha do gráfico é
     * atualizada.
     * @param p PApplet
     * @param margin margem que separa o gráfico da borda da janela
     */
    private void drawLegend(PApplet p, float margin) {
        p.textSize(LEGEND_SIZE);
        p.textAlign(PApplet.LEFT, PApplet.TOP);

        float legendY = margin / 4f;

        p.fill(VILLAGER_COLOR);
        p.circle(margin, legendY, POINT_SIZE);
        p.text("Villagers", margin + 15, legendY - 4);

        p.fill(PREDATOR_COLOR);
        p.circle(margin + 100, legendY, POINT_SIZE);
        p.text("Predators", margin + 115, legendY - 4);
    }

    /**
     * Desenha a grelha do gráfico com 11 divisões horizontais e verticais e as respetivas etiquetas.
     * @param p PApplet
     * @param margin margem que separa o gráfico da borda da janela
     * @param graphWidth largura do gráfico
     * @param graphHeight altura do gráfico
     * @param maxValue valor máximo do eixo y
     */
    private void drawGrid(PApplet p, float margin, float graphWidth, float graphHeight, float maxValue) {
        p.stroke(GRID_COLOR);
        p.strokeWeight(GRID_WEIGHT);

        float labelMargin = margin - 10;

        for (int i = 0; i <= 11; i++) {
            float x = margin + (i * graphWidth / 11);
            float y = margin + (i * graphHeight / 11);
            p.line(x, margin, x, p.height - margin);
            p.line(margin, y, p.width - margin, y);

            if (i > 0) {
                float value = maxValue * 1.1f * (11 - i) / 11;
                p.fill(AXIS_COLOR);
                p.textSize(TEXT_SIZE - 2);
                p.textAlign(PApplet.RIGHT, PApplet.CENTER);
                p.text(String.format("%.1f", value), labelMargin, y);
            }
        }
    }

    /**
     * Desenha as linhas dos eixos x e y do gráfico e nomeia os eixos com "Tempo" e "Valor" que representam o tempo e o
     * valor respetivamente das estatísticas recolhidas.
     * @param p PApplet
     * @param margin margem que separa o gráfico da borda da janela
     */
    private void drawAxes(PApplet p, float margin) {
        p.stroke(AXIS_COLOR);
        p.strokeWeight(AXIS_WEIGHT);

        float labelMargin = margin - 10;

        // desenha os eixos
        p.line(margin, p.height - margin, p.width - margin, p.height - margin);
        p.line(margin, p.height - margin, margin, margin);

        // meter o 0 na origem
        p.fill(AXIS_COLOR);
        p.textSize(TEXT_SIZE - 2);
        p.textAlign(PApplet.RIGHT, PApplet.CENTER);
        p.text("0", labelMargin, p.height - margin);

        // legenda do eixo dos x (horizontal)
        p.textSize(TEXT_SIZE);
        p.textAlign(PApplet.CENTER, PApplet.TOP);
        p.text("Tempo", p.width / 2f, p.height - margin / 2f);

        // legenda do eixo dos y (vertical)
        p.textAlign(PApplet.CENTER, PApplet.BOTTOM);
        p.text("Valor", margin, margin - 10);
    }

    /**
     * Desenha a linha do gráfico com os pontos de dados recolhidos e os respetivos círculos coloridos. Estas linhas de
     * ambos os blobVillagers como os blobPredators são desenhadas com diferentes cores e representam a evolução das
     * estatísticas recolhidas ao longo do tempo.
     * Se houver menos de 2 pontos de dados, a linha não é desenhada porque não é possível traçar uma linha com menos
     * de 2 pontos devido à falta de informação para a linha.
     * @param p PApplet
     * @param margin margem que separa o gráfico da borda da janela
     * @param data Lista de pontos de dados
     * @param color Cor da linha
     * @param maxValue Valor máximo do eixo y
     */
    private void drawDataLine(PApplet p, float margin, List<Float> data, int color, float maxValue) {
        if (data.size() < 2) return;

        p.stroke(color);
        p.strokeWeight(LINE_WEIGHT);
        p.noFill();

        p.beginShape();
        for (int i = 0; i < data.size(); i++) {
            float x = PApplet.map(i, 0, data.size() - 1, margin, p.width - margin);
            float y = PApplet.map(data.get(i), 0, maxValue * 1.1f, p.height - margin, margin);
            p.vertex(x, y);
        }
        p.endShape();

        p.fill(color);
        p.noStroke();
        for (int i = 0; i < data.size(); i += 5) {
            float x = PApplet.map(i, 0, data.size() - 1, margin, p.width - margin);
            float y = PApplet.map(data.get(i), 0, maxValue * 1.1f, p.height - margin, margin);
            p.circle(x, y, POINT_SIZE);
        }
    }


    /**
     * Apanha o valor máximo de uma lista de pontos de dados.
     * @param data Lista de pontos de dados
     * @return Valor máximo
     */
    private float getMaxValue(List<Float> data) {
        return data.stream()
                .max(Float::compare)
                .orElse(1.0f);
    }

    /**
     * Limpa os pontos de dados recolhidos para todos os gráficos quando o jogo é reiniciado.
     */
    public static void resetGraphs() {
        GraphManager[] activeGraphs = new GraphManager[4];
        for (GraphManager graph : activeGraphs) {
            if (graph != null) {
                graph.villagerDataPoints.clear();
                graph.predatorDataPoints.clear();
            }
        }
    }

    @Override
    public void mousePressed(PApplet p) {}
    @Override
    public void keyPressed(PApplet p) {}
    @Override
    public void keyReleased(PApplet p) {}
    @Override
    public void mouseReleased(PApplet p) {}
    @Override
    public void mouseDragged(PApplet p) {}
}