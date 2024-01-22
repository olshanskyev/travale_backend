package net.travale.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private UserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
// configure AuthenticationManager so that it knows from where to load
// user for matching credentials
// Use BCryptPasswordEncoder
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
// We don't need CSRF for this example
        httpSecurity.csrf().disable()
        .authorizeRequests().
               /* antMatchers("/management/**").hasRole("ADMIN").
                antMatchers("/userInfo").hasAnyRole("ADMIN", "USER").
                antMatchers("/requests/**").hasAnyRole("ADMIN", "USER").
                antMatchers("/requestsCount/**").hasAnyRole("ADMIN", "USER").
                antMatchers("/requestLogs/**").hasAnyRole("ADMIN", "USER").
                antMatchers("/requestTypes").hasAnyRole("ADMIN", "USER").
                antMatchers("/streets/**").permitAll().*/
                antMatchers("/auth/login", "/auth/refresh-token", "/auth/logout", "/auth/register", "/auth/reset-pass", "/auth/request-pass").permitAll() // don't authenticate this particular request

// all other requests need to be authenticated
        .anyRequest().authenticated()
// make sure we use stateless session; session won't be used to
// store user's state.
        .and().exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
// Add a filter to validate the tokens with every request
        .and().addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)

        //use default cors + add delete and put operation
        .cors().configurationSource(request -> {
                CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
                configuration.addAllowedMethod(HttpMethod.DELETE);
                configuration.addAllowedMethod(HttpMethod.PUT);
                return configuration;
        });

        //require ssl
        httpSecurity.requiresChannel().anyRequest().requiresSecure();

    }
}

