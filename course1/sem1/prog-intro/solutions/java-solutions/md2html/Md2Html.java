package md2html;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Md2Html {
    public static void main(String[] args) throws IOException {
        List<String> line;
        line = Files.lines(Path.of(args[0]))
                .collect(Collectors.toList());
        String html = Md2HTMLParser.toHtml(line);
        System.out.println(line);
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {
            out.write(html);
        } catch (IOException e) {
            System.out.println("Something bad happened while writing..." + e.getMessage());
        }
    }
}
