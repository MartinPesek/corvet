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

import javax.annotation.PostConstruct;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class GongyuConfig {

    @Value("${gongyu.dropboxAccessKey}")
    private String dropboxAccessKey;

    @Value("${gongyu.storageConnectionString}")
    private String storageConnectionString;

    @Value("${gongyu.storageContainerName}")
    private String storageContainerName;

    @Value("${gongyu.maxFileDownloadSize}")
    private long maxFileDownloadSize;

    private String dropboxUploadDirectory;

    @PostConstruct
    void init() {
        dropboxUploadDirectory = "/" + storageContainerName + "/";
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

    /**
     * Gets Dropbox's upload directory in a "/dir/" format.
     *
     * @return Upload directory path.
     */
    public String getDropboxUploadDirectory() {
        return dropboxUploadDirectory;
    }

    @Bean
    ExecutorService getExecutorService() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    DbxClientV2 getDropboxClient() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("gongyu/1.0").withAutoRetryEnabled().build();
        return new DbxClientV2(config, dropboxAccessKey);
    }

    @Bean
    CloudBlobContainer getStorageService() throws URISyntaxException, InvalidKeyException, StorageException {
        CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
        CloudBlobClient serviceClient = account.createCloudBlobClient();

        CloudBlobContainer container = serviceClient.getContainerReference(storageContainerName);
        container.createIfNotExists(BlobContainerPublicAccessType.BLOB, null, null);

        return container;
    }

}
