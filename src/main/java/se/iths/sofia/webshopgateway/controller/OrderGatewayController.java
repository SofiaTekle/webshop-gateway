package se.iths.sofia.webshopgateway.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.iths.sofia.webshopgateway.service.GatewayProxyService;

@RestController
@RequestMapping("/gateway/orders")
@SecurityRequirement(name = "bearerAuth")
public class OrderGatewayController {

    private final GatewayProxyService gatewayProxyService;

    public OrderGatewayController(GatewayProxyService gatewayProxyService) {
        this.gatewayProxyService = gatewayProxyService;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(
            @RequestBody String requestBody,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorization,
            @RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = false)
            String contentType) {

        return gatewayProxyService.createOrder(
                requestBody, authorization, contentType);
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> createCheckout(
            @RequestBody String requestBody,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorization,
            @RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = false)
            String contentType) {

        return gatewayProxyService.createCheckout(
                requestBody, authorization, contentType);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getOrder(
            @PathVariable("id") Long id,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
            String authorization) {

        return gatewayProxyService.getOrder(id, authorization);
    }
}
