package se.iths.sofia.webshopgateway.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.iths.sofia.webshopgateway.service.GatewayProxyService;

@RestController
@RequestMapping("/gateway")

public class ProductGatewayController {

    private final GatewayProxyService proxyService;

    public ProductGatewayController(GatewayProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @GetMapping("/products")
    public ResponseEntity<String> products(
            @CookieValue(name = "access_token", required = false)
            String accessToken) {

        if (accessToken == null || accessToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return proxyService.products("Bearer " + accessToken);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<String> productById(@PathVariable Long id,
                                              @CookieValue(name = "access_token", required = false)
                                              String accessToken) {

        if (accessToken == null || accessToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return proxyService.productById(id, "Bearer " + accessToken);
    }

    @PostMapping("/products")
    public ResponseEntity<String> addProduct(@RequestBody String requestBody,
                                             @RequestHeader(HttpHeaders.CONTENT_TYPE) String contentType,
                                             @CookieValue(name = "access_token", required = false)
                                             String accessToken) {

        if (accessToken == null || accessToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return proxyService.createProduct(requestBody, "Bearer " + accessToken, contentType);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> removeProduct(@PathVariable Long id,
                                                @CookieValue(name = "access_token", required = false)
                                                String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return proxyService.removeProduct("Bearer " + accessToken, id);
    }
}
