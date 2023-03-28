package vsb.gai0010.grammar;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import vsb.gai0010.Table;

public class GrammarOperations {
	private final Grammar grammar;	
	private final Set<Nonterminal> emptyNonterminals;
	private final Terminal empty;
	private final Terminal dollar;
	private final Table<Symbol, Boolean> firstTable;
	private final Table<Symbol, Boolean> followTable;
	private boolean isFirstTableFilled;
	private boolean isFollowTableFilled;

    public GrammarOperations(Grammar grammar) {
        this.grammar = grammar;
        this.empty = new Terminal("{e}");
        this.dollar = new Terminal("{$}");
        this.firstTable = new Table<>(false);
        this.followTable = new Table<>(false);
        this.emptyNonterminals = new TreeSet<>();
        this.isFirstTableFilled = false;
        this.isFollowTableFilled = false;
        
        computeEmpty();
        
        for (Nonterminal nonterminal : grammar.getNonterminals()) {
        	this.firstTable.addRow(nonterminal);
        	this.firstTable.addColumn(nonterminal);
        	this.followTable.addRow(nonterminal);
        	this.followTable.addColumn(nonterminal);
        }
        for (Terminal terminal : grammar.getTerminals()) {
        	this.firstTable.addColumn(terminal);
        	this.followTable.addColumn(terminal);
        }
        this.followTable.addColumn(dollar);
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
    	
    	copyTerminalValues(this.firstTable);
    }
    
    private static void copyTerminalValues(Table<Symbol, Boolean> table) {
    	boolean hasChanged = false;
    	
    	do {
    		hasChanged = false;
    		
    		for (Symbol row : table.getRowNames()) {
    			for (Symbol col : table.getColumnNames()) {
    				boolean value = table.getCell(row, col); 
    				
    				if (value == true && col instanceof Nonterminal) {
    					for (Symbol colCopy : table.getColumnNames()) {
    						if (table.getCell(row, colCopy) == false && 
    								table.getCell(col, colCopy) == true) {
    							table.setCell(row, colCopy, true);
    							hasChanged = true;
    						}
    					}
    				}
    			}
    		}
    	} while(hasChanged);
    }
    
    public Set<Terminal> first(List<Symbol> symbols) {
    	Set<Terminal> result = new TreeSet<>();
    	
    	if (symbols.isEmpty()) {
    		result.add(empty);
			return result;
		}
		
		if (symbols.get(0) instanceof Terminal) {
			result.add((Terminal) symbols.get(0));
			return result;
		}
		
		if (this.emptyNonterminals.contains(symbols.get(0)) == false) {
			Symbol row = symbols.get(0);
			List<Symbol> columns = this.firstTable.getColumnNames()
					.stream()
					.filter(el -> el instanceof Terminal)
					.toList();
			for (Symbol col : columns) {
				if (this.firstTable.getCell(row, col) == true) {
					result.add((Terminal) col);
				}
			}
			return result;
		}
		
		if (symbols.stream().filter(el -> this.emptyNonterminals.contains(el) == false).count() == 0) {
			result.add(empty);
		}
		
		for (Symbol symbol : symbols) {
			Symbol row = symbols.get(0);
			
			if (symbol instanceof Terminal) {
				result.add((Terminal) symbol);
				continue;
			}
			
			List<Symbol> columns = this.firstTable.getColumnNames()
					.stream()
					.filter(el -> el instanceof Terminal)
					.toList();
			for (Symbol col : columns) {
				if (this.firstTable.getCell(row, col) == true) {
					result.add((Terminal) col);
				}
			}
		}
		
		return result;
    }
    
    public Map<String, Set<Terminal>> first() {
    	Map<String, Set<Terminal>> result = new TreeMap<>();

    	this.fillFirstTable();

    	for (Rule rule : this.grammar.getRules()) {
    		String key = rule.getLHS().getName() + ":" + rule.getRHS()
				.stream()
				.map(el -> el.toString())
				.reduce("", (el1, el2) -> el1 + el2);

    		List<Symbol> rhs = rule.getRHS();
    		
    		result.put(key, first(rhs));
    	}
    	
    	return result;
    }
    
    private void fillFollowTable() {
    	if (this.isFollowTableFilled) {
    		return;
    	}
    	
    	this.fillFirstTable();
    	
    	this.followTable.setCell(this.grammar.getStartNonterminal(), this.dollar, true);
    	
    	for (Rule rule : this.grammar.getRules()) {
    		Nonterminal lhs = rule.getLHS();
    		List<Symbol> rhs = rule.getRHS();
    		
    		for (int i = 0; i < rhs.size(); i++) {
    			if (rhs.get(i) instanceof Nonterminal) {
					Set<Terminal> terminals = first(rhs.subList(i + 1, rhs.size()));
					terminals.remove(empty);
					
					if (terminals.isEmpty()) {
						this.followTable.setCell(rhs.get(i), lhs, true);
					}
					for (Terminal terminal : terminals) {
						this.followTable.setCell(rhs.get(i), terminal, true);
					}
    			}
    		}
    	}
    	
    	copyTerminalValues(this.followTable);
    	
    	this.isFollowTableFilled = true;
    }
    
    public Set<Terminal> follow(Nonterminal nonterminal) {
    	Set<Terminal> result = new TreeSet<>();
    	
    	this.fillFollowTable();
    	
    	for (Symbol symbol : this.followTable.getColumnNames()) {
    		if (symbol instanceof Terminal && this.followTable.getCell(nonterminal, symbol) == true) {
    			result.add((Terminal) symbol);
    		}
    	}
    	
    	return result;
    }
    
    public Map<Nonterminal, Set<Terminal>> follow() {
    	Map<Nonterminal, Set<Terminal>> result = new TreeMap<>();
    	
    	for (Nonterminal nonterminal : this.grammar.getNonterminals()) {
    		result.put(nonterminal, follow(nonterminal));
    	}
    	
    	return result;
    }
}
