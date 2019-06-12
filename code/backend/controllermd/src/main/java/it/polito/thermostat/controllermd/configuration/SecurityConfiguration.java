package it.polito.thermostat.controllermd.configuration;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableConfigurationProperties
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {



    /**
     * Si specifica come si accede alle risorse
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "**").permitAll()//allow CORS option calls
                .and()
                .csrf().disable()
                .authorizeRequests().anyRequest().permitAll();
    }

    /**
     * Invece di usare il service standard usiamo un UserDetailsService da noi definito
     *
     * @param builder
     * @throws Exception
     */
    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {

        //TODO da cancellare ?
        builder.inMemoryAuthentication()
                .withUser("userAd")
                .password(passwordEncoder().encode("pass"))
                .roles("admin")
                .and()
                .withUser("user")
                .password(passwordEncoder().encode("pass"))
                .roles("user");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}