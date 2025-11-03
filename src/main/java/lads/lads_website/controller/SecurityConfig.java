package lads.lads_website.controller;

import lads.lads_website.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                // Normal http requests
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers(
                            // Activity Love Interest Controller
                            "/activityLoveInterest/**",
                            // Activity Run Period Controller
                            "/activityRunPeriod/**",
                            // Banner Controller
                            "/banner/**",
                            // Banner Category Controller
                            "/bannerCategory/**",
                            // Card Controller
                            // Card Pull Controller
                            "/cardPull/**",
                            // Event Controller
                            "/event/**",
                            // Leveling Planning Controller
                            "/levelingPlanning/**",
                            // Player Card Controller
                            "/playerCard/**",
                            // Player Bounty Controller
                            "/playerBounty/**",
                            // Resource Tracking Controller
                            "/resourceTracking/**",
                            // Misc.
                            "/error", "/"
                    ).authenticated();
                })
                // Javascript/CSS files
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers(
                            "/js/CommonMethods.js",
                            "/js/activityRunPeriod/**",
                            "/js/banner/**",
                            "/js/card/**",
                            "/js/cardPull/**",
                            "/js/event/**",
                            "/js/levelingPlanning/**",
                            "/js/playerBounty/**",
                            "/js/playerCard/**",
                            "/js/resourceTracking/**"
                    ).authenticated();
                })
                // Admin only
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers(
                            "/activityRunPeriod/add", "/activityRunPeriod/addNewRunPeriod",
                            "/activityRunPeriod/validateRunPeriodForm",
                            "/banner/add", "/banner/validateBannerForm",
                            "/card/add", "/card/addCard", "/card/validateCardForm",
                            "/event/add", "event/addEvent", "/event/validateEventForm"
                    ).hasAuthority("ADMIN");
                })
                .authorizeHttpRequests(registry->{
                    registry.requestMatchers("/register", "/home", "/player/register").permitAll();
                })
                .formLogin(httpSecurityFormLoginConfigurer -> {
                    httpSecurityFormLoginConfigurer
                            .loginPage("/login")
                            .loginProcessingUrl("/login")
                            .defaultSuccessUrl("/home", true)
                            .permitAll();
                })
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher("/logout"))
                        .deleteCookies("JSESSIONID")
                        .permitAll()); // Ensure logout is permitted
        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
    }
}
