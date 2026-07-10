package airtrafficsimulator.io;

public class ParsingException extends Exception {
	public ParsingException(int lineNumber, String line, String message) {
		super("Linija broj " + lineNumber + ": " + message + "\n" + line);
	}
}
