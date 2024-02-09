import org.jetbrains.annotations.NotNull;

public interface Mailer {
    void sendMessage(@NotNull String email, @NotNull String message);
}
