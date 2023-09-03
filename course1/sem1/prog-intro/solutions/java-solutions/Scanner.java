import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class Scanner {
    private final Reader reader;
    private final int BUFFER_SIZE = 4096;
    private final char[] buffer = new char[BUFFER_SIZE];
    private char c = 0;
    private final char[] separatorArray = System.lineSeparator().toCharArray();
    private final char sep = separatorArray[separatorArray.length - 1];
    private boolean BufferedChar = false;
    private boolean isBufferEmpty = true;
    private int read;
    private int i;

    public Scanner(String in) throws IOException {
        reader = new InputStreamReader(new FileInputStream(in), StandardCharsets.UTF_8);
    }

    public Scanner() throws IOException {
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
            if (!Character.isWhitespace(c)) {
                tempString.append(c);
                BufferedChar = false;
            } else {
                break;
            }
        }
        return tempString.toString();
    }

    private void catchSeparator() throws IOException {
        while (BufferedChar || readInput()) {
            if ((Character.isWhitespace(c) && !isLineSeparator())) {
                BufferedChar = false;
            } else {
                break;
            }
        }
    }

    private boolean isLineSeparator() {
        return c == sep;
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

    public boolean hasNextChar() throws IOException {
        catchSeparator();
        if (isLineSeparator()) {
            BufferedChar = false;
            readInput();
            return false;
        }
        return true;
    }

    public int nextInt() throws IOException {
        try {
            return Integer.parseInt(next());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void close() throws IOException {
        reader.close();
    }

}
