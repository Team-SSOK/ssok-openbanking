package kr.ssok.ssokopenbanking.authentication.config;
//
//import kr.ssok.ssokopenbanking.authentication.security.ApiKeyAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(authz -> authz
                .requestMatchers("/**").permitAll()
                .anyRequest().authenticated()
        );

        http.csrf(auth->auth.disable());

        return http.build();
    }
}

//@EnableWebSecurity
//public class SecurityConfig {
//    private final ApiKeyAuthFilter apiKeyAuthFilter;
//
//    public SecurityConfig(ApiKeyAuthFilter apiKeyAuthFilter) {
//        this.apiKeyAuthFilter = apiKeyAuthFilter;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/openbank/openapikey").permitAll()
//                        .requestMatchers("/api/openbank/transfers").permitAll()
//                        // 나머지는 인증 필요
//                        .anyRequest().authenticated()
//                )
//                // .addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class) // 필터 등록
//                .build();
//    }
//}
