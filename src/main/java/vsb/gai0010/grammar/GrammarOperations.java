package vsb.gai0010.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.Stack;
import java.util.TreeMap;

public class GrammarOperations {
	Grammar grammar;	
	Set<Nonterminal> emptyNonterminals;
	Terminal empty;

    public GrammarOperations(Grammar grammar) {
        this.grammar = grammar;
        this.empty = new Terminal("{e}");
        computeEmpty();
    }

    public Set<Nonterminal> getEmptyNonterminals() {
        return emptyNonterminals;
    }

    private void computeEmpty() {
        emptyNonterminals = new TreeSet<Nonterminal>();
        List<Rule> rules = this.grammar.getRules();
        boolean isEmpty = true;
        
        for (Rule rule : rules) {
        	List<Symbol> rhs = rule.getRHS();
        	
        	if (rhs.size() == 0) {
        		this.emptyNonterminals.add(rule.getLHS());
        		continue;
        	}
        }
        
        if (this.emptyNonterminals.isEmpty() == true) {
        	return;
        }
        
        for (Rule rule : rules) {
        	List<Symbol> rhs = rule.getRHS();
        	
        	List<Symbol> terminals = rhs.stream().filter(el -> el instanceof Terminal).toList();
        	if (terminals.isEmpty() == false) {
        		continue;
        	}
        	
        	for (Symbol symbol : rhs) {
        		if (this.emptyNonterminals.contains(symbol) == false) {
        			isEmpty = false;
        			break;
        		}
        	}
        	
        	if (isEmpty == true) {
        		this.emptyNonterminals.add(rule.getLHS());
        	}
        }
    }
    
    public Map<String, Set<Terminal>> first() {
    	Map<String, Set<Terminal>> result = new TreeMap<>();
    	
    	List<Rule> rules = this.grammar.getRules();
    	for (Rule rule : rules) {
    		Stack<Symbol> stack = new Stack<>();
    		boolean isEmptyNonterminal = this.emptyNonterminals.contains(rule.getLHS());
    		Set<Terminal> terminals = new TreeSet<>();
    		String key = rule.getLHS().getName() + ":" + rule.getRHS()
    			.stream()
    			.map(el -> el.toString())
    			.reduce("", (el1, el2) -> el1 + el2);

    		if (rule.getRHS().isEmpty() == true) {
    			terminals.add(empty);
    			result.put(key, terminals);
    			continue;
    		}
    		
    		if (rule.getRHS().get(0) instanceof Terminal) {
    			terminals.add((Terminal)rule.getRHS().get(0));
    			result.put(key, terminals);
    			continue;
    		}
    		
    		for (Symbol symbol : rule.getRHS()) {
    			stack.push(symbol);
    		}
    		
    		while (stack.empty() == false) {
				Symbol symbol = stack.pop();
				
				if (symbol.getName().equals(empty.getName()) == true && isEmptyNonterminal) {
					terminals.add(empty);
					continue;
				}
				
				if (symbol instanceof Terminal) {
					terminals.add((Terminal)symbol);
					continue;
				}
				
				Nonterminal nonterminal = (Nonterminal)symbol;
				List<Rule> nonterminalRules = nonterminal.getRules();
				for (Rule nonterminalRule : nonterminalRules) {
					try {
						stack.push(nonterminalRule.getRHS().get(0));
					} catch (IndexOutOfBoundsException e) {
						if (isEmptyNonterminal == true) {
							stack.push(empty);
						}
					}
				}
    		}
    		
    		result.put(key, terminals);
    	}
    	
    	return result;
    }
}
