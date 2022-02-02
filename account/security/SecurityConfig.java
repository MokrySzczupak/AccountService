package account.security;

import account.exception.AccessDeniedHandlerImp;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final DataSource dataSource;
    private final AccessDeniedHandlerImp accessDeniedHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
        auth.jdbcAuthentication().dataSource(dataSource);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().headers().frameOptions().disable();
        http.httpBasic()
                .and()
                .authorizeRequests()
                .mvcMatchers(HttpMethod.POST ,"/api/auth/signup").permitAll()
                .mvcMatchers(HttpMethod.PUT ,"/api/acct/payments").hasRole("ACCOUNTANT")
                .mvcMatchers(HttpMethod.POST ,"/api/auth/changepass").authenticated()
                .mvcMatchers(HttpMethod.GET ,"/api/empl/payment").hasAnyRole("USER", "ACCOUNTANT")
                .mvcMatchers(HttpMethod.POST ,"/api/acct/payments").hasRole("ACCOUNTANT")
                .mvcMatchers(HttpMethod.GET ,"/api/admin/user").hasRole("ADMINISTRATOR")
                .mvcMatchers(HttpMethod.DELETE ,"/api/admin/user/**").hasRole("ADMINISTRATOR")
                .mvcMatchers(HttpMethod.PUT ,"/api/admin/user/role").hasRole("ADMINISTRATOR")
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .accessDeniedHandler(accessDeniedHandler);
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder(13);
    }
}
