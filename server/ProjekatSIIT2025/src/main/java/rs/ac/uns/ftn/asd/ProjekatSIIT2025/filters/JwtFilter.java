package rs.ac.uns.ftn.asd.ProjekatSIIT2025.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.JWTService;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.UserService;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

//for every request, this filter will be executed once
//if this filter is successful , then it will forward to UsernamePasswordAuthenticationFilter
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    JWTService jwtService;

    @Autowired
    ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")){ //token in request from user starts with Bearer
            token = authHeader.substring(7); //skip the Bearer word
            email = jwtService.extractEmail(token);
        }
        //check if is already authenticated
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null){
            //gets email and other user details from database
            UserDetails userDetails = context.getBean(UserService.class).loadUserByUsername(email);
            User user = (User) userDetails;
            Date lastPasswordResetDate = user.getLastPasswordResetDate();
            if(jwtService.validateToken(token,userDetails,lastPasswordResetDate)){
                //if token is valid, next filter is used
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //adding the token in the chain
                //it tells Spring Security that For THIS request, the currently logged-in user is
                // userDetails with these authorities
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/v1/auth/")
                || path.startsWith("/api/v1/forgot-password/");
    }
}
