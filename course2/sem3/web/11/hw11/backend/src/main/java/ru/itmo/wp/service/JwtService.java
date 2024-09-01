package ru.itmo.wp.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;
import ru.itmo.wp.domain.User;

@Service
public class JwtService {
    private static final String SECRET = "38c86b553adefeb3e579e39789e38664aef9176e";
    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET);
    private static final JWTVerifier verifier = JWT.require(algorithm).build();

    private final UserService userService;

    public JwtService(UserService userService) {
        this.userService = userService;
    }

    public String create(User user) {
        try {
            return JWT.create()
                    .withClaim("userId", user.getId())
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new RuntimeException("Can't create JWT.");
        }
    }

    public User find(String jwt) {
        try {
            DecodedJWT decodedJwt = verifier.verify(jwt);
            return userService.findById(decodedJwt.getClaim("userId").asLong());
        } catch (JWTVerificationException exception){
            return null;
        }
    }
}
