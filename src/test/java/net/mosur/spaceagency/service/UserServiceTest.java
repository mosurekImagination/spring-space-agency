package net.mosur.spaceagency.service;

import net.mosur.spaceagency.domain.exception.ResourceNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Principal;

import static org.junit.Assert.assertTrue;

@DataJpaTest
@RunWith(SpringRunner.class)
@Import(UserService.class)
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    public void should_return_id_for_customer() {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("customer");
        long id = userService.getUserId(mockPrincipal);
        assertTrue(id > 0);
    }

    @Test
    public void should_return_id_for_manager() {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("manager");
        long id = userService.getUserId(mockPrincipal);
        assertTrue(id > 0);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void should_return_id_for_random_user_fail() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn("qwerqwer");
        long id = userService.getUserId(mockPrincipal);
    }
}