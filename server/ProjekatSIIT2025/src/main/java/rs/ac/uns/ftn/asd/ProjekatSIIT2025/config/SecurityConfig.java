package rs.ac.uns.ftn.asd.ProjekatSIIT2025.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.filters.JwtFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http){
        //disables csrf checks because jwt does it
        return http.csrf(customizer -> customizer.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                //no one will be able to access any page without authentication except register and login pages
                .authorizeHttpRequests(request -> request
                    .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login",
                            "/api/v1/forgot-password/**","/api/v1/auth/activate", "/api/v1/reviews/**" ," /api/v1/auth/activate-driver")
                    .permitAll()
                    .anyRequest().authenticated()
                    )
                //enables rest api access
                //.httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //first filter is jwtFilter which checks user's token before he gets resources
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build(); //returns SecurityFilterChain object

        //enables default login form
        //.formLogin(Customizer.withDefaults())
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        //using customized user details service, not a default one
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        //provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }
    //CORS-a setting
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "skip"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
