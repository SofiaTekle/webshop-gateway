package se.iths.sofia.webshopgateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthGatewayService {

    private final String authServerUrl;

    public AuthGatewayService(
            @Value("${app.auth-server-url}") String authServerUrl) {
        this.authServerUrl = authServerUrl;
    }
//
//    public ResponseEntity<String> login(String requestBody, String contentType) {
//        return " ";
//    }


}
