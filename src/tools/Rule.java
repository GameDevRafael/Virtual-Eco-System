package tools;

public class Rule {
    private final char symbol;
    private final String string;

    public Rule(char symbol, String string) {
        this.symbol = symbol;
        this.string = string;
    }

    public char getSymbol() {
        return symbol;
    }

    public String getString() {
        return string;
    }


}