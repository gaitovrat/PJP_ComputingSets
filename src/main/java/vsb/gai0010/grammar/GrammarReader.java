package vsb.gai0010.grammar;

import java.io.*;

public final class GrammarReader {
    private final LineNumberReader lineNumberReader;
    private int character;
    private String attr;

    public GrammarReader(Reader input) {
        lineNumberReader = new LineNumberReader(input);
        lineNumberReader.setLineNumber(1);
    }

    public Grammar read() throws GrammarException, IOException {
        GrammarImpl grammar = new GrammarImpl();

        character = lineNumberReader.read();
        int sym = getSym();

        while (sym != -1) {
            if (sym != SYM_NT) {
                error("On the left side of the rule, we are expecting a nonterminal.");
            }
            Nonterminal lhs = grammar.addNonterminal(attr);
            if (grammar.getStartNonterminal() == null) {
                grammar.setStartNonterminal(lhs);
            }
            sym = getSym();
            if (sym != ':') {
                error("':' expected.");
            }
            do {
                sym = getSym();
                Rule rule = new Rule(lhs);

                while (sym == SYM_NT || sym == SYM_T) {
                    if (sym == SYM_NT) {
                        Nonterminal nt = grammar.addNonterminal(attr);
                        rule.addSymbol(nt);
                        sym = getSym();
                    } else if (sym == SYM_T) {
                        Terminal t = grammar.addTerminal(attr);
                        rule.addSymbol(t);
                        sym = getSym();
                    }
                }
                lhs.addRule(rule);
            } while (sym == '|');
            if (sym != ';') {
                error("';' expected.");
            }
            sym = getSym();
        }
        return grammar;
    }

    private void error(String msg) throws GrammarException {
        throw new GrammarException(msg, lineNumberReader.getLineNumber());
    }

    private static final int SYM_NT = 'N';
    private static final int SYM_T = 'T';
    private static final int SYM_EOF = -1;

    private int getSym() throws IOException {
        for (;;) {
            if (character < 0) {
                return SYM_EOF;
            }
            if (Character.isWhitespace((char) character)) {
                character = lineNumberReader.read();
            } else if (character == '{') {
                do {
                    character = lineNumberReader.read();
                } while (character >= 0 && character != '}');
                if (character >= 0) {
                    character = lineNumberReader.read();
                }
            } else {
                break;
            }
        }

        if (Character.isLetter((char) character)) {
            StringBuilder buf = new StringBuilder();
            do {
                buf.append((char) character);
                character = lineNumberReader.read();
            } while (character > 0 && Character.isLetterOrDigit((char) character));
            attr = buf.toString();
            return Character.isLowerCase(attr.charAt(0)) ? SYM_T : SYM_NT;
        }

        int sym = character;
        character = lineNumberReader.read();
        return sym;
    }
}
