package net.mosur.spaceagency.service;

import net.mosur.spaceagency.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class UserService {

    public long getUserId(Principal principal) {
        switch (principal.getName()) {
            case ("customer"):
                return 1;
            case ("customer2"):
                return 2;
            case ("manager"):
                return 3;
            default:
                throw new ResourceNotFoundException();
        }
    }
}
