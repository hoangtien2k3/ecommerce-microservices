package com.ecommerce.commonlib.storage.autoconfigure;

import com.ecommerce.commonlib.storage.ObjectStorageService;
import com.ecommerce.commonlib.storage.S3ObjectStorageService;
import com.ecommerce.commonlib.storage.StorageProperties;
import java.net.URI;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * Auto-wires an S3-compatible {@link ObjectStorageService} (RustFS by default).
 *
 * <p>Activated whenever the AWS SDK is on the classpath and
 * {@code ecommerce.storage.enabled} is not {@code false}. Services override any bean by
 * declaring their own {@link S3Client}, {@link S3Presigner} or {@link ObjectStorageService}.
 */
@AutoConfiguration
@ConditionalOnClass(S3Client.class)
@ConditionalOnProperty(prefix = "ecommerce.storage", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(StorageProperties.class)
public class ObjectStorageAutoConfiguration {

    private StaticCredentialsProvider credentials(StorageProperties props) {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey()));
    }

    @Bean
    @ConditionalOnMissingBean
    public S3Client s3Client(StorageProperties props) {
        return S3Client.builder()
                .endpointOverride(URI.create(props.getEndpoint()))
                .region(Region.of(props.getRegion()))
                .credentialsProvider(credentials(props))
                .forcePathStyle(props.isPathStyleAccess())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public S3Presigner s3Presigner(StorageProperties props) {
        return S3Presigner.builder()
                .endpointOverride(URI.create(props.getEndpoint()))
                .region(Region.of(props.getRegion()))
                .credentialsProvider(credentials(props))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(props.isPathStyleAccess())
                        .build())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectStorageService objectStorageService(
            S3Client s3Client, S3Presigner presigner, StorageProperties props) {
        S3ObjectStorageService service = new S3ObjectStorageService(s3Client, presigner, props);
        if (props.isAutoCreateBucket()) {
            service.ensureBucketExists(props.getBucket());
        }
        return service;
    }
}
