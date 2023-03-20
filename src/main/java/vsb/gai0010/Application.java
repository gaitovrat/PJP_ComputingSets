package vsb.gai0010;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import vsb.gai0010.grammar.Grammar;
import vsb.gai0010.grammar.GrammarException;
import vsb.gai0010.grammar.GrammarOperations;
import vsb.gai0010.grammar.GrammarReader;
import vsb.gai0010.grammar.Nonterminal;
import vsb.gai0010.grammar.Terminal;

public class Application {
	public static void main(String[] args) {
		InputStream resource = Application.class.getClassLoader().getResourceAsStream("input");
        Grammar grammar;
        
        assert resource != null : "Resource not found.";

        try (InputStreamReader fileReader = new InputStreamReader(resource)) {
            GrammarReader inp = new GrammarReader(fileReader);
            grammar = inp.read();
        } catch (GrammarException e) {
            System.err.println("Error(" + e.getLineNumber() + ") " + e.getMessage());
            return;
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            return;
        }

        grammar.dump(System.out);

        GrammarOperations grammarOperations = new GrammarOperations(grammar);

        /* first step, computing nonterminals that can generate empty word */
        for (Nonterminal nonterminals : grammarOperations.getEmptyNonterminals()) {
            System.out.print(nonterminals.getName() + " ");
        }
        System.out.println();
        
        Map<String,Set<Terminal>> first = grammarOperations.first();
        for (Entry<String, Set<Terminal>> entry : first.entrySet()) {
        	String value = entry.getValue().stream().map(el -> el.toString())
        			.reduce("", (el1, el2) -> el1 + " " + el2);
        	System.out.printf("first[%s] = %s\n", entry.getKey(), value);
        }
        
        Map<Nonterminal,Set<Terminal>> follow = grammarOperations.follow();
        for (Entry<Nonterminal, Set<Terminal>> entry : follow.entrySet()) {
        	String value = entry.getValue().stream().map(el -> el.toString())
        			.reduce("", (el1, el2) -> el1 + " " + el2);
        	System.out.printf("follow[%s] = %s\n", entry.getKey().getName(), value);
        }
	}
}
