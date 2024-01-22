package net.travale.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.annotation.PostConstruct;
import net.travale.model.JwtResponse;
import net.travale.model.RefreshTokenRequest;
import net.travale.model.TokenPair;
import net.travale.service.JwtUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${jwt.accessTokenValidity}")
    private long ACCESS_TOKEN_VALIDITY;
    @Value("${jwt.refreshTokenValidity}")
    private long REFRESH_TOKEN_VALIDITY;

    @Value("${jwt.passwordResetTokenValidity}")
    private long PASSWORD_RESET_TOKEN_VALIDITY;

    @Value("${jwt.secret}")
    private String secret;

    SecretKey secretKey;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @PostConstruct
    void init() {
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    //retrieve username from jwt token
    String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    //retrieve expiration date from jwt token
    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //for retrieving any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

    }

    //check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    //generate access token for user
    private String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return doGenerateJwtToken(claims, userDetails.getUsername(), ACCESS_TOKEN_VALIDITY * 1000);
    }

    private String generateRefreshToken(UserDetails userDetails) {
        return doGenerateJwtToken(new HashMap<>(), userDetails.getUsername(), REFRESH_TOKEN_VALIDITY * 1000);
    }



    //while creating the token -
//1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
//2. Sign the JWT using the HS512 algorithm and secret key.
//3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
//   compaction of the JWT to a URL-safe string

    //UsersConfig.User user;
    private String doGenerateJwtToken(Map<String, Object> claims, String subject, long expirationMs) {

        return Jwts.builder().claims(claims).subject(subject).issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    //validate token
    Boolean validateAccessToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (ExpiredJwtException exc) {
            // token expired
            logger.warn("Access token expired");
            return false;
        }
    }


    /**
     * generaes new tokens for user
     * @param authentication authentication information
     * @return JwtResponse with access_token and refresh_token
     */
    public JwtResponse generateTokens(Authentication authentication) {
        UserDetails userDetails = (UserDetails)authentication.getPrincipal();
        final String token = generateAccessToken(userDetails);
        final String refreshToken = generateRefreshToken(userDetails);
        TokenPair tokenPair = new TokenPair().setAccessToken(token).setRefreshToken(refreshToken);
        return new JwtResponse(tokenPair);
    }


    /**
     * refreshes tokens after token-refresh opearion
     * @param refreshTokenRequest request with access and refresh token
     * @return new access token
     */
    public JwtResponse refreshTokens(RefreshTokenRequest refreshTokenRequest) throws TokenRefreshException {
        String usernameFromToken;

        // start validating refresh token
        try {
            // this throws ExpiredJwtException so we can't get username
            usernameFromToken = getUsernameFromToken(refreshTokenRequest.getToken().getRefreshToken());
        } catch (ExpiredJwtException exc) {
            // token expired
            logger.warn("Refresh token expired");
            throw new TokenRefreshException("Refresh token expired");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(usernameFromToken);
        final String newToken = generateAccessToken(userDetails);
        TokenPair tokenPair = new TokenPair().setAccessToken(newToken).setRefreshToken(refreshTokenRequest.getToken().getRefreshToken());
        return (new JwtResponse(tokenPair));


    }

    public String generatePasswordResetToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateJwtToken(claims, userName, PASSWORD_RESET_TOKEN_VALIDITY * 1000);
    }

    public Boolean validatePasswordResetToken(String token, String userName) {
        try {
            return getUsernameFromToken(token).equals(userName);
        }  catch (ExpiredJwtException exc) {
            return false;
        } catch (Exception ex) {
            return false;
        }
    }


}
