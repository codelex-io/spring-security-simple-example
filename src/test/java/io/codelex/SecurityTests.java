package io.codelex;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpEntity.EMPTY;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class SecurityTests {
    @Autowired
    TestRestTemplate restTemplate;

    static final String email = "dev@codelex.io";
    static final String password = "Password123";

    @Test
    public void customer_account_should_be_secured_by_default() {
        var result = restTemplate.getForEntity("/api/account", String.class);
        assertEquals(FORBIDDEN, result.getStatusCode());
    }

    @Test
    public void customer_should_be_authorised_on_registration() {
        var result = register();
        assertEquals(OK, result.getStatusCode());

        var sessionId = sessionId(result);
        assertEquals(OK, accessAccount(sessionId).getStatusCode());
    }

    @Test
    public void customer_should_be_authorised_on_sign_in() {
        var result = register();
        assertEquals(OK, result.getStatusCode());

        result = signIn();
        assertEquals(OK, result.getStatusCode());

        var sessionId = sessionId(result);
        assertEquals(OK, accessAccount(sessionId).getStatusCode());
    }

    @Test
    public void customer_should_be_able_to_sign_out() {
        var result = register();
        assertEquals(OK, result.getStatusCode());

        var sessionId = sessionId(result);

        result = signOut(sessionId);
        assertEquals(OK, result.getStatusCode());

        assertEquals(FORBIDDEN, accessAccount(sessionId).getStatusCode());
    }

    @Test
    public void customer_should_be_able_to_get_account_details() {
        var result = register();
        assertEquals(OK, result.getStatusCode());

        var sessionId = sessionId(result);
        assertEquals(email, accessAccount(sessionId).getBody());
    }

    @Test
    public void customer_should_not_be_able_to_access_admin_endpoints() {
        var result = register();
        assertEquals(OK, result.getStatusCode());

        var sessionId = sessionId(result);
        assertEquals(FORBIDDEN, accessAdminAccount(sessionId).getStatusCode());
    }

    private ResponseEntity<Void> register() {
        var uri = fromPath("/api/register")
                .queryParam("email", email)
                .queryParam("password", password)
                .build()
                .toUri();

        return restTemplate.postForEntity(uri, EMPTY, Void.class);
    }

    private ResponseEntity<Void> signIn() {
        var uri = fromPath("/api/sign-in")
                .queryParam("email", email)
                .queryParam("password", password)
                .build()
                .toUri();

        return restTemplate.postForEntity(uri, EMPTY, Void.class);
    }

    private ResponseEntity<Void> signOut(String sessionId) {
        return restTemplate.exchange("/api/sign-out", POST, request(sessionId), Void.class);
    }

    private ResponseEntity<String> accessAccount(String sessionId) {
        return restTemplate.exchange("/api/account", GET, request(sessionId), String.class);
    }

    private ResponseEntity<String> accessAdminAccount(String sessionId) {
        return restTemplate.exchange("/admin-api/account", GET, request(sessionId), String.class);
    }

    private String sessionId(ResponseEntity<?> result) {
        return result.getHeaders().getFirst("Set-Cookie");
    }

    private HttpEntity<?> request(String sessionId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", sessionId);
        return new HttpEntity<>(headers);
    }
}
