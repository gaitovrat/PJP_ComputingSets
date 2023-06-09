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

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj instanceof Symbol) == false) {
			return false;
		}
		
		Symbol symbol = (Symbol)obj;
		return  this.getName().equals(symbol.getName());
	}
	
	
}
