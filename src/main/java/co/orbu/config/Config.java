package co.orbu.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class Config {

    @Value("${ofs.dropboxAccessKey}")
    private String dropboxAccessKey;

    @Bean
    public ExecutorService getExecutorService() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public DbxClientV2 getDropboxClient() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("ius/1.0").withAutoRetryEnabled().build();
        return new DbxClientV2(config, dropboxAccessKey);
    }

}
