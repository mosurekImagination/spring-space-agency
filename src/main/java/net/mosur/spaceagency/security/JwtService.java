package net.mosur.spaceagency.security;

import net.mosur.spaceagency.domain.model.User;

import java.util.Optional;

//@Service
public interface JwtService {

    String toToken(User user);

    Optional<String> getSubFromToken(String token);
}
