package com.codimen.lendit.security;

import com.codimen.lendit.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationService authenticationService;

    private static BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authenticationService).passwordEncoder(getPasswordEncoder());
    }

    public static BCryptPasswordEncoder getPasswordEncoder() {
        if(bCryptPasswordEncoder == null){
            bCryptPasswordEncoder = new BCryptPasswordEncoder(5);
        }
        return bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf().ignoringAntMatchers("/api/auth/sign-in","/api/auth/is-authenticated",
                "/api/user/register-user","/api/user/validate-email",
                "/api/account/get-banks","/api/account/get-branch-by-id","/api/account/get-branch-by-bank"
                ,"/api/auth/forgot-password","/api/auth/reset-password","/api/auth/confirm-email");

        httpSecurity
                .authorizeRequests()
                    //Allowing Public Apis
                    .antMatchers("/api/auth/sign-in","/api/auth/is-authenticated",
                            "/api/user/register-user","/api/user/validate-email",
                            "/api/auth/forgot-password",
                            "/api/auth/confirm-email","/api/auth/reset-password").permitAll()
                    //Allowing Swagger
                    .antMatchers("/common/**", "/v2/api-docs", "/configuration/ui", "/swagger-resources",
                            "/configuration/security", "/swagger-ui.html", "/webjars/**").permitAll()
                    .antMatchers("/api/admin*//*").access("hasAuthority('SUPER_ADMIN')")
                    .antMatchers("/**").fullyAuthenticated()
                .and()
                .logout()
                    .logoutUrl("/signout")
                    .logoutRequestMatcher(new AntPathRequestMatcher("/signout"))
                    .logoutSuccessHandler(new CustomLogoutSuccessHandler())
                    .clearAuthentication(true)
                    .deleteCookies("XSRF-TOKEN")
                    .invalidateHttpSession(true)
                .and()
                    .exceptionHandling()
                    .accessDeniedPage("/error?error=access_denied");

        CookieCsrfTokenRepository csrfTokenRepository = new CookieCsrfTokenRepository();
        csrfTokenRepository.setCookieHttpOnly(true);
        httpSecurity.csrf().csrfTokenRepository(csrfTokenRepository);

    }

    @Bean
    public UserLogInDetailsInMemory userLogInDetailsInMemory(){
        return UserLogInDetailsInMemory.getInstance();
    }
}
