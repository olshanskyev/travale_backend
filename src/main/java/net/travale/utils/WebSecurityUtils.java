package net.travale.utils;


import io.jsonwebtoken.ExpiredJwtException;
import javax.servlet.http.HttpServletRequest;
import net.travale.model.Role;
import net.travale.model.User;
import net.travale.service.ExtendedUserDetails;
import net.travale.service.JwtUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import java.util.*;


@Component
public class WebSecurityUtils {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(WebSecurityUtils.class);

    /**
     *
     * @param request
     * @return Authentication if success or null if something went wrong
     */
    public Authentication authenticateRequest(HttpServletRequest request){

        String username = null;
        String jwtToken = null;
        final String requestTokenHeader = request.getHeader("Authorization");

// JWT Token is in the form "Bearer token". Remove Bearer word and get
// only the Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                logger.error("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                logger.error("JWT Token has expired");
            }
        } else {
            logger.info("JWT Token does not begin with Bearer String");
        }

// Once we get the token validate it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

// if token is valid configure authentication that should be set in Spring
            if (jwtTokenUtil.validateAccessToken(jwtToken, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                return usernamePasswordAuthenticationToken;
            }
        }
        return null;
    }


    /**
     *
     * @return authorized user or else null
     */
    public User getAuthorizedUser() {
        Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authentication instanceof ExtendedUserDetails)
            return ((ExtendedUserDetails)authentication).getUser();
        else
            return null;
    }


    public boolean isAdmin() {
        return authorizedUserHasAnyRole(new HashSet<>(Collections.singletonList(Role.ROLE_ADMIN)));
    }

    public boolean authorizedUserHasAnyRole(Set<Role> roles) {
        Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authentication instanceof UserDetails) {
            return ((UserDetails) authentication).getAuthorities().stream().map(item -> Role.valueOf(item.getAuthority())).anyMatch(roles::contains);
        }
         else
            return false;
    }

}
