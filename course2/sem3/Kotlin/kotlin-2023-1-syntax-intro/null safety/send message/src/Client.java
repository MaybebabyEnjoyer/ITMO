import org.jetbrains.annotations.Nullable;

public class Client {
    private final Contact contact;

    public Client(@Nullable Contact contact) {
        this.contact = contact;
    }

    @Nullable
    public Contact getPersonalInfo() {
        return contact;
    }
}
