package co.orbu.config;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class GongyuConfig {

    @Value("${gongyu.storageContainerName}")
    private String storageContainerName;

    @Value("${gongyu.maxFileDownloadSize}")
    private long maxFileDownloadSize;

    private final Secrets secrets;

    @Autowired
    public GongyuConfig(Secrets secrets) {
        this.secrets = Objects.requireNonNull(secrets);
    }

    /**
     * Gets maximum allowed file size when downloading a file from a server.
     *
     * @return File size in bytes.
     */
    // TODO: use this then remove @SuppressWarnings
    @SuppressWarnings("unused")
    public long getMaxFileDownloadSize() {
        return maxFileDownloadSize;
    }

    @Bean
    ExecutorService getExecutorService() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    CloudBlobContainer getStorageService() throws URISyntaxException, InvalidKeyException, StorageException {
        CloudStorageAccount account = CloudStorageAccount.parse(secrets.getAzureStorageConnectionString());
        CloudBlobClient serviceClient = account.createCloudBlobClient();

        CloudBlobContainer container = serviceClient.getContainerReference(storageContainerName);
        container.createIfNotExists(BlobContainerPublicAccessType.BLOB, null, null);

        return container;
    }

}
