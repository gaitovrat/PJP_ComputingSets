package vsb.gai0010.grammar;

import java.util.*;

public class Nonterminal extends Symbol {
    private final List<Rule> rules;

    public Nonterminal(String name) {
        super(name);
        this.rules = new ArrayList<>();
    }

    public List<Rule> getRules() {
        return rules;
    }

    void addRule(Rule rule) {
        rules.add(rule);
    }
}
