package vsb.gai0010.grammar;

import java.util.Set;
import java.util.TreeSet;

public class GrammarOperations {
	Grammar grammar;	
	Set<Nonterminal> emptyNonterminals;

    public GrammarOperations(Grammar grammar) {
        this.grammar = grammar;
        computeEmpty();
    }

    public Set<Nonterminal> getEmptyNonterminals() {
        return emptyNonterminals;
    }

    private void computeEmpty() {
        emptyNonterminals = new TreeSet<Nonterminal>();
        //TODO: start here
    }
}
