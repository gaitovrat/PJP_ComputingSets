package vsb.gai0010.grammar;

public abstract class Symbol implements Comparable<Symbol> {
	private final String name;

    public Symbol(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Symbol symbol) {
        return name.compareTo(symbol.getName());
    }

}
