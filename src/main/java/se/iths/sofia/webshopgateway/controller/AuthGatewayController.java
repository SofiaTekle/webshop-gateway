package se.iths.sofia.webshopgateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.iths.sofia.webshopgateway.service.AuthGatewayService;

@RestController
@RequestMapping("/gateway")

public class AuthGatewayController {
    private final AuthGatewayService authGatewayService;

    public AuthGatewayController(AuthGatewayService authGatewayService) {
        this.authGatewayService = authGatewayService;
    }


    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestBody String requestBody, HttpServletRequest request) {
        return authGatewayService.login(
                requestBody,
                request.getHeader(HttpHeaders.CONTENT_TYPE));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody String requestBody, HttpServletRequest request) {
        return authGatewayService.register(
                requestBody,
                request.getHeader(HttpHeaders.CONTENT_TYPE)
        );
    }

}
