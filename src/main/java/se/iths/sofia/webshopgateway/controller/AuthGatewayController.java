package se.iths.sofia.webshopgateway.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.iths.sofia.webshopgateway.service.GatewayProxyService;

/**
 * Tar emot anrop för login, registrering, logout och hämtning av inloggad användare.
 */
@RestController
@RequestMapping("/gateway")
public class AuthGatewayController {
    private final GatewayProxyService gatewayProxyService;

    public AuthGatewayController(GatewayProxyService gatewayProxyService) {
        this.gatewayProxyService = gatewayProxyService;
    }

    /**
     * Tar emot inloggningsuppgifter och låter proxytjänsten sätta JWT-cookien.
     *
     * @param requestBody uppgifterna som skickas vidare som JSON
     * @param contentType formatet på innehållet, exempelvis application/json
     * @return användaruppgifter och cookie vid lyckad inloggning, annars ett felsvar
     */
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(
            @RequestBody String requestBody,
            @RequestHeader(HttpHeaders.CONTENT_TYPE) String contentType) {

        return gatewayProxyService.loginWithCookie(requestBody, contentType);
    }

    /**
     * Vidarebefordrar registreringen till auth-service.
     *
     * @param requestBody uppgifterna som skickas vidare som JSON
     * @param contentType formatet på innehållet, exempelvis application/json
     * @return HTTP-svaret med statuskod och body, eller ett felsvar om anropet misslyckas
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody String requestBody,
            @RequestHeader(HttpHeaders.CONTENT_TYPE) String contentType) {

        return gatewayProxyService.register(requestBody, contentType);
    }

    /**
     * Returnerar instruktionen som tar bort JWT-cookien i webbläsaren.
     *
     * @return status 204 utan body och en Set-Cookie-header
     */
    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() {
        return gatewayProxyService.logout();
    }

    /**
     * Läser JWT-cookien och hämtar användaruppgifterna via auth-service.
     * Token verifieras i auth-service, inte i denna controller.
     *
     * @param accessToken token från cookien, eller null om cookien saknas
     * @return HTTP-svaret med statuskod och body, eller ett felsvar om anropet misslyckas
     */
    @GetMapping("/auth/me")
    public ResponseEntity<String> session(
            @CookieValue(
                    name = "access_token",
                    required = false
            )String accessToken
    ){
        return gatewayProxyService.session(accessToken);
    }
}
