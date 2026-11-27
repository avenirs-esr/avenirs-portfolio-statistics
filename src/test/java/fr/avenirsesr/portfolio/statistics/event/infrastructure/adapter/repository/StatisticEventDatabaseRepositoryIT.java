package fr.avenirsesr.portfolio.statistics.event.infrastructure.adapter.repository;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.data.domain.model.DateFilter;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.statistic.domain.model.enums.EStatisticEventType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEventCountCriteria;
import fr.avenirsesr.portfolio.statistics.event.infrastructure.fixture.StatisticEventFixture;
import fr.avenirsesr.portfolio.statistics.shared.infrastructure.ContainerConfigurationTest;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StatisticEventDatabaseRepositoryIT extends ContainerConfigurationTest {
  private static final UUID INSTITUTION_ID = UUID.randomUUID();

  @Autowired private StatisticEventDatabaseRepository repository;
  @Autowired private StatisticEventJpaRepository jpaRepository;
  @Autowired private EntityManager entityManager;

  @BeforeEach
  void setup() {
    jpaRepository.deleteAll();
  }

  private void persist(EStatisticEventType type, UUID institutionId, String occurredAt) {
    repository.save(
        StatisticEventFixture.create()
            .withEventType(type)
            .withInstitutionId(institutionId)
            .withOccurredAt(Instant.parse(occurredAt))
            .toModel());
  }

  @Nested
  class WhenSavingAnEvent {

    @Test
    void thenItShouldBeReadBackWithItsMetadataAndObject() {
      BddLogger.given("A statistic event with metadata and an object");
      var event =
          StatisticEventFixture.create()
              .withEventId(UUID.randomUUID())
              .withEventType(EStatisticEventType.FEEDBACK_REQUESTED)
              .withUserCategory(EUserCategory.STAFF)
              .withMetadata(Map.of("source", "activity"))
              .toModel();

      BddLogger.when("It is saved and read back from the database");
      repository.save(event);
      entityManager.flush();
      entityManager.clear();
      var stored = repository.findById(event.getId()).orElseThrow();

      BddLogger.then("Every field is read back");
      assertThat(stored.getUserId()).isEqualTo(event.getUserId());
      assertThat(stored.getEventType()).isEqualTo(EStatisticEventType.FEEDBACK_REQUESTED);
      assertThat(stored.getOccurredAt()).isEqualTo(event.getOccurredAt());
      assertThat(stored.getUserCategory()).isEqualTo(EUserCategory.STAFF);
      assertThat(stored.getInstitutionId()).isEqualTo(event.getInstitutionId());
      assertThat(stored.getProgramId()).isEqualTo(event.getProgramId());
      assertThat(stored.getObjectType()).isEqualTo(event.getObjectType());
      assertThat(stored.getObjectId()).isEqualTo(event.getObjectId());
      assertThat(stored.getMetadata()).isEqualTo(Map.of("source", "activity"));
    }
  }

  @Nested
  class WhenCountingEvents {

    @Nested
    class AndCriteriaAreGiven {

      @Test
      void thenItShouldFilterByTypeInstitutionAndInclusivePeriod() {
        BddLogger.given("Events of several types, institutions and dates");
        persist(EStatisticEventType.USER_LOGGED_IN, INSTITUTION_ID, "2026-11-01T00:00:00Z");
        persist(EStatisticEventType.USER_LOGGED_IN, INSTITUTION_ID, "2026-11-15T23:59:59Z");
        persist(EStatisticEventType.USER_LOGGED_IN, INSTITUTION_ID, "2026-11-16T00:00:00Z");
        persist(EStatisticEventType.USER_LOGGED_IN, UUID.randomUUID(), "2026-11-10T00:00:00Z");
        persist(EStatisticEventType.TRACE_CREATED, INSTITUTION_ID, "2026-11-10T00:00:00Z");

        BddLogger.when("The logins of the institution between the 1st and the 15th are counted");
        long count =
            repository.count(
                new StatisticEventCountCriteria(
                    EStatisticEventType.USER_LOGGED_IN,
                    INSTITUTION_ID,
                    new DateFilter(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 15))));

        BddLogger.then("Only the matching events of the inclusive period are counted");
        assertThat(count).isEqualTo(2);
      }
    }

    @Nested
    class AndNoCriteriaIsGiven {

      @Test
      void thenItShouldCountEveryEvent() {
        BddLogger.given("Events of several types");
        persist(EStatisticEventType.USER_LOGGED_IN, INSTITUTION_ID, "2026-11-01T00:00:00Z");
        persist(EStatisticEventType.TRACE_CREATED, null, "2026-11-02T00:00:00Z");

        BddLogger.when("Events are counted without criteria");
        long count = repository.count(new StatisticEventCountCriteria(null, null, null));

        BddLogger.then("Every event is counted");
        assertThat(count).isEqualTo(2);
      }
    }
  }
}
