package se.iths.sofia.webshopgateway.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.iths.sofia.webshopgateway.service.GatewayProxyService;

@RestController
@RequestMapping("/gateway")
public class AuthGatewayController {
    private final GatewayProxyService gatewayProxyService;

    public AuthGatewayController(GatewayProxyService gatewayProxyService) {
        this.gatewayProxyService = gatewayProxyService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(
            @RequestBody String requestBody,
            @RequestHeader(HttpHeaders.CONTENT_TYPE) String contentType) {

        return gatewayProxyService.loginWithCookie(requestBody, contentType);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody String requestBody,
            @RequestHeader(HttpHeaders.CONTENT_TYPE) String contentType) {

        return gatewayProxyService.register(requestBody, contentType);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() {
        return gatewayProxyService.logout();
    }

}
