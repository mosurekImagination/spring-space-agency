package net.mosur.spaceagency.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//    @Bean
//    public JwtTokenFilter jwtTokenFilter() {
//        return new JwtTokenFilter();
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//
//        http.csrf().disable()
//                .cors()
//                .and()
//                .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
//                .and()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//                .authorizeRequests()
//                .antMatchers(HttpMethod.OPTIONS).permitAll()
//                .antMatchers(HttpMethod.GET, "/articles/feed").authenticated()
//                .antMatchers(HttpMethod.POST, "/users", "/users/login").permitAll()
//                .antMatchers(HttpMethod.GET, "/articles/**", "/profiles/**", "/tags").permitAll()
//                .anyRequest().authenticated();
//
//        http.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and().headers().frameOptions().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

//        auth.inMemoryAuthentication()
//                .withUser("manager")
//                .password("password")
//                .roles("MANAGER")
//                .authorities("MANAGE_MISSIONS", "MANAGE_PRODUCTS")
//                .and()
//                .withUser("customer")
//                .password("password")
//                .authorities("SEARCH_PRODUCTS", "ORDER_PRODUCTS")
//                .roles("CUSTOMER");
//    }

        //hardCoded users for tests without Db
        User.UserBuilder users = User.withDefaultPasswordEncoder();

        auth.inMemoryAuthentication()
                .withUser(users.username("manager").password("1234").roles("MANAGER"))
                .withUser(users.username("customer").password("1234").roles("CUSTOMER"));
    }
}
