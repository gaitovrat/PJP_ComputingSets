package vsb.gai0010.grammar;

import java.io.*;
import java.util.Collection;
import java.util.List;

public interface Grammar {
    Collection<Nonterminal> getNonterminals();

    Collection<Terminal> getTerminals();

    List<Rule> getRules();

    Nonterminal getStartNonterminal();

    void dump(PrintStream out);
}
