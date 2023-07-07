package markup;

import java.util.List;
import java.util.Map;

public class Main implements Element {
    Map<String, String> markup = Map.of(
            "emphasis", "*",
            "strong", "__",
            "strikeout", "~",
            "none", ""
    );
    Map<String, String> tex = Map.of(
            "emphasis", "\\emph{",
            "strong", "\\textbf{",
            "strikeout", "\\textst{",
            "close", "}"
    );
    List<Element> elements;
    String markUpTag;
    String texOpenTag;
    String texCloseTag;
    @Override
    public void toMarkdown(StringBuilder sb) {
        sb.append(markUpTag);
        for(Element element : elements) {
            element.toMarkdown(sb);
        }
        sb.append(markUpTag);
    }

    @Override
    public void toTex(StringBuilder sb) {
        sb.append(texOpenTag);
        for(Element element : elements) {
            element.toTex(sb);
        }
        sb.append(texCloseTag);
    }
}
