import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WsppCountPosition {
    public static void main(String[] args) {
        Map<String, List<Pair>> data = new LinkedHashMap<>();
        try (ScannerMod sc = new ScannerMod(args[0])) {
            try {
                while (sc.hasNext()) {
                    String word = sc.next().toLowerCase();
                    List<Pair> def = data.getOrDefault(word, new ArrayList<>());
                    def.add(new Pair(sc.numberOfLines(), sc.numberOfWords()));
                    data.put(word, def);
                }
            } catch (NullPointerException e) {
                System.out.println("NullPointerException" + " " + e.getMessage());
            }
            ValueComparator sortByNumber = new ValueComparator(data);
            // Map
            TreeMap<String, List<Pair>> sortedData = new TreeMap<>(sortByNumber);
            sortedData.putAll(data);
            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {
                for (Map.Entry<String, List<Pair>> entry : sortedData.entrySet()) {
                    out.write(entry.getKey() + " " + entry.getValue().size());
                    for (Pair pair : entry.getValue()) {
                        out.write(" " + pair.getPair());
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

class ValueComparator implements Comparator<String> {
    Map<String, List<Pair>> base;

    public ValueComparator(Map<String, List<Pair>> base) {
        this.base = base;
    }

    public int compare(String a, String b) {
        // retrun condition ? isTrue : isFalse;
        if (base.get(a).size() >= base.get(b).size()) {
            return 1;
        } else {
            return -1;
        }
    }
}