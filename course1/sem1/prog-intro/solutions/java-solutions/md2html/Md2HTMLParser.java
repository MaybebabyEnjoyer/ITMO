package md2html;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Md2HTMLParser {
    private static final Map<String, String> marksMap = Map.of(
            "*", "em",
            "_", "em",
            "**", "strong",
            "__", "strong",
            "--", "s",
            "`", "code",
            "++", "u"
    );
    static private final Map<Character, String> specialSymbols = Map.of(
            '<', "&lt;",
            '>', "&gt;",
            '&', "&amp;"
    );

    public static String toHtml(List<String> text) {
        StringBuilder returnString = new StringBuilder();
        for (int i = 0; i < text.size(); i++) {
            StringBuilder sb1 = new StringBuilder();
            while (i < text.size() && !text.get(i).isEmpty()) {
                sb1.append(text.get(i)).append(System.lineSeparator());
                i++;
            }
            if (sb1.length() == 0) {
                continue;
            }
            sb1.setLength(sb1.length() - System.lineSeparator().length());
            paragraphParser(returnString, sb1.toString());
        }
        return returnString.toString();
    }

    private static int headerDepthCount(String string) {
        int depth = 0;
        while (depth < string.length() && string.charAt(depth) == '#') {
            depth++;
            if (depth > 6) {
                return depth;
            }
        }
        if (depth < string.length() && string.charAt(depth) == ' ') {
            return depth;
        }
        return 0;
    }

    private static String markParagraph(int headerDepth, boolean opened) {
        return "<" + (opened ? "" : "/") + (headerDepth == 0 ? "p" : "h" + headerDepth) + ">";
    }

    private static boolean isMarker(String string) {
        return marksMap.containsKey(string);
    }

    private static boolean checkSlash(String string, int position) {
        if (position + 2 == string.length()) {
            return false;
        }
        return 0 <= position && string.charAt(position) == '\\';
    }

    private static boolean markPosChecker(String st, int pos) {
        for (int mark = 1; mark <= 2; mark++) {
            if (pos + mark <= st.length() && isMarker(st.substring(pos, pos + mark))) {
                return true;
            }
        }
        return false;
    }

    private static String takeMark(String st, int pos) {
        for (int mark = 2; ; mark--) {
            if (pos + mark <= st.length()) {
                final String marker = st.substring(pos, pos + mark);
                if (isMarker(marker)) {
                    return marker;
                }
            }
        }
    }

    private static void addMark(Map<Integer, Integer> marks, Map<String, Integer> lastMark, String mark, int pos) {
        if (lastMark.containsKey(mark)) {
            marks.put(lastMark.get(mark), pos);
            lastMark.remove(mark);
        } else {
            lastMark.put(mark, pos);
        }
    }

    private static int applyMarks(Map<Integer, Integer> marks, StringBuilder sb, String returnString, int pos) {
        String mark = takeMark(returnString, pos);
        int closing = marks.get(pos);
        if (marksMap.containsKey(mark)) {
            String htmlMark = marksMap.get(mark);
            sb.append("<").append(htmlMark).append(">");
            stringParser(marks, sb, returnString, pos + mark.length(), closing);
            sb.append("</").append(htmlMark).append(">");
            pos = closing + mark.length() - 1;
        } else {
            sb.append(returnString.charAt(pos));
        }
        return pos;
    }

    private static void stringParser(Map<Integer, Integer> marks, StringBuilder sb, String returnString, int l, int r) {
        for (int i = l; i < r; i++) {
            if (marks.containsKey(i)) {
                i = applyMarks(marks, sb, returnString, i);
            } else if (specialSymbols.containsKey(returnString.charAt(i))) {
                sb.append(specialSymbols.get(returnString.charAt(i)));
            } else {
                sb.append(returnString.charAt(i));
                if ((sb.charAt(sb.length() - 1) == '*' || sb.charAt(sb.length() - 1) == '+') && sb.charAt(sb.length() - 2) == '\\') {
                    sb.replace(sb.length() - 2, sb.length() - 1, "");
                }
            }
        }
    }

    private static void findMark(Map<Integer, Integer> marks, String returnString) {
        Map<String, Integer> lastMark = new HashMap<>();
        for (int i = 0; i < returnString.length(); i++) {
            while (checkSlash(returnString, i)) {
                i += 2;
            }
            if (markPosChecker(returnString, i)) {
                final String marker = takeMark(returnString, i);
                addMark(marks, lastMark, marker, i);
                i += marker.length() - 1;
            }
        }
    }

    private static void paragraphParser(StringBuilder sb, String returnString) {
        int headerDepth = headerDepthCount(returnString);
        Map<Integer, Integer> marks = new HashMap<>();
        sb.append(markParagraph(headerDepth, true));
        findMark(marks, returnString);
        stringParser(marks, sb, returnString, headerDepth == 0 ? 0 : headerDepth + 1, returnString.length());
        sb.append(markParagraph(headerDepth, false));
        sb.append(System.lineSeparator());
    }
}