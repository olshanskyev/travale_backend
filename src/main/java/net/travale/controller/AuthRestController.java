package net.travale.controller;


import net.travale.model.*;
import net.travale.service.UserService;
import net.travale.utils.JwtTokenUtil;
import net.travale.utils.TokenRefreshException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenUtil tokenUtil;


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody JwtRequest authenticationRequest) throws Exception {

        //authenticate via email and password using authenticationManager
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
            JwtResponse jwtResponse = tokenUtil.generateTokens(authentication);
            return ResponseEntity.ok(jwtResponse);
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse("badCredentials"), HttpStatus.UNAUTHORIZED);
        }
    }


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody JwtRequest registerRequest) {
        if (userService.alreadyExists(registerRequest.getUsername())){
            return new ResponseEntity<>(new ErrorResponse("userAlreadyExists", new HashMap<>() {{
                put("user", registerRequest.getUsername());
            }}), HttpStatus.CONFLICT);
        }


        // todo create new user
        return null;
    }

    @RequestMapping(value = "/refresh-token", method = RequestMethod.POST)
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            return ResponseEntity.ok(tokenUtil.refreshTokens(refreshTokenRequest));
        } catch (TokenRefreshException ex) {
            return new ResponseEntity<>("tokenRefreshError", HttpStatus.UNAUTHORIZED);
        }

    }

    @RequestMapping(value = "/logout", method = RequestMethod.DELETE)
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("");
    }

    @RequestMapping(value = "/reset-pass", method = RequestMethod.PUT)
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {

        if (resetPasswordRequest.getToken() == null || resetPasswordRequest.getToken().isEmpty()){
            return new ResponseEntity<>(new ErrorResponse("emptyPasswordResetToken", null), HttpStatus.NOT_FOUND);
        }

        Optional<User> byPasswordResetToken = userService.findByPasswordResetToken(resetPasswordRequest.getToken());
        if (byPasswordResetToken.isPresent()){
            if (resetPasswordRequest.getPassword().equals(resetPasswordRequest.getConfirmPassword())){
                // validate token
                if (!tokenUtil.validatePasswordResetToken(resetPasswordRequest.getToken(), byPasswordResetToken.get().getUserName())){
                    return new ResponseEntity<>(new ErrorResponse("tokenNotValidOrExpired", null), HttpStatus.CONFLICT);
                }
                userService.setNewPassword(byPasswordResetToken.get(), resetPasswordRequest.getPassword());
            } else {
                return new ResponseEntity<>(new ErrorResponse("passwordsNotMatch", null), HttpStatus.CONFLICT);
            }
        } else {
            return new ResponseEntity<>(new ErrorResponse("resetPasswordRequestNotFound", null), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok("");
    }


    @RequestMapping(value = "/request-pass", method = RequestMethod.POST)
    public ResponseEntity<?> requestPassword(@RequestBody RequestPasswordRequest requestPasswordRequest) {
        Optional<User> byUserName = userService.findByUserName(requestPasswordRequest.getEmail());
        if (byUserName.isPresent()){
            userService.requestPasswordReset(byUserName.get());
        } else {
            return new ResponseEntity<>(new ErrorResponse("userNotFound", new HashMap<String, String>() {{
                put("user", requestPasswordRequest.getEmail());
            }}), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok("");
    }
}
