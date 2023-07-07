package markup;

public class Text implements Element {
    public String value;
    protected Text (String value) {
        this.value = value;
    }
    @Override
    public void toMarkdown(StringBuilder sb) {
        sb.append(value);
    }

    @Override
    public void toTex(StringBuilder sb) {
        sb.append(value);
    }
}
