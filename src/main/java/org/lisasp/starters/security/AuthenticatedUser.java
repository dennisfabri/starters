package org.lisasp.starters.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import java.util.Optional;
import org.lisasp.starters.data.entity.User;
import org.lisasp.starters.data.service.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AuthenticatedUser {

    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;

    public AuthenticatedUser(AuthenticationContext authenticationContext, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
    }

    @Transactional
    public Optional<User> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(userDetails -> userRepository.findByUsername(userDetails.getUsername()));
    }

    public void logout() {
        // UI.getCurrent().getPage().setLocation(SecurityConfiguration.LOGOUT_URL);
        // SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        // logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);

        authenticationContext.logout();
    }

}
