package com.ecommerce.commonlib.storage;

import java.io.InputStream;
import lombok.Builder;
import lombok.Getter;

/**
 * Result of a download: the object payload plus its metadata.
 *
 * <p>{@link #getContent()} is a live stream backed by the network connection — callers
 * must close it (e.g. try-with-resources, or hand it to a Spring {@code InputStreamResource}
 * which closes it after the body is written).
 */
@Getter
@Builder
public class StorageObject {

    /** Open stream over the object bytes; the caller is responsible for closing it. */
    private final InputStream content;

    /** Object key within the bucket. */
    private final String key;

    /** MIME type as stored, may be {@code null}. */
    private final String contentType;

    /** Size in bytes, or {@code -1} if unknown. */
    private final long contentLength;
}
