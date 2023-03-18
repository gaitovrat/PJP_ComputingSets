package vsb.gai0010.grammar;

import java.util.*;
import java.io.*;

public class GrammarImpl implements Grammar {
    private final Map<String, Terminal> terminals;
    private final Map<String, Nonterminal> nonterminals;
    private Nonterminal startNonterminal;
    
    public GrammarImpl() {
    	this.terminals = new TreeMap<String, Terminal>();
    	this.nonterminals = new TreeMap<String, Nonterminal>();
    }

    @Override
    public Collection<Terminal> getTerminals() {
        return this.terminals.values();
    }

    public Terminal addTerminal(String name) {
        Terminal terminal = (Terminal) this.terminals.get(name);
        if (terminal == null) {
            terminal = new Terminal(name);
            this.terminals.put(name, terminal);
        }
        return terminal;
    }

    @Override
    public Collection<Nonterminal> getNonterminals() {
        return this.nonterminals.values();
    }

    public Nonterminal addNonterminal(String name) {
        Nonterminal nonterminal = (Nonterminal) this.nonterminals.get(name);
        if (nonterminal == null) {
            nonterminal = new Nonterminal(name);
            this.nonterminals.put(name, nonterminal);
        }
        return nonterminal;
    }

    @Override
    public List<Rule> getRules() {
        ArrayList<Rule> rules = new ArrayList<Rule>();

        for (Nonterminal nonterminal : this.getNonterminals()) {
            for (Rule rule : nonterminal.getRules()) {
                rules.add(rule);
            }
        }
        return rules;
    }

    public void addRule(Rule rule) {
        rule.getLHS().addRule(rule);
    }

    @Override
    public Nonterminal getStartNonterminal() {
        return this.startNonterminal;
    }

    public void setStartNonterminal(Nonterminal start) {
        this.startNonterminal = start;
    }

    public void dump(PrintStream out) {
        out.print("Terminals:");
        for (Terminal terminal : this.getTerminals()) {
            out.print(" " + terminal.getName());
        }
        out.println();

        out.print("Nonterminals:");
        for (Nonterminal nonterminal : this.getNonterminals()) {
            out.print(" " + nonterminal.getName());
        }
        out.println();

        out.println("Starting nonterminal: " + this.getStartNonterminal().getName());

        out.println("Rules:");
        int i = 0;
        for (Rule rule : this.getRules()) {
            i++;
            out.print("[" + i + "] " + rule.getLHS().getName() + " -> ");

            for (Symbol symbol : rule.getRHS()) {
                out.print(symbol.getName() + " ");
            }
            out.println();
        }
    }
}
