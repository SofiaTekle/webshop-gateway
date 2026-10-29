package se.iths.sofia.webshopgateway.service;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import se.iths.sofia.webshopgateway.dto.AuthServiceResponse;
import se.iths.sofia.webshopgateway.dto.LoginResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Vidarebefordrar anrop till auth-, product- och order-service.
 * Hanterar JWT-cookien, medan mikrotjänsterna verifierar token och utför affärslogiken.
 */
@Service
public class GatewayProxyService {
    private final String authServerUrl;
    private final String productServiceUrl;
    private final String orderServiceUrl;
    private final HttpClient httpClient;
    private final boolean cookieSecure;
    private final String cookieSameSite;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GatewayProxyService(
            @Value("${app.auth-server-url}") String authServerUrl,
            @Value("${app.product-service-url}") String productServiceUrl,
            @Value("${app.order-service-url}") String orderServiceUrl,
            @Value("${app.cookie.secure:false}") boolean cookieSecure,
            @Value("${app.cookie.same-site:Lax}") String cookieSameSite,
            HttpClient httpClient) {
        this.authServerUrl = authServerUrl;
        this.productServiceUrl = productServiceUrl;
        this.orderServiceUrl = orderServiceUrl;
        this.cookieSecure = cookieSecure;
        this.cookieSameSite = cookieSameSite;
        this.httpClient = httpClient;
    }

    /**
     * Hämtar produkterna från product-service endpoint /products
     *
     * @param authorization Bearer-token för autentisering hos product-service
     * @return http-svaret från product-service med statuskod och body
     */
    public ResponseEntity<String> products(String authorization) {
    /**
     * Bygger och skickar ett GET-anrop till den angivna tjänsten.
     *
     * @param baseUrl tjänstens grundadress
     * @param path sökvägen som läggs till efter grundadressen
     * @param authorization hela Bearer-värdet, eller null om anropet inte behöver token
     * @return HTTP-svaret med statuskod och body, eller ett felsvar om anropet misslyckas
     */
        ResponseEntity<String> getResponse = forwardGet(productServiceUrl, "/products", authorization);

        return getResponse;
    }

    /**
     * Hämtar en specifik produkt från product-service med hjälp av ID från endpointen /products/{id}
     *
     * @param id  vilken produkt vi hämtar
     * @param authorization Bearer-token för autentisering hos product-service
     * @return http-svaret från product-service med statuskod och body
     */
    public ResponseEntity<String> productById(Long id, String authorization) {
        ResponseEntity<String> getResponse = forwardGet(productServiceUrl, "/products/" + id, authorization);
        return getResponse;
    }


    /**
     * Skickar beställningen till order-service för att skapa en order.
     *
     * @param requestBody uppgifterna som skickas vidare som JSON
     * @param authorization hela Authorization-värdet, inklusive Bearer och token
     * @param contentType formatet på innehållet, exempelvis application/json
     * @return HTTP-svaret med statuskod och body, eller ett felsvar om anropet misslyckas
     */
    public ResponseEntity<String> createOrder(String requestBody, String authorization, String contentType) {
    /**
     * Bygger och skickar ett POST-anrop med innehållet och eventuell token.
     *
     * @param baseUrl tjänstens grundadress
     * @param path sökvägen som läggs till efter grundadressen
     * @param requestBody uppgifterna som skickas vidare som JSON
     * @param authorization hela Bearer-värdet, eller null om anropet inte behöver token
     * @param contentType innehållets format, eller null om ingen Content-Type ska sättas
     * @return HTTP-svaret med statuskod och body, eller ett felsvar om anropet misslyckas
     */
        return forwardPost(orderServiceUrl, "/orders", requestBody, authorization, contentType);
    }

    /**
     * Skickar beställningen till order-service för att skapa en order och starta Stripe Checkout.
     *
     * @param requestBody uppgifterna som skickas vidare som JSON
     * @param authorization hela Authorization-värdet, inklusive Bearer och token
     * @param contentType formatet på innehållet, exempelvis application/json
     * @return HTTP-svaret med statuskod och body, eller ett felsvar om anropet misslyckas
     */
    public ResponseEntity<String> createCheckout(
            String requestBody, String authorization, String contentType) {
        return forwardPost(
                orderServiceUrl, "/orders/checkout",
                requestBody, authorization, contentType);
    }

    /**
     * Hämtar en beställning via ID från order-service.
     *
     * @param id beställningens ID
     * @param authorization hela Authorization-värdet, inklusive Bearer och token
     * @return HTTP-svaret med statuskod och body, eller ett felsvar om anropet misslyckas
     */
    public ResponseEntity<String> getOrder(Long id, String authorization) {
        return forwardGet(orderServiceUrl, "/orders/" + id, authorization);
    }

    /**
     * Vidarebefordrar ett anrop för att radera en produkt i product-service.
     *
     * @param authorization hela Authorization-värdet, inklusive Bearer och token
     * @param id ID för produkten som ska raderas
     * @return HTTP-svaret med statuskod och body, eller ett felsvar om anropet misslyckas
     */
    public ResponseEntity<String> removeProduct(String authorization, Long id) {
        return forwardDelete(productServiceUrl, "/products/" + id, authorization);
    }

    /**
     * Skickar produktuppgifterna till product-service för att skapa en produkt.
     *
     * @param requestBody uppgifterna som skickas vidare som JSON
     * @param authorization hela Authorization-värdet, inklusive Bearer och token
     * @param contentType formatet på innehållet, exempelvis application/json
     * @return HTTP-svaret med statuskod och body, eller ett felsvar om anropet misslyckas
     */
    public ResponseEntity<String> createProduct(String requestBody, String authorization, String contentType) {
        return forwardPost(productServiceUrl, "/products", requestBody, authorization, contentType);
    }

    /**
     * Skickar registreringsuppgifterna till auth-service utan Bearer-token.
     *
     * @param requestBody uppgifterna som skickas vidare som JSON
     * @param contentType formatet på innehållet, exempelvis application/json
     * @return HTTP-svaret med statuskod och body, eller ett felsvar om anropet misslyckas
     */
    public ResponseEntity<String> register(String requestBody, String contentType) {
        return forwardPost(
                authServerUrl,
                "/appusers/register",
                requestBody,
                null,
                contentType
        );
    }

    public ResponseEntity<String> forwardGet(String baseUrl, String path, String authorization) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(baseUrl + path)).GET();
        ResponseEntity<String> sendResponse = send(builder, authorization);
        return sendResponse;
    }

    public ResponseEntity<String> forwardPost(String baseUrl,
                                              String path,
                                              String requestBody,
                                              String authorization,
                                              String contentType) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody));

        if (contentType != null && !contentType.isBlank()) {
            builder.header(HttpHeaders.CONTENT_TYPE, contentType);
        }
        return send(builder, authorization);

    }
    /**
     * Skapar en Bearer-header av token och anropar auth-services /auth/me.
     * Auth-service ansvarar för att verifiera token.
     *
     * @param accessToken JWT-token från cookien, utan Bearer-prefix
     * @return HTTP-svaret med statuskod och body, eller ett felsvar om anropet misslyckas
     */
    public ResponseEntity<String> session(String accessToken){
        return forwardGet(authServerUrl,
                "/auth/me",
                "Bearer " + accessToken);
    }

    /**
     * Bygger och skickar ett DELETE-anrop till den angivna tjänsten.
     *
     * @param baseUrl tjänstens grundadress
     * @param path sökvägen till resursen
     * @param authorization hela Authorization-värdet, inklusive Bearer och token
     * @return HTTP-svaret med statuskod och body, eller ett felsvar om anropet misslyckas
     */
    private ResponseEntity<String> forwardDelete(String baseUrl, String path, String authorization) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(baseUrl + path)).DELETE();

        return send(builder, authorization);
    }

    /**
     * Bygger och skickar ett PUT-anrop med innehållet och eventuell token.
     *
     * @param baseUrl tjänstens grundadress
     * @param path sökvägen till resursen
     * @param requestBody uppgifterna som skickas vidare som JSON
     * @param authorization hela Authorization-värdet, inklusive Bearer och token
     * @param contentType formatet på innehållet, exempelvis application/json
     * @return HTTP-svaret med statuskod och body, eller ett felsvar om anropet misslyckas
     */
    private ResponseEntity<String> forwardPut(
            String baseUrl,
            String path,
            String requestBody,
            String authorization,
            String contentType) {
        HttpRequest.Builder builder =
                HttpRequest.newBuilder(URI.create(baseUrl + path))
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody));
        if (contentType != null && !contentType.isBlank()) {
            builder.header(HttpHeaders.CONTENT_TYPE, contentType);
        }
        return send(builder, authorization);
    }


    /**
     * Skickar HTTP-anropet och vidarebefordrar statuskod, body och eventuell Content-Type.
     * Återställer trådens avbrottsflagga om anropet avbryts.
     *
     * @param builder det förberedda HTTP-anropet
     * @param authorization hela Bearer-värdet, eller null om token inte ska skickas
     * @return tjänstens svar, status 502 vid kommunikationsfel eller status 503 om anropet avbryts
     */
    private ResponseEntity<String> send(HttpRequest.Builder builder, String authorization) {
        if (authorization != null && !authorization.isBlank()) {
            builder.header(HttpHeaders.AUTHORIZATION, authorization);
        }
        try {
            HttpResponse<String> response = httpClient.send(
                    builder.build(),
                    HttpResponse.BodyHandlers.ofString()
            );
            ResponseEntity.BodyBuilder result = ResponseEntity.status(response.statusCode());

            response.headers().firstValue(HttpHeaders.CONTENT_TYPE)
                    .ifPresent(value -> result.header(
                            HttpHeaders.CONTENT_TYPE, value
                    ));
            return result.body(response.body());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Kunde inte nå bakomliggande tjänst");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Anropet avbröts");
        }
    }

    /**
     * Skickar inloggningsuppgifterna till auth-service.
     * Vid lyckad inloggning läggs JWT i en HttpOnly-cookie och användaruppgifterna returneras utan token.
     *
     * @param requestBody uppgifterna som skickas vidare som JSON
     * @param contentType innehållets format; application/json används om värdet saknas
     * @return status 200 med användaruppgifter och Set-Cookie vid lyckad inloggning, annars ett felsvar
     */
    public ResponseEntity<?> loginWithCookie(
            String requestBody,
            String contentType
    ) {
        HttpRequest.Builder request = HttpRequest.newBuilder(
                        URI.create(authServerUrl + "/auth/login")
                )
                .header(
                        HttpHeaders.CONTENT_TYPE,
                        contentType != null && !contentType.isBlank()
                                ? contentType
                                : "application/json"
                )
                .POST(HttpRequest.BodyPublishers.ofString(requestBody));

        return sendLogin(request);
    }

    /**
     * Skickar login-anropet och bygger ett cookiesvar om autentiseringen lyckas.
     *
     * @param request det förberedda login-anropet
     * @return ett login-svar, auth-services felsvar eller status 502 vid kommunikationsfel eller avbrott
     */
    private ResponseEntity<?> sendLogin(HttpRequest.Builder request) {
        try {
            HttpResponse<String> response = httpClient.send(
                    request.build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() < 200
                    || response.statusCode() >= 300) {
                return ResponseEntity
                        .status(response.statusCode())
                        .body(response.body());
            }

            return createLoginResponse(response.body());

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body("Gateway could not reach auth service");
        }
    }

    /**
     * Läser auth-services svar, placerar token i en HttpOnly-cookie och bygger användarsvaret.
     * Cookiens Secure och SameSite hämtas från konfigurationen.
     *
     * @param responseBody JSON-svaret från auth-service efter lyckad inloggning
     * @return status 200 med användaruppgifter och cookie, eller status 502 om JSON-svaret inte kan läsas
     */
    private ResponseEntity<?> createLoginResponse(String responseBody) {
        try {
            AuthServiceResponse authResponse =
                    objectMapper.readValue(
                            responseBody,
                            AuthServiceResponse.class
                    );

            ResponseCookie cookie = ResponseCookie
                    .from("access_token", authResponse.accessToken())
                    .httpOnly(true)
                    .secure(cookieSecure)
                    .sameSite(cookieSameSite)
                    .path("/")
                    .maxAge(Duration.ofSeconds(authResponse.expiresIn()))
                    .build();

            LoginResponse loginResponse = new LoginResponse(
                    authResponse.expiresIn(),
                    authResponse.subject(),
                    authResponse.roles()
            );

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(loginResponse);

        } catch (JacksonException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body("Auth service returned an invalid response");
        }
    }

    /**
     * Ber webbläsaren att radera access_token-cookien genom att sätta dess livslängd till noll.
     * Detta återkallar inte en redan utfärdad JWT.
     *
     * @return status 204 utan body och en Set-Cookie-header som tar bort cookien
     */
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie
                .from("access_token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(Duration.ZERO)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }


}
