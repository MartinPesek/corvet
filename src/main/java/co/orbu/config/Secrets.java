package co.orbu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Secrets loaded from Azure Key Vault.
 */
@Component
class Secrets {

    @Value("${DROPBOX-ACCESS-KEY}")
    private String dropboxAccessKey;

    @Value("${AZURE-STORAGE-CONNECTION-STRING}")
    private String azureStorageConnectionString;

    String getDropboxAccessKey() {
        return dropboxAccessKey;
    }

    String getAzureStorageConnectionString() {
        return azureStorageConnectionString;
    }
}
