package co.orbu.config;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * Secrets loaded from Azure Key Vault.
 */
@Component
class Secrets {

    private String dropboxAccessKey;
    private String azureStorageConnectionString;

    String getDropboxAccessKey() {
        return dropboxAccessKey;
    }

    String getAzureStorageConnectionString() {
        return azureStorageConnectionString;
    }

    public Secrets() {
        String authUrl = String.format("https://login.windows.net/%s/oauth2/token", System.getenv("KEYVAULT_TENANT_ID"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("grant_type", "client_credentials");
        request.add("resource", "https://vault.azure.net");
        request.add("client_id", System.getenv("KEYVAULT_CLIENT_ID"));
        request.add("client_secret", System.getenv("KEYVAULT_CLIENT_SECRET"));

        RestTemplate restTemplate = new RestTemplate();
        AuthResponse authResponse = restTemplate.postForObject(authUrl, new HttpEntity<>(request, headers), AuthResponse.class);
        if (authResponse == null) {
            throw new RuntimeException("No response from key vault server.");
        }

        if (!"bearer".equalsIgnoreCase(authResponse.getTokenType())) {
            throw new IllegalStateException("Bearer token type expected but was: " + authResponse.getTokenType());
        }

        String vault = System.getenv("KEYVAULT_NAME");
        dropboxAccessKey = getSecret(authResponse.getAccessToken(), vault, "DROPBOX-ACCESS-KEY");
        azureStorageConnectionString = getSecret(authResponse.getAccessToken(), vault, "AZURE-STORAGE-CONNECTION-STRING");
    }

    private String getSecret(String token, String vault, String name) {
        HttpHeaders secretHeader = new HttpHeaders();
        secretHeader.set("Authorization", "Bearer " + token);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<SecretBundle> secret = restTemplate.exchange("https://{0}.vault.azure.net/secrets/{1}/?api-version=7.0",
                HttpMethod.GET,
                new HttpEntity<>(secretHeader),
                SecretBundle.class,
                vault, name);

        return Objects.requireNonNull(secret.getBody()).getValue();
    }
}
