package br.com.tamanhofamilia.idadmin.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.*;
import java.util.stream.Collectors;

@EnableWebSecurity
public class WebConfig extends WebSecurityConfigurerAdapter {
    private Converter<Jwt, Collection<GrantedAuthority>> jwtConverter =
            source-> ((List<String>) Optional.ofNullable((Map<String, Object>)source.getClaim("realm_access"))
            .orElse(Collections.emptyMap())
            .get("roles"))
            .stream().map(n -> "ROLE_" + n)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtConverter);
        http
                .cors().disable()
                .csrf().disable()
            .authorizeRequests()
                .anyRequest()
                .authenticated()
            .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                ;
    }
}
