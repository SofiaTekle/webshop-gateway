package se.iths.sofia.webshopgateway.controller;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
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
    public ResponseEntity<String> products (HttpServletRequest request){
        ResponseEntity<String> response = proxyService.products(request.getHeader(HttpHeaders.AUTHORIZATION));

        return response;
    }
    @GetMapping("/products/{id}")
    public ResponseEntity<String> productById(@PathVariable Long id, HttpServletRequest request){
        ResponseEntity<String> response = proxyService.productById(id, request.getHeader(HttpHeaders.AUTHORIZATION));
        return response;
    }
    @PostMapping("/products")
    public ResponseEntity<String> addProduct(@RequestBody String requestBody, HttpServletRequest request){
        ResponseEntity<String> response = proxyService.createProduct(requestBody,
                request.getHeader(HttpHeaders.AUTHORIZATION),
                request.getHeader(HttpHeaders.CONTENT_TYPE));
        return response;
    }
    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> removeProduct(@PathVariable Long id, HttpServletRequest request){
        ResponseEntity<String> response = proxyService.removeProduct(request.getHeader(HttpHeaders.AUTHORIZATION), id);

        return response;
    }
}
