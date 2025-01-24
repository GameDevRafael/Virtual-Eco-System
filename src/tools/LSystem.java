package tools;

import model.entity.plant.LeafTree;

import java.util.Arrays;
import java.util.List;

/**
 * Esta classe que repsenta um LSystem (Sistema de Lindenmayer) é responsável por gerar uma sequência de caracteres
 * com base num conjunto de regras. Este sistema é usado para gerar sequências de caracteres que são usadas para
 * representar estruturas fractais, no nosso caso, as árvores.
 */
public class LSystem {
    private String sequence;
    private final Rule[] ruleset;
    private int currentGeneration;
    private String[] generationHistory;
    private static final int MAX_GENERATIONS = 3;

    public LSystem(String axiom, Rule[] ruleset) {
        sequence = axiom;
        this.ruleset = ruleset;
        this.currentGeneration = 0;
        this.generationHistory = new String[MAX_GENERATIONS + 1];

        Arrays.fill(generationHistory, axiom); // preenche o array com o axiom
    }

    public void reset() {
        currentGeneration = 0;
        generationHistory = new String[MAX_GENERATIONS + 1];
    }

    /**
     * Faz a próxima geração da sequência de caracteres com base nas regras definidas e guarda a geração atual num
     * array.
     */
    public void nextGeneration() {
        generationHistory[currentGeneration] = sequence;
        currentGeneration++;

        String nextGen = "";
        for(int i=0; i < sequence.length();i++) {
            char c = sequence.charAt(i);

            String replace = "" + c;
            for (Rule rule : ruleset) {
                if (c == rule.getSymbol()) {
                    replace = rule.getString();
                    break;
                }
            }
            nextGen += replace;
        }
        this.sequence = nextGen;
    }

    /**
     * Faz a geração anterior da sequência de caracteres para simular a morte de uma árvore.
     */
    public void previousGeneration(List<LeafTree> leafTrees) {
        if (currentGeneration <= 0) {
            leafTrees.clear();
            return;
        }

        currentGeneration--;
        sequence = generationHistory[currentGeneration];
    }


    public String getSequence() {
        return sequence;
    }


}