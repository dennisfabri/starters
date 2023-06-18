package org.lisasp.starters.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.lisasp.starters.views.login.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(registry -> {
            // Statically served images
            registry.requestMatchers(new AntPathRequestMatcher("/images/*.png")).permitAll();
            // Icons from the line-awesome addon
            registry.requestMatchers(new AntPathRequestMatcher("/line-awesome/**/*.svg")).permitAll();

            registry.requestMatchers(new AntPathRequestMatcher("/starter/**")).authenticated();
            registry.requestMatchers(new AntPathRequestMatcher("/team/**")).authenticated();
            registry.requestMatchers(new AntPathRequestMatcher("/export/**")).hasAnyRole("ADMIN");
        });
        super.configure(http);
        setLoginView(http, LoginView.class);
    }

}
