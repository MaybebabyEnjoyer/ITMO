package markup;

import java.util.List;

public class Strikeout extends Main {
    protected Strikeout(List<Element> elements) {
        this.elements = elements;
        this.markUpTag = markup.get("strikeout");
        this.texOpenTag = tex.get("strikeout");
        this.texCloseTag = tex.get("close");
    }
}
