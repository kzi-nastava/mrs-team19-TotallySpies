package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    private String secretKey = "";

    public JWTService(){
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGenerator.generateKey();
            secretKey = Base64.getEncoder().encodeToString(sk.getEncoded()); //bytes to string
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public String generateToken(User user){
        Map<String,Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("role", "ROLE_" + user.getRole().name()); // e.g. ROLE_DRIVER
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() +30 * 60 * 1000L)) //expires in 30 minutes
                .and()
                .signWith(getKey()) //key is needed for signing
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes); //converting string to Key
    }


    public String extractEmail(String token) {
        //email need to be extracted from claims in token because it is a part of claims
        return extractClaim(token, Claims::getSubject);
    }
    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }
    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public boolean validateToken(String token, UserDetails userDetails, Date lastPasswordResetDate) {
        //token is valid if it is not expired, after token generation user did not change his password
        // and email from token matches user's from database
        final String email = extractEmail(token);
        final Date issuedAt = extractIssuedAt(token);
        return (email.equals(userDetails.getUsername())
                && !isCreatedBeforeLastPasswordReset(issuedAt,lastPasswordResetDate)
                &&!isTokenExpired(token));
    }
    private Boolean isCreatedBeforeLastPasswordReset(Date issuedAt, Date lastPasswordReset) {
        return (lastPasswordReset != null && issuedAt.before(lastPasswordReset));
    }
    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }
}
