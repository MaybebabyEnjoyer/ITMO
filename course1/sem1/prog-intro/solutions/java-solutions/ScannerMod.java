import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

class wordChecker implements Check {
    @Override
    public boolean check(char c) {
        return (Character.getType(c) == Character.DASH_PUNCTUATION || Character.isLetter(c) || c == '\'');
    }
}

public class ScannerMod implements AutoCloseable {
    private final Reader reader;
    private final int BUFFER_SIZE = 4096;
    private final char[] buffer = new char[BUFFER_SIZE];
    private char c = 0;
    private int lines = 1;
    private int words = 0;
    char[] separatorArray = System.lineSeparator().toCharArray();
    private final char sep = separatorArray[separatorArray.length - 1];
    private boolean BufferedChar = false;
    private boolean isBufferEmpty = true;
    private int read;
    private wordChecker isWord = new wordChecker();
    private int i;

    public ScannerMod(String in) throws IOException {
        reader = new InputStreamReader(new FileInputStream(in), StandardCharsets.UTF_8);
    }

    public ScannerMod() throws IOException {
        reader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
    }

    private boolean readInput() throws IOException {
        if (isBufferEmpty) {
            read = reader.read(buffer);
            if (read < 0) {
                BufferedChar = false;
                return false;
            }
            isBufferEmpty = false;
            i = 0;
        }
        if (i + 1 == read) {
            isBufferEmpty = true;
        }
        c = buffer[i++];
        BufferedChar = true;
        return true;
    }

    public String next() throws IOException {
        StringBuilder tempString = new StringBuilder();
        while (BufferedChar || readInput()) {
            if (isWord.check(c)) {
                tempString.append(c);
                BufferedChar = false;
            } else {
                break;
            }
        }
        words++;
        return tempString.toString();
    }

    private void catchSeparator() throws IOException {
        while (BufferedChar || readInput()) {
            if ((!isWord.check(c) && !isLineSeparator())) {
                BufferedChar = false;
            } else {
                break;
            }
        }
    }

    private boolean isLineSeparator() {
        if (c == sep) {
            lines++;
            words = 0;
            return true;
        } else {
            return false;
        }
    }

    public boolean hasInput() throws IOException {
        return BufferedChar || readInput();
    }

    public boolean hasNext() throws IOException {
        catchSeparator();
        while (hasInput() && isLineSeparator()) {
            BufferedChar = false;
            catchSeparator();
        }
        return BufferedChar;
    }

    public int numberOfLines() {
        return (lines + 1) / 2;
    }

    public int numberOfWords() {
        return words;
    }

    public void close() throws IOException {
        reader.close();
    }

}