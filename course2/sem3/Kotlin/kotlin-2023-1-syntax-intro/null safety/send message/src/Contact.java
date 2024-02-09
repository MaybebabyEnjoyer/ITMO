import org.jetbrains.annotations.NotNull;

public class Contact {
    private final String email;

    public Contact(@NotNull String email) {
        this.email = email;
    }

    @NotNull
    public String getEmail() {
        return email;
    }
}
