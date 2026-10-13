package se.iths.sofia.webshopgateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.iths.sofia.webshopgateway.service.AuthGatewayService;

@RestController
@RequestMapping("/gateway/auth")

public class AuthGatewayController {
    private final AuthGatewayService authGatewayService;

    public AuthGatewayController(AuthGatewayService authGatewayService) {
        this.authGatewayService = authGatewayService;
    }


//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody String requestBody, HttpServletRequest request) {
//        return authGatewayService.login(requestBody, request.getHeader(HttpHeaders.CONTENT_TYPE));
//    }

}
