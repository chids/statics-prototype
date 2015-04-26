package plan3.statics.model;

import plan3.pure.jersey.exceptions.PreconditionFailedException;

import java.util.Observable;
import java.util.Observer;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.ConflictException;

public class Coordinator extends Observable {
    private static final Logger LOG = LoggerFactory.getLogger(Coordinator.class);

    final Cache cache;
    final Storage storage;
    private final Lock lock;

    public Coordinator(final Cache cache, final Storage storage, final Lock lock, final Observer... observers) {
        this.cache = cache;
        this.storage = storage;
        this.lock = lock;
        Stream.of(observers).forEach(this::addObserver);
    }

    public Static update(final Static previous, final Content candidate) throws Exception {
        return this.lock.execute(candidate, () -> conditionalWrite(previous, candidate));
    }

    public Static add(final Content candidate) throws Exception {
        return this.lock.execute(candidate, () -> unconditionalAdd(candidate));
    }

    private Static conditionalWrite(final Static previous, final Content candidate) {
        if(candidate.isKnown(this.cache)) {
            final Static cached = this.cache.get(candidate);
            if(candidate.exists(this.cache)) {
                LOG.warn("{} NOT written, content already in cache", candidate.path());
                return candidate.path();
            }
            if(cached.equals(previous)) {
                LOG.warn("{} WRITTEN, previous version matched", previous);
                return write(candidate);
            }
            // We know about another revision
            throw new PreconditionFailedException("Current version is " + cached + ", not " + previous);
        }
        // The cache knows nothing about the previous version
        // => Reject until cache has refreshed
        throw new ConflictException("Cache out of sync: " + candidate);
    }

    private Static unconditionalAdd(final Content candidate) {
        if(candidate.isKnown(this.cache)) {
            final boolean persisted = candidate.exists(this.storage);
            final boolean cached = candidate.exists(this.cache);
            if(persisted && cached) {
                LOG.warn("{} NOT written, content exists in storage and cache", candidate.path());
                return candidate.path();
            }
            else
                if(cached) {
                    // Case: Removed directly from S3
                    candidate.removeFrom(this.cache);
                    LOG.warn("{} EVICTED, content exists in cache but NOT in storage", candidate.path());
                }
                else
                    if(persisted) {
                        // => Update cache from storage and reject
                        candidate.writeTo(this.cache);
                        LOG.warn("{} REFRESHED, exists in storage but cache has OTHER revision", candidate.path());
                    }
                    else {
                        LOG.warn("{} REJECTED, exists in cache but with OTHER revision", candidate.path());
                    }
            throw new ConflictException("Cache out of sync: " + candidate);
        }
        else
            if(candidate.exists(this.storage)) {
                // Already exists in storage but not in cache
                // => Update cache from storage and reject
                // (case: Evicted from cache)
                candidate.writeTo(this.cache);
                LOG.warn("{} REFRESHED, exists in storage, NOT in cache", candidate.path());
                throw new ConflictException("Cache out of sync: " + candidate);
            }
        // Not in cache, not in storage: NEW WRITE
        LOG.warn("{} WRITTEN, not in cache not storage", candidate.path());
        return write(candidate);
    }

    private Static write(final Content content) {
        content.writeTo(this.storage);
        content.writeTo(this.cache);
        super.setChanged();
        super.notifyObservers(content);
        return content.path();
    }
}