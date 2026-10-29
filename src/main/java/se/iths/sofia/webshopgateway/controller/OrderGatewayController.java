package se.iths.sofia.webshopgateway.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.iths.sofia.webshopgateway.service.GatewayProxyService;

/**
 * Tar emot orderanrop och skickar dem vidare med JWT från användarens cookie.
 */
@RestController
@RequestMapping("/gateway/orders")
public class OrderGatewayController {

    private final GatewayProxyService gatewayProxyService;

    public OrderGatewayController(GatewayProxyService gatewayProxyService) {
        this.gatewayProxyService = gatewayProxyService;
    }

    /**
     * Vidarebefordrar beställningen till order-service för orderskapning.
     *
     * @param requestBody uppgifterna som skickas vidare som JSON
     * @param contentType innehållets format, eller null om headern saknas
     * @param accessToken JWT-token från cookien, utan Bearer-prefix
     * @return status 401 om token saknas eller är tom, annars svaret från proxytjänsten
     */
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

    /**
     * Vidarebefordrar beställningen till order-service för att starta Stripe Checkout.
     *
     * @param requestBody uppgifterna som skickas vidare som JSON
     * @param contentType innehållets format, eller null om headern saknas
     * @param accessToken JWT-token från cookien, utan Bearer-prefix
     * @return status 401 om token saknas eller är tom, annars svaret från proxytjänsten
     */
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

    /**
     * Vidarebefordrar hämtning av en beställning via ID.
     *
     * @param id beställningens ID
     * @param accessToken JWT-token från cookien, utan Bearer-prefix
     * @return status 401 om token saknas eller är tom, annars svaret från proxytjänsten
     */
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
