package uk.nhs.digital.nhsconnect.lab.results.mesh.scheduler;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchedulerTimestampRepository
    extends CrudRepository<SchedulerTimestamp, String>, SchedulerTimestampRepositoryExtensions {
}
