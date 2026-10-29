# Webshop Gateway

Gateway är frontendens gemensamma ingång till webshopens backend.
Den vidarebefordrar anrop till Auth Service, Product Service och Order Service.

## Ansvar och samverkan

- Vidarebefordrar registrering och inloggning till Auth Service.
- Sparar JWT-token i en HttpOnly-cookie vid lyckad inloggning.
- Hämtar aktuell användare via Auth Service.
- Tar bort inloggningscookien vid utloggning.
- Vidarebefordrar produkt- och orderanrop.
- Skickar token vidare som Bearer-token till bakomliggande tjänster.

Frontend anropar Gateway via sökvägar som börjar med `/gateway`.
Bakomliggande tjänster verifierar token och kontrollerar behörigheter.

Mail Service ingår indirekt i köpflödet:
Order Service skickar orderbekräftelser via RabbitMQ till Mail Service.

## Tekniker

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Java HttpClient
- Jackson för JSON-hantering
- Springdoc OpenAPI 3.1.0 och Swagger UI
- Maven och Maven Wrapper

Gateway använder egna controllers och en proxyservice för att vidarebefordra HTTP-anrop.

## Förutsättningar

- JDK 21.
- Git för att klona projektet.
- Tillgängliga backend-tjänster för de funktioner som används.

Maven Wrapper ingår, så Maven behöver inte installeras separat.

Gateway kan startas separat, men anrop till backend kräver att motsvarande tjänst är tillgänglig.

## Hämta projektet

```bash
git clone https://github.com/SofiaTekle/webshop-gateway.git
cd webshop-gateway
```

## Miljövariabler

Grundkonfigurationen finns i
`src/main/resources/application.properties`.

Följande variabler har standardvärden:

- `PORT`: `9003`.
- `AUTH_SERVER_URL`: `http://localhost:8080`.
- `PRODUCT_SERVICE_URL`: `http://localhost:8081`.
- `ORDER_SERVICE_URL`: `http://localhost:9999`.
- `COOKIE_SECURE`: `false`.
- `COOKIE_SAME_SITE`: `Lax`.
- `FRONTEND_URL`: `http://localhost:5173`.

`FRONTEND_URL` läses av `CorsConfig`.

Tjänsteadresserna ska vara basadresser utan `/gateway` och utan avslutande snedstreck.
Gateway lägger själv till respektive endpoints sökväg.

Alla variabler ovan har standardvärden.
För lokal körning på standardportarna behöver de inte anges separat.

Exempel för macOS och Linux:

```bash
export PORT='9003'
export AUTH_SERVER_URL='http://localhost:8080'
export PRODUCT_SERVICE_URL='http://localhost:8081'
export ORDER_SERVICE_URL='http://localhost:9999'
export FRONTEND_URL='http://localhost:5173'
export COOKIE_SECURE='false'
export COOKIE_SAME_SITE='Lax'
```

Variabler kan även anges i IntelliJ under Run → Edit Configurations → Environment variables.

En `.env`-fil läses inte automatiskt av den visade konfigurationen.

## Databas

Gateway har ingen egen databas i den visade implementationen.
Data lagras och hanteras av bakomliggande tjänster.

Gateway behöver därför inga egna databasuppgifter, Stripe-nycklar eller RabbitMQ-inställningar.

## Starta lokalt

1. Starta Auth Service för registrering och inloggning.
2. Starta Product Service för produktfunktionerna.
3. Starta Order Service för beställningar och Checkout.
4. Kontrollera att tjänsteadresserna stämmer.
5. Starta Gateway från projektets rotmapp.

### macOS och Linux

```bash
./mvnw spring-boot:run
```

Om skriptet saknar körbehörighet:

```bash
bash ./mvnw spring-boot:run
```

### Windows PowerShell

```powershell
.\mvnw.cmd spring-boot:run
```

Gateways standardadress är:

```text
http://localhost:9003
```

Frontend ska använda denna Gateway och dess `/gateway`-endpoints.
Om Gateways port eller adress ändras behöver frontendens API-konfiguration också uppdateras.

För hela betalnings- och e-postflödet behöver även Order Services beroenden och Mail Service vara korrekt konfigurerade.

## API

### Autentisering

- `POST /gateway/register`: vidarebefordrar registrering till Auth Services `/appusers/register`.
- `POST /gateway/auth/login`: loggar in och sätter inloggningscookien.
- `GET /gateway/auth/me`: hämtar aktuell användare från Auth Service.
- `POST /gateway/auth/logout`: tar bort inloggningscookien och returnerar status 204.

Skicka JSON med `Content-Type: application/json` för registrering och inloggning.

### Produkter

- `GET /gateway/products`: hämtar alla produkter.
- `GET /gateway/products/{id}`: hämtar en produkt.
- `POST /gateway/products`: skapar en produkt.
- `DELETE /gateway/products/{id}`: tar bort en produkt.

### Beställningar

- `POST /gateway/orders`: skapar en beställning.
- `POST /gateway/orders/checkout`: skapar en beställning
  och en Stripe Checkout-session.
- `GET /gateway/orders/{id}`: hämtar en beställning.

Produkt- och ordercontrollers returnerar status 401 om cookien `access_token` saknas eller är tom.

Behörigheter för respektive operation kontrolleras av bakomliggande tjänst. Exempelvis kräver Product Service
administratörsbehörighet för att skapa och ta bort produkter.

Request- och responseformat följer bakomliggande tjänsters API.

De visade Gateway-kontrollerna innehåller ingen Stripe-webhook.
Webhook-leverans konfigureras till Order Service enligt dess README.

## Inloggning och cookies

Vid lyckad inloggning:Gateway skickar inloggningsuppgifterna till Auth Service. Auth Service returnerar token och
användaruppgifter. Gateway sätter cookien `access_token`. Frontend får uppgifter om giltighetstid, användaridentitet och
roller i svaret, utan själva tokenen.

Cookien har följande inställningar:

- `HttpOnly`: `true`.
- `Path`: `/`.
- `Secure`: styrs av `COOKIE_SECURE`.
- `SameSite`: styrs av `COOKIE_SAME_SITE`.
- Livslängd: hämtas från Auth Services `expiresIn`.

Frontend behöver inkludera cookies i sina API-anrop, exempelvis:

```javascript
fetch("http://localhost:9003/gateway/auth/me", {
    credentials: "include"
});
```

Gateway läser cookien och skickar token vidare till backend som:

```text
Authorization: Bearer <JWT_TOKEN>
```

Att enbart skicka en Authorization-header till produkt- eller orderendpoints ersätter inte cookien i de visade
kontrollerna.

Vid utloggning skickas en tom cookie med samma namn och sökväg, med livslängden noll. Detta tar bort webbläsarens
cookie.

## CORS

Gateway tillåter frontend-origin från `FRONTEND_URL`, med standardvärdet:

```text
http://localhost:5173
```

Dessutom tillåts:

- `http://localhost:5174`
- `http://localhost:5175`

Tillåtna metoder är GET, POST, PUT, DELETE och OPTIONS.
Credentials är aktiverade.

Om Frontend körs på en annan adress behöver `FRONTEND_URL` matcha dess origin, inklusive protokoll och port.
Ange origin utan sidans sökväg.

Använd konsekvent `localhost` vid lokal utveckling, så att frontendadress, Gateway-adress och cookieflöde stämmer
överens.

## Cookie-inställningar för olika miljöer

För lokal utveckling över HTTP är standardvärdena:

```text
COOKIE_SECURE=false
COOKIE_SAME_SITE=Lax
```

Vid HTTPS-drift ska cookie-inställningarna anpassas till frontendens och Gateways faktiska adresser.

För ett flöde som behöver cookies mellan olika webbplatser används vanligtvis `SameSite=None` tillsammans med
`Secure=true`.
Webbläsarens regler för tredjepartscookies kan också påverka flödet.

Kontrollera även `FRONTEND_URL` och att frontend skickar anrop med credentials.

## API-dokumentation

När Gateway körs på standardporten finns dokumentationen här:

- Swagger UI: http://localhost:9003/swagger-ui/index.html
- OpenAPI: http://localhost:9003/v3/api-docs

Produkt- och orderanrop via Gateway kräver inloggningscookien.
En Bearer-inställning i ett API-verktyg ersätter inte detta krav.

## Test och bygge

Kör tester:

```bash
./mvnw test
```

Bygg och verifiera projektet:

```bash
./mvnw clean verify
```

På Windows används `.\mvnw.cmd` i stället för `./mvnw`.

## Felsökning

- Gateway startar inte: kontrollera att JDK 21 används
  och att port `9003` är ledig.
- Status 502: kontrollera att bakomliggande tjänst är tillgänglig
  och att dess basadress är korrekt.
  Vid inloggning kan 502 även betyda att Auth Service
  returnerade ett svar som Gateway inte kunde läsa.
- Status 503 från proxyanrop: anropet avbröts.
- Status 401: kontrollera inloggningen, cookiens giltighet
  och att frontend skickar cookies med anropet.
- Status 403 från backend: kontrollera användarens behörigheter.
- CORS-fel: kontrollera frontendens origin och `FRONTEND_URL`.
- Cookien sparas eller skickas inte: kontrollera
  `COOKIE_SECURE`, `COOKIE_SAME_SITE`, protokoll,
  värdnamn och frontendens credentials-inställning.
- Betalningen förblir väntande: kontrollera Stripe-webhooken
  och betalningsflödet i Order Service.

## Systemets repositories

- [Auth Service](https://github.com/armin-akhtarzand/webshop-auth-server)
- [Product Service](https://github.com/armin-akhtarzand/webshop-product-service)
- [Order Service](https://github.com/armin-akhtarzand/webshop-order-service)
- [Mail Service](https://github.com/armin-akhtarzand/webshop-mail-service)
- [Gateway](https://github.com/SofiaTekle/webshop-gateway)
- [Frontend](https://github.com/SofiaTekle/webshop-frontend)
