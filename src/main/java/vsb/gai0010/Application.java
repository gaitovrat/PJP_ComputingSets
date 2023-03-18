package vsb.gai0010;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import vsb.gai0010.grammar.Grammar;
import vsb.gai0010.grammar.GrammarException;
import vsb.gai0010.grammar.GrammarOperations;
import vsb.gai0010.grammar.GrammarReader;
import vsb.gai0010.grammar.Nonterminal;

public class Application {
	public static void main(String[] args) {
		URL resource = Application.class.getClassLoader().getResource("input");
        Grammar grammar;
        
        assert resource != null : "Resource not found.";

        try (FileReader fileReader = new FileReader(new File(resource.toURI()))) {
            GrammarReader inp = new GrammarReader(fileReader);
            grammar = inp.read();
        } catch (GrammarException e) {
            System.err.println("Error(" + e.getLineNumber() + ") " + e.getMessage());
            return;
        } catch (IOException | URISyntaxException e) {
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
	}
}
