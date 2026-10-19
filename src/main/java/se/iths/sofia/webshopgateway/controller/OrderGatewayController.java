package se.iths.sofia.webshopgateway.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.iths.sofia.webshopgateway.service.GatewayProxyService;

@RestController
@RequestMapping("/gateway/orders")
public class OrderGatewayController {

    private final GatewayProxyService gatewayProxyService;

    public OrderGatewayController(GatewayProxyService gatewayProxyService) {
        this.gatewayProxyService = gatewayProxyService;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(
            @RequestBody String requestBody,
            @RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = false)
            String contentType,
            @CookieValue(name = "access_token", required = false)
            String accessToken) {

        if (accessToken == null || accessToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return gatewayProxyService.createOrder(
                requestBody, "Bearer " + accessToken, contentType);
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> createCheckout(
            @RequestBody String requestBody,
            @RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = false)
            String contentType,
            @CookieValue(name = "access_token", required = false)
            String accessToken) {

        if (accessToken == null || accessToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return gatewayProxyService.createCheckout(
                requestBody, "Bearer " + accessToken, contentType);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getOrder(
            @PathVariable("id") Long id,
            @CookieValue(name = "access_token", required = false)
            String accessToken) {

        if (accessToken == null || accessToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return gatewayProxyService.getOrder(id, "Bearer " + accessToken);
    }
}
