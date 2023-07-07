package markup;

import java.util.List;

public class Paragraph extends Main {
    protected Paragraph(List<Element> elements) {
        this.elements = elements;
        this.markUpTag = markup.get("none");
        this.texOpenTag = markup.get("none");
        this.texCloseTag = markup.get("none");
    }
}
