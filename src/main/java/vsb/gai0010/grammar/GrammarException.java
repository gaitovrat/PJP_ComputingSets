package vsb.gai0010.grammar;

public class GrammarException extends Exception {
	private static final long serialVersionUID = 3268597161196859317L;

	private final int lineNumber;

	public GrammarException(String msg, int lineNo) {
        super(msg);
        this.lineNumber = lineNo;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

}
