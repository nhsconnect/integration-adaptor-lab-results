package uk.nhs.digital.nhsconnect.lab.results.mesh.scheduler;

import com.mongodb.MongoWriteException;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import uk.nhs.digital.nhsconnect.lab.results.utils.TimestampService;

import java.time.Instant;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class SchedulerTimestampRepositoryExtensionsImpl implements SchedulerTimestampRepositoryExtensions {

    protected static final String MESH_TIMESTAMP_COLLECTION_NAME = "schedulerTimestamp";
    private static final String SCHEDULER_TYPE = "schedulerType";
    private static final String TIMESTAMP_FIELD_NAME = "updateTimestamp";

    private final MongoOperations mongoOperations;
    private final TimestampService timestampService;

    @Override
    public boolean updateTimestamp(String schedulerType, Instant timestamp, long seconds) {
        final var query = query(where(TIMESTAMP_FIELD_NAME)
            .lt(timestampService.getCurrentTimestamp().minusSeconds(seconds))
            .and(SCHEDULER_TYPE).is(schedulerType));

        final var update = Update.update(TIMESTAMP_FIELD_NAME, timestamp)
            .set(SCHEDULER_TYPE, schedulerType);

        if (documentAlreadyExists(schedulerType)) {
            final UpdateResult result = mongoOperations.updateFirst(query, update, MESH_TIMESTAMP_COLLECTION_NAME);

            return updateSuccessful(result);
        } else {
            LOGGER.info("{} collection does not exist or it is empty. Document with timestamp will be created",
                MESH_TIMESTAMP_COLLECTION_NAME);
            final SchedulerTimestamp schedulerTimestamp = new SchedulerTimestamp(
                schedulerType, timestampService.getCurrentTimestamp());
            try {
                mongoOperations.save(schedulerTimestamp, MESH_TIMESTAMP_COLLECTION_NAME);
            } catch (MongoWriteException | DuplicateKeyException e) {
                LOGGER.warn("Unable to create new document for scheduler type {}. "
                    + "Most likely another instance already created the document.", schedulerType, e);
            }
            return false;
        }

    }

    private boolean documentAlreadyExists(String schedulerType) {
        final var query = query(where(SCHEDULER_TYPE).is(schedulerType));
        final var count = mongoOperations.count(query, MESH_TIMESTAMP_COLLECTION_NAME);
        if (count > 1) {
            LOGGER.error("More than one document exists for schedulerType {}. "
                + "This can cause unexpected scheduling behaviour.", schedulerType);
        }
        return count >= 1;
    }

    private boolean updateSuccessful(UpdateResult result) {
        return result.getModifiedCount() == 1L;
    }
}
