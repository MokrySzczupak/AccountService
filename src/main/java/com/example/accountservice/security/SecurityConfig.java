package com.example.accountservice.security;

import com.example.accountservice.exception.AccessDeniedHandlerImp;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final AccessDeniedHandlerImp accessDeniedHandler;
    private final AuthenticationEntryPointImp authenticationEntryPoint;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().headers().frameOptions().disable();
        http.cors();
        http.httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .authorizeRequests()
                .mvcMatchers(HttpMethod.POST, "/actuator/shutdown").permitAll() // for tests reason
                .mvcMatchers(HttpMethod.POST ,"/api/auth/signup").permitAll()
                .mvcMatchers(HttpMethod.PUT ,"/api/acct/payments").hasRole("ACCOUNTANT")
                .mvcMatchers(HttpMethod.POST ,"/api/auth/changepass")
                    .hasAnyRole("ACCOUNTANT", "USER", "ADMINISTRATOR")
                .mvcMatchers(HttpMethod.GET ,"/api/empl/payment")
                    .hasAnyRole("USER", "ACCOUNTANT", "ADMINISTRATOR", "AUDITOR")
                .mvcMatchers(HttpMethod.POST ,"/api/acct/payments").hasRole("ACCOUNTANT")
                .mvcMatchers(HttpMethod.GET ,"/api/admin/user").hasRole("ADMINISTRATOR")
                .mvcMatchers(HttpMethod.DELETE ,"/api/admin/user/**").hasRole("ADMINISTRATOR")
                .mvcMatchers(HttpMethod.PUT ,"/api/admin/user/role").hasRole("ADMINISTRATOR")
                .mvcMatchers(HttpMethod.PUT ,"/api/admin/user/access").hasRole("ADMINISTRATOR")
                .mvcMatchers(HttpMethod.GET ,"/api/security/events").hasRole("AUDITOR")
                .mvcMatchers(HttpMethod.GET ,"/api/acct/payments").hasRole("ACCOUNTANT")
                .and()
                .formLogin()
                .defaultSuccessUrl("/success")
                .failureForwardUrl("/failure")
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler);
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder(13);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
