import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public class WordStatWordsSuffix {
    public static void main(String[] args) {
        TreeMap<String, Integer> data = new TreeMap<>();
        StringBuilder word = new StringBuilder();
        StringBuilder suffix = new StringBuilder();
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), StandardCharsets.UTF_8));
            try {
                String read;
                while ((read = input.readLine()) != null) {
                    read = read.toLowerCase() + ' ';
                    word.setLength(0);
                    for (int j = 0; j < read.length(); j++) {
                        if (!(Character.getType(read.charAt(j)) == Character.DASH_PUNCTUATION ||
                                Character.isLetter(read.charAt(j)) || read.charAt(j) == '\'')) {
                            if (word.length() < 3 && word.length() != 0) {
                                data.put(word.toString(), data.getOrDefault(word.toString(), 0) + 1);
                            } else if (word.length() != 0) {
                                for (int i = word.length() - 3; i < word.length(); i++) {
                                    suffix.append(word.toString().charAt(i));
                                }
                                data.put(suffix.toString(), data.getOrDefault(suffix.toString(), 0) + 1);
                                suffix.setLength(0);
                            }
                            word.setLength(0);
                        } else {
                            word.append(read.charAt(j));
                        }
                    }
                }
                try {
                    BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8));
                    try {
                        for (Map.Entry<String, Integer> entry : data.entrySet()) {
                            out1.write(entry.getKey() + " " + entry.getValue());
                            out1.newLine();
                        }
                    } finally {
                        out1.close();
                    }
                } catch (FileNotFoundException ex) {
                    System.out.println("Can not find file" + ' ' + ex.getMessage());
                } catch (IOException ex) {
                    System.out.println("Error while writing in file" + ' ' + ex.getMessage());
                } finally {
                    input.close();
                }
            } catch (IOException ex) {
                System.out.println("Input/Output error" + ' ' + ex.getMessage());
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Can not find file" + ' ' + ex.getMessage());
        }
    }
}