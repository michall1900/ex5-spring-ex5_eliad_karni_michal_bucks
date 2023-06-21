package hac.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class ApplicationConfig  {

    @Bean
    @Scope("singleton")
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder bCryptPasswordEncoder) {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("1")
                .password(bCryptPasswordEncoder.encode("1"))
                .roles("USER")
                .build());
        manager.createUser(User.withUsername("2")
                .password(bCryptPasswordEncoder.encode("2"))
                .roles("USER")
                .build());
        manager.createUser(User.withUsername("0")
                .password(bCryptPasswordEncoder.encode("0"))
                .roles("ADMIN")
                .build());
        manager.createUser(User.withUsername("useradmin")
                .password(bCryptPasswordEncoder.encode("password"))
                .roles("USER", "ADMIN")
                .build());
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(withDefaults())

                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/register", "/login").anonymous()
                        .requestMatchers("/css/**","/images/**","/javascripts/**", "/", "/403", "/errorpage", "/how-to-play", "/simulateError", "/game/init").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/shared/**").hasAnyRole("USER", "ADMIN")
                )
                .formLogin((form) -> form
                                .loginPage("/login")
                                .defaultSuccessUrl("/", true)
                )
                .logout(LogoutConfigurer::permitAll)
                .exceptionHandling(
                        (exceptionHandling) -> exceptionHandling
                                .accessDeniedPage("/403")
                )

        ;

        return http.build();

    }


    // instead of defining an open path in the method above, you can do it here:
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/favicon.ico");
    }

}
