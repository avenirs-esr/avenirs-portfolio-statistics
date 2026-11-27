package fr.avenirsesr.portfolio.statistics.event.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.data.domain.model.DateFilter;
import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;
import fr.avenirsesr.portfolio.common.statistic.domain.model.enums.EStatisticEventType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEvent;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEventCountCriteria;
import fr.avenirsesr.portfolio.statistics.event.domain.port.output.repository.StatisticEventRepository;
import fr.avenirsesr.portfolio.statistics.event.infrastructure.fixture.StatisticEventFixture;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatisticEventServiceImplTest {

  @Mock private StatisticEventRepository statisticEventRepository;

  @InjectMocks private StatisticEventServiceImpl service;

  @Nested
  class WhenRecordingAnEvent {

    @Nested
    class AndTheEventHasNoEmitterId {

      @Test
      void thenItShouldSaveItWithAGeneratedIdAndAllItsAttachments() {
        BddLogger.given("An event without emitter id");
        var data = StatisticEventFixture.create().toData();
        when(statisticEventRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        BddLogger.when("record is called");
        var recorded = service.record(data);

        BddLogger.then("The event is saved with a generated id and all its attachments");
        var captor = ArgumentCaptor.forClass(StatisticEvent.class);
        verify(statisticEventRepository).save(captor.capture());
        var saved = captor.getValue();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(data.userId());
        assertThat(saved.getEventType()).isEqualTo(data.eventType());
        assertThat(saved.getOccurredAt()).isEqualTo(data.occurredAt());
        assertThat(saved.getUserCategory()).isEqualTo(data.userCategory());
        assertThat(saved.getInstitutionId()).isEqualTo(data.institutionId());
        assertThat(saved.getProgramId()).isEqualTo(data.programId());
        assertThat(saved.getObjectType()).isEqualTo(data.objectType());
        assertThat(saved.getObjectId()).isEqualTo(data.objectId());
        assertThat(saved.getMetadata()).isEqualTo(data.metadata());
        assertThat(recorded).isSameAs(saved);
      }
    }

    @Nested
    class AndTheEventHasAnEmitterIdNotRecordedYet {

      @Test
      void thenItShouldUseTheEmitterIdAsEventId() {
        BddLogger.given("An event with an emitter id not recorded yet");
        var eventId = UUID.randomUUID();
        when(statisticEventRepository.findById(eventId)).thenReturn(Optional.empty());
        when(statisticEventRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        BddLogger.when("record is called");
        var recorded = service.record(StatisticEventFixture.create().withEventId(eventId).toData());

        BddLogger.then("The event is stored under the emitter id");
        assertThat(recorded.getId()).isEqualTo(eventId);
      }
    }

    @Nested
    class AndTheEventIsAlreadyRecorded {

      @Test
      void thenItShouldReturnTheStoredEventAndWriteNothing() {
        BddLogger.given("An event whose emitter id is already recorded");
        var data = StatisticEventFixture.create().withEventId(UUID.randomUUID()).toData();
        var existing = StatisticEvent.create(data);
        when(statisticEventRepository.findById(data.eventId())).thenReturn(Optional.of(existing));

        BddLogger.when("record is called again");
        var recorded = service.record(data);

        BddLogger.then("The stored event is returned and nothing is written");
        assertThat(recorded).isSameAs(existing);
        verify(statisticEventRepository, never()).save(any());
      }
    }

    @Nested
    class AndAMandatoryFieldIsMissing {

      @Test
      void thenItShouldRejectAnEventWithoutType() {
        BddLogger.given("An event without type");
        var data = StatisticEventFixture.create().withEventType(null).toData();

        BddLogger.when("record is called");
        BddLogger.then("A NOT_NULL error is raised without any repository access");
        assertThatThrownBy(() -> service.record(data))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode")
            .isEqualTo(EErrorCode.NOT_NULL);
        verifyNoInteractions(statisticEventRepository);
      }

      @Test
      void thenItShouldRejectAnEventWithoutUser() {
        BddLogger.given("An event without user");
        var data = StatisticEventFixture.create().withUserId(null).toData();

        BddLogger.when("record is called");
        BddLogger.then("A NOT_NULL error is raised without any repository access");
        assertThatThrownBy(() -> service.record(data))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode")
            .isEqualTo(EErrorCode.NOT_NULL);
        verifyNoInteractions(statisticEventRepository);
      }
    }

    @Nested
    class AndTheMetadataHasTooManyEntries {

      @Test
      void thenItShouldRejectTheEvent() {
        BddLogger.given("An event with more metadata entries than allowed");
        Map<String, Object> metadata = new HashMap<>();
        for (int i = 0; i <= StatisticEventServiceImpl.MAX_METADATA_ENTRIES; i++) {
          metadata.put("key" + i, i);
        }
        var data = StatisticEventFixture.create().withMetadata(metadata).toData();

        BddLogger.when("record is called");
        BddLogger.then("A TOO_LONG error is raised without any repository access");
        assertThatThrownBy(() -> service.record(data))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode")
            .isEqualTo(EErrorCode.TOO_LONG);
        verifyNoInteractions(statisticEventRepository);
      }
    }
  }

  @Nested
  class WhenCountingEvents {

    @Nested
    class AndTheCriteriaAreValid {

      @Test
      void thenItShouldReturnTheRepositoryCount() {
        BddLogger.given("Count criteria on a type, an institution and a period");
        var criteria =
            new StatisticEventCountCriteria(
                EStatisticEventType.USER_LOGGED_IN,
                UUID.randomUUID(),
                new DateFilter(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 15)));
        when(statisticEventRepository.count(criteria)).thenReturn(42L);

        BddLogger.when("count is called");
        long count = service.count(criteria);

        BddLogger.then("The repository count is returned");
        assertThat(count).isEqualTo(42L);
      }
    }

    @Nested
    class AndThePeriodEndsBeforeItStarts {

      @Test
      void thenItShouldRejectTheCriteria() {
        BddLogger.given("A period whose end is before its start");
        var criteria =
            new StatisticEventCountCriteria(
                null, null, new DateFilter(LocalDate.of(2026, 11, 15), LocalDate.of(2026, 11, 1)));

        BddLogger.when("count is called");
        BddLogger.then("An END_DATE_BEFORE_START_DATE error is raised");
        assertThatThrownBy(() -> service.count(criteria))
            .isInstanceOf(BusinessException.class)
            .extracting("errorCode")
            .isEqualTo(EErrorCode.END_DATE_BEFORE_START_DATE);
        verifyNoInteractions(statisticEventRepository);
      }
    }
  }
}
