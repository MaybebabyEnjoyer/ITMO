package markup;

public interface Element {
    void toMarkdown(StringBuilder sb);
    void toTex(StringBuilder sb);
}
