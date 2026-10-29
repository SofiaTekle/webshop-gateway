package se.iths.sofia.webshopgateway.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.iths.sofia.webshopgateway.service.GatewayProxyService;

/**
 * Tar emot produktanrop och skickar JWT från cookien vidare som Bearer-token.
 */
@RestController
@RequestMapping("/gateway")

public class ProductGatewayController {

    private final GatewayProxyService proxyService;

    public ProductGatewayController(GatewayProxyService proxyService) {
        this.proxyService = proxyService;
    }

    /**
     * Vidarebefordrar hämtning av produkter till product-service.
     *
     * @param accessToken JWT-token från cookien, utan Bearer-prefix
     * @return status 401 om token saknas eller är tom, annars svaret från proxytjänsten
     */
    @GetMapping("/products")
    public ResponseEntity<String> products(
            @CookieValue(name = "access_token", required = false)
            String accessToken) {

        if (accessToken == null || accessToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return proxyService.products("Bearer " + accessToken);
    }

    /**
     * Vidarebefordrar hämtning av en specifik produkt.
     *
     * @param id produktens ID
     * @param accessToken JWT-token från cookien, utan Bearer-prefix
     * @return status 401 om token saknas eller är tom, annars svaret från proxytjänsten
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<String> productById(@PathVariable Long id,
                                              @CookieValue(name = "access_token", required = false)
                                              String accessToken) {

        if (accessToken == null || accessToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return proxyService.productById(id, "Bearer " + accessToken);
    }

    /**
     * Vidarebefordrar produktuppgifterna för att skapa en produkt.
     * Product-service kontrollerar användarens behörighet.
     *
     * @param requestBody uppgifterna som skickas vidare som JSON
     * @param contentType formatet på innehållet, exempelvis application/json
     * @param accessToken JWT-token från cookien, utan Bearer-prefix
     * @return status 401 om token saknas eller är tom, annars svaret från proxytjänsten
     */
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

    /**
     * Vidarebefordrar begäran att radera en produkt.
     * Product-service kontrollerar användarens behörighet.
     *
     * @param id ID för produkten som ska raderas
     * @param accessToken JWT-token från cookien, utan Bearer-prefix
     * @return status 401 om token saknas eller är tom, annars svaret från proxytjänsten
     */
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
