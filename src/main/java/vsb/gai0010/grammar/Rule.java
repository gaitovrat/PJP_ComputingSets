package vsb.gai0010.grammar;

import java.util.*;

public class Rule {
	private final Nonterminal lhs;	
	private final List<Symbol> rhs;

    public Rule(Nonterminal lhs) {
        this.lhs = lhs;
        this.rhs = new ArrayList<>();
    }

    public Nonterminal getLHS() {
        return lhs;
    }

    public List<Symbol> getRHS() {
        return rhs;
    }

    public void addSymbol(Symbol s) {
        rhs.add(s);
    }

}
