package com.example.dine_in_order_api.service;

import com.example.dine_in_order_api.dto.request.AuthRecord;
import com.example.dine_in_order_api.security.jwt.ClaimName;
import com.example.dine_in_order_api.security.jwt.TokenPayload;
import com.example.dine_in_order_api.security.jwt.TokenType;
import com.example.dine_in_order_api.service.helper.TokenGenerationServiceHelper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class TokenGenerationService{

     private final TokenGenerationServiceHelper tokenGenerationServiceHelper;

     public HttpHeaders grantAccessToken(AuthRecord authRecord){
         Map<String,Object> claim = setClaims(authRecord);

         String accessCookie = tokenGenerationServiceHelper.generateToken(TokenType.ACCESS,claim, Instant.ofEpochMilli(authRecord.accessExpiration()));

         HttpHeaders httpHeaders = new HttpHeaders();
         httpHeaders.add(HttpHeaders.SET_COOKIE,accessCookie);
         return httpHeaders;
     }

     public HttpHeaders grantAccessAndRefreshToken(AuthRecord authRecord){

         Map<String,Object> claims = setClaims(authRecord);
         String accessCookie = tokenGenerationServiceHelper.generateToken(TokenType.ACCESS,claims, Instant.ofEpochMilli(authRecord.accessExpiration()));
         String refreshCookie =tokenGenerationServiceHelper.generateToken(TokenType.REFRESH,claims, Instant.ofEpochMilli(authRecord.refereshExpiration()));

         HttpHeaders httpHeaders = new HttpHeaders();
         httpHeaders.add(HttpHeaders.SET_COOKIE,accessCookie);
         httpHeaders.add(HttpHeaders.SET_COOKIE,refreshCookie);

         return httpHeaders;
     }

     public Map<String,Object> setClaims(AuthRecord authRecord){
         return Map.of(
                       ClaimName.USER_ID,authRecord.userId(),
                       ClaimName.USER_EMAIL,authRecord.email(),
                       ClaimName.USER_role,authRecord.userRole().name());
     }

}
