package com.example.dine_in_order_api.service.serviceImpl;

import com.example.dine_in_order_api.config.AppEnv;
import com.example.dine_in_order_api.dto.request.AuthRecord;
import com.example.dine_in_order_api.dto.request.LoginRequest;
import com.example.dine_in_order_api.exception.CustomAuthenticationException;
import com.example.dine_in_order_api.exception.UserNotFoundException;
import com.example.dine_in_order_api.model.User;
import com.example.dine_in_order_api.repository.UserRepository;
import com.example.dine_in_order_api.security.jwt.*;
import com.example.dine_in_order_api.security.util.CookieManager;
import com.example.dine_in_order_api.service.AuthService;
import com.example.dine_in_order_api.service.TokenGenerationService;
import com.example.dine_in_order_api.service.helper.TokenGenerationServiceHelper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final AppEnv appEnv;
    private final JWTService jwtService;
    private final TokenGenerationServiceHelper tokenGenerationServiceHelper;
    private final TokenGenerationService tokenGenerationService;
    private final TokenBlackListService tokenBlackListService;
    private final CookieManager cookieManager;

    @Override
    public AuthRecord login(LoginRequest loginRequest) {

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());

        try {
            Authentication authentication = authenticationManager.authenticate(token);
            if (!authentication.isAuthenticated()) {
                throw new BadCredentialsException("Authentication failed. Invalid credentials.");
            }
            User user = userRepository.findByEmail(loginRequest.email())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found !!"));
            return generateAuthRecord(user);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
    @Override
    public AuthRecord refreshLogin(String refreshToken){
        Claims claims = jwtService.parseToken(refreshToken);

        String email = claims.get(ClaimName.USER_EMAIL, String.class);
        long refereshExpiration = claims.getExpiration().toInstant().toEpochMilli();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found !!"));;

        long accessExpiration = Instant.now().plusSeconds(3600).toEpochMilli();

        AuthRecord authRecord = new AuthRecord(
                user.getUserid(),
                user.getUsername(),
                email,
                user.getUserrole(),
                accessExpiration,
                refereshExpiration
        );

        return authRecord;
    }

    @Override
    public HttpHeaders logout(String refreshToken, String accessToken) {
        tokenBlackListService.blackListToken(refreshToken);
        tokenBlackListService.blackListToken(accessToken);

        String refreshCookie = cookieManager.generateCookie(TokenType.REFRESH.type(), "",0);
        String accessCookie =  cookieManager.generateCookie(TokenType.ACCESS.type(), "",0);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.SET_COOKIE,refreshCookie);
        httpHeaders.add(HttpHeaders.SET_COOKIE,accessCookie);
        return httpHeaders;
    }

    private AuthRecord  generateAuthRecord(User user) {
        Instant now = Instant.now();
        long accessExpiration = now.plusSeconds(appEnv.getSecurity().getTokenValidity().getAccessValidity()).toEpochMilli();
        long refreshExpiration =now.plusSeconds(appEnv.getSecurity().getTokenValidity().getRefreshValidity()).toEpochMilli();

        AuthRecord authRecord = new AuthRecord(
                        user.getUserid(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getUserrole(),
                        accessExpiration,
                        refreshExpiration
                );
        return authRecord;
    }
}
