package vsb.gai0010.grammar;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

public class GrammarOperations {
	private Grammar grammar;	
	private Set<Nonterminal> emptyNonterminals;
	private Terminal empty;
	private Terminal dollar;

    public GrammarOperations(Grammar grammar) {
        this.grammar = grammar;
        this.empty = new Terminal("{e}");
        this.dollar = new Terminal("{$}");
        
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
    
    public Set<Terminal> first(List<Symbol> symbols, boolean isEmptyNonterminal) {
    	Stack<Symbol> stack = new Stack<>();
		Set<Terminal> terminals = new TreeSet<>();
		
		if (symbols.isEmpty() == true) {
			terminals.add(empty);
			return terminals;
		}
		
		if (symbols.get(0) instanceof Terminal) {
			terminals.add((Terminal)symbols.get(0));
			return terminals;
		}
		
		for (Symbol symbol : symbols) {
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
			
			for (Rule nonterminalRule : ((Nonterminal)symbol).getRules()) {
				try {
					stack.push(nonterminalRule.getRHS().get(0));
				} catch (IndexOutOfBoundsException e) {
					if (isEmptyNonterminal == true) {
						stack.push(empty);
					}
				}
			}
		}
		
		return terminals;
    }
    
    public Map<String, Set<Terminal>> first() {
    	Map<String, Set<Terminal>> result = new TreeMap<>();

    	for (Rule rule : this.grammar.getRules()) {
    		String key = rule.getLHS().getName() + ":" + rule.getRHS()
				.stream()
				.map(el -> el.toString())
				.reduce("", (el1, el2) -> el1 + el2);
    		result.put(key, first(rule.getRHS(), this.emptyNonterminals.contains(rule.getLHS())));
    	}
    	
    	return result;
    }
    
    public Set<Terminal> follow(Nonterminal nonterminal) {
    	Set<Terminal> result = new TreeSet<>();
    	Stack<Rule> rules = new Stack<>();
    	Stack<Nonterminal> nonterminals = new Stack<>();
    	
    	nonterminals.push(nonterminal);
    	
    	do {
    		Nonterminal currentNonterminal = nonterminals.pop();
    		if (currentNonterminal.equals(this.grammar.getStartNonterminal()) == true) {
    			result.add(this.dollar);
    		}
    		
	    	for (Rule rule : this.grammar.getRules()) {
	    		List<Symbol> rhs = rule.getRHS();
	    		
	    		for (int i = 0; i < rhs.size(); i++) {
	    			if ((rhs.get(i) instanceof Nonterminal) && rhs.get(i).equals(currentNonterminal)) {
	    				rules.push(rule);
	    			}
	    		}
	    	}
	    	
	    	
	    	while (rules.empty() == false) {
	    		Rule rule = rules.pop();
	    		List<Symbol> rhs = rule.getRHS();
	    		
	    		for (int i = 0; i < rhs.size(); i++) {
	    			if (rhs.get(i) instanceof Nonterminal) {
	    				Set<Terminal> firstSet = first(rhs.subList(i + 1, rhs.size()), false);
	    				firstSet.remove(empty);
	    				result.addAll(firstSet);
	    				
	    				if (firstSet.isEmpty() == true && rule.getLHS().equals(nonterminal) == false) {
    						nonterminals.push(rule.getLHS());
	    				}
	    				
	    				break;
	    			}
	    		}
	    	}
    	} while (nonterminals.empty() == false);
    	
    	return result;
    }
    
    public Map<Nonterminal, Set<Terminal>> follow() {
    	Map<Nonterminal, Set<Terminal>> result = new TreeMap<>();
    	
    	for (Nonterminal nonterminal : this.grammar.getNonterminals()) {
    		result.put(nonterminal, new TreeSet<>());
    	}
    	
    	for (Entry<Nonterminal, Set<Terminal>> entry : result.entrySet()) {
    		result.get(entry.getKey()).addAll(follow(entry.getKey()));
    	}
    	
    	return result;
    }
}
