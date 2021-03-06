package plan3.statics.model.impl;

import static java.util.Objects.requireNonNull;

import plan3.statics.model.Content;
import plan3.statics.model.Location;
import plan3.statics.model.Storage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.io.ByteStreams;

public class S3Storage implements Storage {
    private static final ByteArrayInputStream empty = new ByteArrayInputStream(new byte[0]);
    private final AmazonS3 s3;
    private final String bucket;

    public S3Storage(final AmazonS3 s3, final String bucket) {
        this.s3 = requireNonNull(s3, "Client");
        this.bucket = requireNonNull(bucket, "Bucket");
    }

    @Override
    public void put(final Content content) {
        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(content.mime().toString());
        metadata.setHeader(Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead);
        this.s3.putObject(this.bucket, key(content), content.content(), metadata);
        metadata.setHeader(Headers.REDIRECT_LOCATION, "/".concat(key(content)));
        this.s3.putObject(this.bucket, content.where().current(), empty, metadata);
    }

    @Override
    public boolean exists(final Location location) {
        try {
            final String revision = this.s3.getObjectMetadata(this.bucket, key(location)).getETag();
            return location.toString('/').endsWith(revision);
        }
        catch(final AmazonS3Exception ase) {
            if(ase.getStatusCode() == 404) {
                return false;
            }
            throw new IllegalStateException(ase);
        }
    }

    @Override
    public Content get(final Location location) {
        final S3Object result = this.s3.getObject(this.bucket, key(location));
        try(InputStream content = result.getObjectContent()) {
            final MediaType mime = MediaType.valueOf(result.getObjectMetadata().getContentType());
            return new Content(location, mime, ByteStreams.toByteArray(content));
        }
        catch(final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void remove(final Location location) {
        this.s3.deleteObject(this.bucket, key(location));
    }
}
