import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class Wspp {
    public static void main(String[] args) {
        Map<String, IntList> data = new LinkedHashMap<>();
        int k = 1;
        try (ScannerMod sc = new ScannerMod(args[0])) {
            ;
            try {
                while (sc.hasNext()) {
                    String word = sc.next().toLowerCase();
                    IntList a = data.getOrDefault(word, new IntList());
                    a.append(k);
                    data.put(word, a);
                    k++;
                }
            } catch (NullPointerException e) {
                System.out.println("NullPointerException" + " " + e.getMessage());
            }

            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {
                for (Map.Entry<String, IntList> entry : data.entrySet()) {
                    out.write(entry.getKey() + " " + entry.getValue().size());
                    for (int i = 0; i < entry.getValue().size(); i++) {
                        out.write(" " + entry.getValue().get(i));
                    }
                    out.newLine();
                }
            } catch (IOException ex) {
                System.out.println("Error while writing" + " " + ex.getMessage());
            }
        } catch (IOException ex) {
            System.out.println("Something bad happened..." + " " + ex.getMessage());
        }
    }
}