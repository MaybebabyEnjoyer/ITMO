package ru.itmo.web.hw4.util;

import ru.itmo.web.hw4.model.Post;
import ru.itmo.web.hw4.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DataUtil {
    private static final List<User> USERS = Arrays.asList(
            new User(1, "MikeMirzayanov", "Mike Mirzayanov", User.Color.RED),
            new User(6, "pashka", "Pavel Mavrin", User.Color.BLUE),
            new User(9, "geranazavr555", "Georgiy Nazarov", User.Color.GREEN),
            new User(11, "tourist", "Gennady Korotkevich", User.Color.RED)
    );

    private static final List<Post> POSTS = Arrays.asList(
            new Post(1, "Hello", "Hello, world", 1),
            new Post(2, "Test", "Test test test test", 6),
            new Post(5, "Lorem", "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Corporis earum eveniet in maiores omnis tempora. A accusantium architecto blanditiis deleniti dolor doloremque, dolores dolorum ea ex expedita facere facilis incidunt ipsam iusto laboriosam laborum minima, modi molestias mollitia neque numquam odio perspiciatis quae quaerat quasi, quidem recusandae sapiente sunt totam ullam unde vero voluptatem voluptates. Aspernatur consequuntur dicta ex excepturi nulla? Dolor fugiat, ipsum laudantium neque placeat tempore totam vitae. Aut beatae cumque dolore eius ipsa iure magnam molestias nam natus nemo nobis odit pariatur praesentium quos reiciendis sequi sunt temporibus tenetur, totam voluptatem! Aliquam aliquid aspernatur labore? Reprehenderit, rerum.\n",
                    11),
            new Post(87, "What?", "What happened?", 9),
            new Post(76, "New Homework", "Выложили новую домашку, решайте!!!", 1)
    );

    public static void addData(HttpServletRequest request, Map<String, Object> data) {
        data.put("users", USERS);
        data.put("posts", POSTS);

        for (User user : USERS) {
            if (Long.toString(user.getId()).equals(request.getParameter("logged_user_id"))) {
                data.put("user", user);
            }
        }
        data.put("link", request.getRequestURI());
    }
}
