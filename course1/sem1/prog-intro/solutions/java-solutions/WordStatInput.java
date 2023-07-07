import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class WordStatInput {
    public static void main(String[] args) {
        LinkedHashMap<String, Integer> data = new LinkedHashMap<>();
        StringBuilder word = new StringBuilder();
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), StandardCharsets.UTF_8));
            try {
                String read;
                try {
                    while ((read = input.readLine()) != null) {
                        read = read.toLowerCase() + ' ';
                        word.setLength(0);
                        for (int j = 0; j < read.length(); j++) {
                            if (!(Character.getType(read.charAt(j)) == Character.DASH_PUNCTUATION || Character.isLetter(read.charAt(j)) || read.charAt(j) == '\'')) {
                                if (word.length() > 0) {
                                    if (data.containsKey(word.toString())) {
                                        data.put(word.toString(), data.get(word.toString()) + 1);
                                    } else {
                                        data.put(word.toString(), 1);
                                    }
                                }
                                word.setLength(0);
                            } else {
                                word.append(read.charAt(j));
                            }
                        }
                    }
                } catch (NullPointerException e) {
                    System.out.println("NullPointerException");
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