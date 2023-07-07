package markup;

import java.util.List;

public class Strong extends Main {
    protected Strong(List<Element> elements) {
        this.elements = elements;
        this.markUpTag = markup.get("strong");
        this.texOpenTag = tex.get("strong");
        this.texCloseTag = tex.get("close");
    }
}
