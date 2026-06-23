package com.ecommerce.commonlib.storage;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

/**
 * {@link ObjectStorageService} backed by the AWS SDK for Java v2 {@link S3Client}.
 * Works against any S3-compatible backend; defaults are tuned for RustFS.
 */
public class S3ObjectStorageService implements ObjectStorageService {

    private static final Logger log = LoggerFactory.getLogger(S3ObjectStorageService.class);

    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final StorageProperties properties;

    public S3ObjectStorageService(S3Client s3Client, S3Presigner presigner, StorageProperties properties) {
        this.s3Client = s3Client;
        this.presigner = presigner;
        this.properties = properties;
    }

    @Override
    public String defaultBucket() {
        return properties.getBucket();
    }

    @Override
    public void ensureBucketExists(String bucket) {
        try {
            s3Client.headBucket(b -> b.bucket(bucket));
        } catch (NoSuchBucketException e) {
            createBucket(bucket);
        } catch (S3Exception e) {
            // RustFS / S3 return 404 (sometimes wrapped) when the bucket is missing.
            if (e.statusCode() == 404) {
                createBucket(bucket);
            } else {
                throw new StorageException("Failed to check bucket: " + bucket, e);
            }
        }
    }

    private void createBucket(String bucket) {
        try {
            s3Client.createBucket(b -> b.bucket(bucket));
            log.info("Created object-storage bucket '{}'", bucket);
        } catch (S3Exception e) {
            throw new StorageException("Failed to create bucket: " + bucket, e);
        }
    }

    @Override
    public String upload(String key, byte[] content, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(defaultBucket())
                .key(key)
                .contentType(contentType)
                .contentLength((long) content.length)
                .build();
        try {
            s3Client.putObject(request, RequestBody.fromBytes(content));
            return key;
        } catch (S3Exception e) {
            throw new StorageException("Failed to upload object: " + key, e);
        }
    }

    @Override
    public String upload(String bucket, String key, InputStream content, long contentLength, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();
        try {
            s3Client.putObject(request, RequestBody.fromInputStream(content, contentLength));
            return key;
        } catch (S3Exception e) {
            throw new StorageException("Failed to upload object: " + key, e);
        }
    }

    @Override
    public StorageObject download(String key) {
        return download(defaultBucket(), key);
    }

    @Override
    public StorageObject download(String bucket, String key) {
        GetObjectRequest request = GetObjectRequest.builder().bucket(bucket).key(key).build();
        try {
            ResponseInputStream<GetObjectResponse> stream = s3Client.getObject(request);
            GetObjectResponse response = stream.response();
            return StorageObject.builder()
                    .content(stream)
                    .key(key)
                    .contentType(response.contentType())
                    .contentLength(response.contentLength() == null ? -1 : response.contentLength())
                    .build();
        } catch (NoSuchKeyException e) {
            throw new StorageException("Object not found: " + bucket + "/" + key, e);
        } catch (S3Exception e) {
            throw new StorageException("Failed to download object: " + key, e);
        }
    }

    @Override
    public boolean exists(String key) {
        HeadObjectRequest request = HeadObjectRequest.builder().bucket(defaultBucket()).key(key).build();
        try {
            s3Client.headObject(request);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }
            throw new StorageException("Failed to stat object: " + key, e);
        }
    }

    @Override
    public void delete(String key) {
        delete(defaultBucket(), key);
    }

    @Override
    public void delete(String bucket, String key) {
        try {
            s3Client.deleteObject(b -> b.bucket(bucket).key(key));
        } catch (S3Exception e) {
            throw new StorageException("Failed to delete object: " + key, e);
        }
    }

    @Override
    public URL presignedGetUrl(String key, Duration ttl) {
        GetObjectRequest get = GetObjectRequest.builder().bucket(defaultBucket()).key(key).build();
        GetObjectPresignRequest presign = GetObjectPresignRequest.builder()
                .signatureDuration(ttlOrDefault(ttl))
                .getObjectRequest(get)
                .build();
        return presigner.presignGetObject(presign).url();
    }

    @Override
    public URL presignedPutUrl(String key, String contentType, Duration ttl) {
        PutObjectRequest put = PutObjectRequest.builder()
                .bucket(defaultBucket())
                .key(key)
                .contentType(contentType)
                .build();
        PutObjectPresignRequest presign = PutObjectPresignRequest.builder()
                .signatureDuration(ttlOrDefault(ttl))
                .putObjectRequest(put)
                .build();
        return presigner.presignPutObject(presign).url();
    }

    private Duration ttlOrDefault(Duration ttl) {
        return ttl != null ? ttl : properties.getPresignDuration();
    }
}
