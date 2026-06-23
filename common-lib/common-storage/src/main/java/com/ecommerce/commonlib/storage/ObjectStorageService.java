package com.ecommerce.commonlib.storage;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;

/**
 * Backend-agnostic façade over an S3-compatible object store.
 *
 * <p>Single-argument overloads operate on the configured default bucket
 * ({@code ecommerce.storage.bucket}); the bucket-qualified overloads target any bucket.
 * Implementations translate SDK failures into {@link StorageException}.
 */
public interface ObjectStorageService {

    /** The default bucket configured for this service. */
    String defaultBucket();

    /** Create the bucket if it does not already exist (idempotent). */
    void ensureBucketExists(String bucket);

    /** Upload bytes to the default bucket and return the stored key. */
    String upload(String key, byte[] content, String contentType);

    /** Stream {@code content} ({@code contentLength} bytes) to {@code bucket} and return the key. */
    String upload(String bucket, String key, InputStream content, long contentLength, String contentType);

    /** Fetch an object from the default bucket. */
    StorageObject download(String key);

    /** Fetch an object from {@code bucket}. */
    StorageObject download(String bucket, String key);

    /** True if the key exists in the default bucket. */
    boolean exists(String key);

    /** Delete a key from the default bucket (no-op if absent). */
    void delete(String key);

    /** Delete a key from {@code bucket} (no-op if absent). */
    void delete(String bucket, String key);

    /** Pre-signed URL granting temporary GET access to a default-bucket object. */
    URL presignedGetUrl(String key, Duration ttl);

    /** Pre-signed URL granting temporary PUT access to upload a default-bucket object. */
    URL presignedPutUrl(String key, String contentType, Duration ttl);
}
