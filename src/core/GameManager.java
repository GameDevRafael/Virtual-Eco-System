package core;

import model.entity.animal.Blob;
import model.entity.animal.BlobPredator;
import model.entity.animal.BlobVillager;
import model.entity.plant.LeafTree;
import model.entity.resource.Fruit;
import model.factory.EntityFactory;
import model.world.WorldMap;
import processing.IProcessingApp;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import tools.LSystem;
import tools.Rule;
import tools.SubPlot;

import java.util.ArrayList;
import java.util.List;

/**
 * O GameManager lidera o jogo. Controla a inicialização e a atualização de todos os elementos do jogo. Inicializa
 * o mapa, os blobs, as árvores, as frutas e o LSystem. Controla a pausa do jogo e a inicialização do jogador e a sua
 * movimentação, bem como a interação com o mouse e o teclado.
 */
public class GameManager implements IProcessingApp {
    private WorldMap worldMap;
    private InteractionManager IM;
    private Game game;
    private EntityFactory entityFactory;
    private LSystem lSys;
    private Blob blobPlayer = null;
    private SubPlot subPlotGame;

    private static List<BlobVillager> blobVillagers;
    private static List<BlobPredator> blobPredators;
    private final List<LeafTree> leafTrees;
    private final List<Fruit> fruits;

    private int maxFruits = 40;
    private final int maxPredators = 5; // 5
    private final int maxVillagers = 10; // 10
    private final int maxTrees = 4;

    private static final double[] WINDOW_GAME = {0, 1000, 0, 800};
    private static final float[] VP_GAME = {0, 0, 1f, 1f};

    private boolean pauseGame = false;

    public GameManager() {
        blobVillagers = new ArrayList<>();
        blobPredators = new ArrayList<>();
        leafTrees = new ArrayList<>();
        fruits=new ArrayList<>();
    }

    /**
     * Inicializa o jogo.
     * Cria o mapa, os blobs, as árvores, as frutas e o LSystem. Inicializa os blobs com mutações aleatórias. Define o
     * mapa do mundo do jogo.
     * @param p PApplet
     */
    @Override
    public void setup(PApplet p) {
        subPlotGame = new SubPlot(WINDOW_GAME, VP_GAME, p.width, p.height);
        // 1000px / 25 = 40 e 800px / 25 = 32
        worldMap = new WorldMap(p, 33, 40, subPlotGame);
        Rule[] rules = new Rule[1];
        rules[0] = new Rule('F', "F[+F]F[-F]FL");
        lSys = new LSystem("F", rules);

        PImage blobVillagerImage = p.loadImage("data/blob.png");
        PImage blobPredatorImage = p.loadImage("data/predator.png");
        PImage fruitImage = p.loadImage("data/fruit.png");
        PImage blobHouse = p.loadImage("data/house.png");

        entityFactory = new EntityFactory(blobVillagerImage, blobPredatorImage, fruitImage, blobHouse);

        initializeEntities(p);

        for(BlobPredator blobPredator : blobPredators){
            blobPredator.mutatePositive(p);
        }
        for(BlobVillager blobVillager : blobVillagers){
            blobVillager.mutate(p);
        }

        game = new Game(blobVillagers, blobPredators, fruits, IM, this);
        worldMap.setGame(game);

    }

    /**
     * Cria instâncias de cada entidade até ào limite máximo definido para cada entidade.
     * Cria a instância do InteractionManager com as frutas, o subPlotGame, a entityFactory e o worldMap.
     * @param p PApplet
     */
    private void initializeEntities(PApplet p) {
        for(int numberOfVillagers = 1; numberOfVillagers <= maxVillagers; numberOfVillagers++){
            initializeVillagers(p);
        }
        for(int numberOfPredators = 1; numberOfPredators <= maxPredators; numberOfPredators++){
            initializePredators(p);
        }
        for(int numberOfTrees = 1; numberOfTrees <= maxTrees; numberOfTrees++){
            initializeFruitTree(p);
        }
        for(int numberOfFruits = 1; numberOfFruits <= maxFruits; numberOfFruits++){
            initializeFruit(p);
        }
        IM = new InteractionManager(fruits, subPlotGame, entityFactory, worldMap);
    }

    public void initializeVillagers(PApplet p){
        blobVillagers.add(entityFactory.addBlobVillager(worldMap, p, null, null));
    }
    public void initializePredators(PApplet p){
        blobPredators.add(entityFactory.addBlobPredator(worldMap, p, null, null));
    }
    public void initializeFruit(PApplet p){
        fruits.add(entityFactory.addFruit(worldMap, p, null));
    }
    public void initializeFruitTree(PApplet p){
        leafTrees.add(entityFactory.addFruitTree(worldMap, p));
    }

    /**
     * Desenha o jogo.
     * Desenha o mapa, os blobs, as árvores, as frutas e o LSystem. Atualiza a transição do dia para a noite.
     * Desenha a movimentação do jogador.
     * Desenha as informações dos blobs.
     * @param p PApplet
     * @param dt float
     */
    @Override
    public void draw(PApplet p, float dt) {

        worldMap.updateTransition(game.getIsDay());
        worldMap.draw(p);

        game.update(lSys, p, leafTrees, fruits, subPlotGame, dt, maxFruits, pauseGame, blobPlayer);

        for (BlobVillager blob : blobVillagers) {
            blob.display(p, subPlotGame);
            blob.displayInfo(p, subPlotGame);
        }
        for (BlobPredator blob : blobPredators) {
            blob.display(p, subPlotGame);
            blob.displayInfo(p, subPlotGame);
        }
        for (Fruit fruit : fruits) {
            fruit.display(p, subPlotGame);
        }
        for (LeafTree tree : leafTrees) {
            p.pushMatrix();
            tree.setPose(3.14f/2, p, subPlotGame);
            tree.render(lSys, p, subPlotGame, game.getSeason());
            p.popMatrix();
        }

        if (blobPlayer != null && !pauseGame) {
            PVector force = blobPlayer.getPlayerMovementForce();
            blobPlayer.move(dt, force);
        }

    }

    public static List<BlobPredator> getBlobPredators() {
        return new ArrayList<>(blobPredators);
    }

    public static List<BlobVillager> getBlobVillagers() {
        return new ArrayList<>(blobVillagers);
    }

    public void setNumOfFruit(int numOfFruit) {
        this.maxFruits=numOfFruit;
    }

    /**
     * Ao pressionar o botão esquerdo do rato podemos inserir um BlobVillager, com o direito podemos inserir um
     * BlobPredator e com o botão do meio do rato podemos inserir uma fruta.
     * Se não existir um blob do tipo de blobs a ser inserido, este é criado com os valores defaults, senão é criado a
     * partir das características de um blob do mesmo tipo que esteja vivo.
     * O blob é adicionado à lista de blobs do tipo correspondente e mutado.
     * @param p PApplet
     */
    @Override
    public void mousePressed(PApplet p) {
        PVector mousePosition = new PVector(p.mouseX, p.height - p.mouseY);
        BlobVillager blobVillager;
        BlobPredator blobPredator;

        fruits.add(entityFactory.addFruit(worldMap, p, mousePosition));

        if (p.mouseButton == PApplet.LEFT) {
            if(blobVillagers.isEmpty()){
                blobVillager = entityFactory.addBlobVillager(worldMap, p, null, mousePosition);
            } else{
                blobVillager = entityFactory.addBlobVillager(worldMap, p, blobVillagers.get((int)
                        p.random(blobVillagers.size())), mousePosition);
            }
            blobVillager.mutate(p);
            blobVillagers.add(blobVillager);

        } else if (p.mouseButton == PApplet.RIGHT) {
            if(blobPredators.isEmpty()){
                blobPredator = entityFactory.addBlobPredator(worldMap, p, null, mousePosition);
            } else{
                blobPredator = entityFactory.addBlobPredator(worldMap, p, blobPredators.get((int)
                        p.random(blobPredators.size())), mousePosition);
            }
            blobPredator.mutate(p);
            blobPredators.add(blobPredator);
        } else if(p.mouseButton == PApplet.CENTER){
            fruits.add(entityFactory.addFruit(worldMap, p, mousePosition));
        }
    }

    /**
     * Ao soltar uma tecla, o jogador deixa de se mover.
     * @param p PApplet
     */
    @Override
    public void keyReleased(PApplet p) {
        if (blobPlayer != null && (p.key == 'w' || p.key == 'a' || p.key == 's' || p.key == 'd' ||
                p.key == 'W' || p.key == 'A' || p.key == 'S' || p.key == 'D')) {
            blobPlayer.handlePlayerMovement(p.key, false);
        }
    }

    /**
     * Ao pressionar uma tecla, podemos pausar o jogo, reiniciar o jogo, selecionar um blob do tipo BlobVillager ou
     * BlobPredator e movimentar o blob selecionado tornando-se este no jogador.
     * @param p PApplet
     */
    @Override
    public void keyPressed(PApplet p) {
        if (p.key == ' ') {
            pauseGame = !pauseGame;

        } else if (p.key == '1') {
            if(!blobVillagers.isEmpty()){
                blobPlayer = game.getRandomBlobVillager(p);
            }

        } else if(p.key == '2'){
            if(!blobPredators.isEmpty()){
                blobPlayer = game.getRandomBlobPredator(p);
            }
        }

        if (blobPlayer != null && (p.key == 'w' || p.key == 'a' || p.key == 's' || p.key == 'd' ||
                p.key == 'W' || p.key == 'A' || p.key == 'S' || p.key == 'D')) {
            blobPlayer.handlePlayerMovement(Character.toLowerCase(p.key), true);
        }
    }

    @Override
    public void mouseReleased(PApplet p) {}

    @Override
    public void mouseDragged(PApplet p) {}

}