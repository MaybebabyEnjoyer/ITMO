package markup;

import java.util.List;

public class Emphasis extends Main {
    protected Emphasis(List<Element> elements) {
        this.elements = elements;
        this.markUpTag = markup.get("emphasis");
        this.texOpenTag = tex.get("emphasis");
        this.texCloseTag = tex.get("close");
    }
}
