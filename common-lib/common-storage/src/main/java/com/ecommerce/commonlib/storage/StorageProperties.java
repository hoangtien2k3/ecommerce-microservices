package com.ecommerce.commonlib.storage;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for the shared S3-compatible {@link ObjectStorageService}.
 *
 * <p>Defaults target a local <a href="https://rustfs.com">RustFS</a> node exposing the
 * S3 API on {@code :9000}. Any S3-compatible backend (RustFS, AWS S3, ...) works by
 * pointing {@code endpoint} / credentials at it.
 *
 * <pre>
 * ecommerce:
 *   storage:
 *     endpoint: http://localhost:9000
 *     region: us-east-1
 *     access-key: rustfsadmin
 *     secret-key: rustfsadmin
 *     bucket: ecommerce-media
 *     path-style-access: true
 *     auto-create-bucket: true
 *     presign-duration: 1h
 * </pre>
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ecommerce.storage")
public class StorageProperties {

    /** Toggle the whole storage auto-configuration on/off. */
    private boolean enabled = true;

    /** S3 API endpoint of the backend (RustFS). */
    private String endpoint = "http://localhost:9000";

    /** Region sent in the signature; arbitrary but required by the SDK. */
    private String region = "us-east-1";

    /** Access key / username. */
    private String accessKey = "rustfsadmin";

    /** Secret key / password. */
    private String secretKey = "rustfsadmin";

    /** Default bucket used by the single-argument helper methods. */
    private String bucket = "ecommerce-media";

    /**
     * Use path-style addressing ({@code endpoint/bucket/key}) instead of virtual-hosted
     * ({@code bucket.endpoint/key}). Required for most self-hosted backends like RustFS.
     */
    private boolean pathStyleAccess = true;

    /** Create the default bucket on startup if it is missing. */
    private boolean autoCreateBucket = true;

    /** Default time-to-live for generated pre-signed URLs. */
    private Duration presignDuration = Duration.ofHours(1);
}
