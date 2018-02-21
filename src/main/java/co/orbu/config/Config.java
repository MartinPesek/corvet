package co.orbu.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class Config {

    @Value("${ofs.dropboxAccessKey}")
    private String dropboxAccessKey;

    @Value("${ofs.storageConnectionString}")
    private String storageConnectionString;

    @Bean
    public ExecutorService getExecutorService() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public DbxClientV2 getDropboxClient() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("ius/1.0").withAutoRetryEnabled().build();
        return new DbxClientV2(config, dropboxAccessKey);
    }

    @Bean
    public CloudBlobContainer getStorageService() throws URISyntaxException, InvalidKeyException, StorageException {
        CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
        CloudBlobClient serviceClient = account.createCloudBlobClient();

        CloudBlobContainer container = serviceClient.getContainerReference("ofs");
        container.createIfNotExists(BlobContainerPublicAccessType.BLOB, null, null);

        return container;
    }

}
