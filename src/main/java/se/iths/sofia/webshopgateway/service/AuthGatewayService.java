package se.iths.sofia.webshopgateway.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthGatewayService {

    private final GatewayProxyService gatewayProxyService;

    public AuthGatewayService(GatewayProxyService gatewayProxyService) {
        this.gatewayProxyService = gatewayProxyService;
    }

    public ResponseEntity<String> login(String requestBody, String contentType) {
        return gatewayProxyService.login(requestBody, contentType);
    }

    public ResponseEntity<String> register(String requestBody, String contentType) {
        return gatewayProxyService.register(requestBody, contentType
        );
    }

}
