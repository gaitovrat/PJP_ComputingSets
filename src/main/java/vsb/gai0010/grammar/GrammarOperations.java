package vsb.gai0010.grammar;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import vsb.gai0010.Table;

public class GrammarOperations {
	private final Grammar grammar;	
	private final Set<Nonterminal> emptyNonterminals;
	private final Terminal empty;
	private final Terminal dollar;
	private final Table<Symbol, Boolean> firstTable;
	private boolean isFirstTableFilled;

    public GrammarOperations(Grammar grammar) {
        this.grammar = grammar;
        this.empty = new Terminal("{e}");
        this.dollar = new Terminal("{$}");
        this.firstTable = new Table<>(false);
        this.emptyNonterminals = new TreeSet<>();
        this.isFirstTableFilled = false;
        
        computeEmpty();
        
        for (Nonterminal nonterminal : grammar.getNonterminals()) {
        	this.firstTable.addRow(nonterminal);
        	this.firstTable.addColumn(nonterminal);
        }
        for (Terminal terminal : grammar.getTerminals()) {
        	this.firstTable.addColumn(terminal);
        }
    }

    public Set<Nonterminal> getEmptyNonterminals() {
        return emptyNonterminals;
    }

    private void computeEmpty() {
        List<Rule> rules = this.grammar.getRules();
        boolean hasChanged = false;
        
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
        
        
        do {
        	hasChanged = false;
        	
	        for (Rule rule : rules) {
	        	List<Symbol> rhs = rule.getRHS();
	        	
	        	List<Symbol> terminals = rhs.stream().filter(el -> el instanceof Terminal).toList();
	        	if (terminals.isEmpty() == false) {
	        		continue;
	        	}
	        	
	        	for (Symbol symbol : rhs) {
	        		if (this.emptyNonterminals.contains(symbol) && 
	        				this.emptyNonterminals.contains(rule.getLHS()) == false) {
	        			this.emptyNonterminals.add(rule.getLHS());
	        			hasChanged = true;
	        		}
	        	}
	        }
        } while (hasChanged);
    }
    
    private void fillFirstTable() {
    	if (this.isFirstTableFilled) {
    		return;
    	}
    	
    	boolean hasChanged = false;
    	
    	for (Rule rule : this.grammar.getRules()) {
    		Nonterminal lhs = rule.getLHS();
    		List<Symbol> rhs = rule.getRHS();
    		
    		if (rhs.isEmpty()) {
    			continue;
    		}
    		
    		if (rhs.get(0) instanceof Terminal) {
    			this.firstTable.setCell(lhs, rhs.get(0), true);
    			continue;
    		}
    		
    		if (this.emptyNonterminals.contains(rhs.get(0)) == false) {
    			this.firstTable.setCell(lhs, rhs.get(0), true);
    			continue;
    		}
    		
    		for (Symbol symbol : rhs) {
    			this.firstTable.setCell(lhs, symbol, true);
    		}
    	}
    	
    	do {
    		hasChanged = false;
    		
    		for (Symbol row : this.firstTable.getRowNames()) {
    			for (Symbol col : this.firstTable.getColumnNames()) {
    				boolean value = this.firstTable.getCell(row, col); 
    				
    				if (value == true && col instanceof Nonterminal) {
    					for (Symbol colCopy : this.firstTable.getColumnNames()) {
    						if (this.firstTable.getCell(row, colCopy) == false && 
    								this.firstTable.getCell(col, colCopy) == true) {
    							this.firstTable.setCell(row, colCopy, true);
    							hasChanged = true;
    						}
    					}
    				}
    			}
    		}
    	} while(hasChanged);
    }
    
    public Map<String, Set<Terminal>> first() {
    	Map<String, Set<Terminal>> result = new TreeMap<>();

    	this.fillFirstTable();

    	for (Rule rule : this.grammar.getRules()) {
    		String key = rule.getLHS().getName() + ":" + rule.getRHS()
				.stream()
				.map(el -> el.toString())
				.reduce("", (el1, el2) -> el1 + el2);
    		Set<Terminal> terminals = new TreeSet<>();

    		List<Symbol> rhs = rule.getRHS();
    		
    		if (rhs.isEmpty()) {
    			terminals.add(empty);
    			result.put(key, terminals);
    			continue;
    		}
    		
    		if (rhs.get(0) instanceof Terminal) {
    			terminals.add((Terminal) rhs.get(0));
    			result.put(key, terminals);
    			continue;
    		}
    		
    		if (this.emptyNonterminals.contains(rhs.get(0)) == false) {
    			Symbol row = rhs.get(0);
    			List<Symbol> columns = this.firstTable.getColumnNames()
    					.stream()
    					.filter(el -> el instanceof Terminal)
    					.toList();
    			for (Symbol col : columns) {
    				if (this.firstTable.getCell(row, col) == true) {
    					terminals.add((Terminal) col);
    				}
    			}
    			result.put(key, terminals);
    			continue;
    		}
    		
    		if (rhs.stream().filter(el -> this.emptyNonterminals.contains(el) == false).count() == 0) {
    			terminals.add(empty);
    		}
    		
    		for (Symbol symbol : rhs) {
    			Symbol row = rhs.get(0);
    			if (symbol instanceof Terminal) {
    				terminals.add((Terminal) symbol);
    				continue;
    			}
    			
    			List<Symbol> columns = this.firstTable.getColumnNames()
    					.stream()
    					.filter(el -> el instanceof Terminal)
    					.toList();
    			for (Symbol col : columns) {
    				if (this.firstTable.getCell(row, col) == true) {
    					terminals.add((Terminal) col);
    				}
    			}
    		}
    		result.put(key, terminals);
    	}
    	
    	return result;
    }
    
    public Set<Terminal> follow(Nonterminal nonterminal) {
    	Set<Terminal> result = new TreeSet<>();
    	
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
