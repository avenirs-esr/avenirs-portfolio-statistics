package fr.avenirsesr.portfolio.statistics.event.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.DateFilter;
import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.exception.FieldValidationException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEvent;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEventCountCriteria;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEventData;
import fr.avenirsesr.portfolio.statistics.event.domain.port.input.StatisticEventService;
import fr.avenirsesr.portfolio.statistics.event.domain.port.output.repository.StatisticEventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class StatisticEventServiceImpl implements StatisticEventService {
  public static final int MAX_METADATA_ENTRIES = 20;

  private final StatisticEventRepository statisticEventRepository;

  @Override
  public StatisticEvent record(StatisticEventData data) {
    validate(data);

    if (data.eventId() != null) {
      var alreadyRecorded = statisticEventRepository.findById(data.eventId());
      if (alreadyRecorded.isPresent()) {
        log.debug("Statistic event [{}] already recorded, ignored", data.eventId());
        return alreadyRecorded.get();
      }
    }

    var saved = statisticEventRepository.save(StatisticEvent.create(data));
    log.debug("Statistic event [{}] of type [{}] recorded", saved.getId(), saved.getEventType());
    return saved;
  }

  @Override
  public long count(StatisticEventCountCriteria criteria) {
    DateFilter dateFilter = criteria.dateFilter();
    if (dateFilter != null
        && dateFilter.fromDate() != null
        && dateFilter.toDate() != null
        && dateFilter.toDate().isBefore(dateFilter.fromDate())) {
      throw new BusinessException(EErrorCode.END_DATE_BEFORE_START_DATE);
    }

    return statisticEventRepository.count(criteria);
  }

  private void validate(StatisticEventData data) {
    if (data.eventType() == null) {
      throw new FieldValidationException(EErrorCode.NOT_NULL, "eventType");
    }
    if (data.userId() == null) {
      throw new FieldValidationException(EErrorCode.NOT_NULL, "userId");
    }
    if (data.userCategory() == null) {
      throw new FieldValidationException(EErrorCode.NOT_NULL, "userCategory");
    }
    if (data.occurredAt() == null) {
      throw new FieldValidationException(EErrorCode.NOT_NULL, "occurredAt");
    }
    if (data.metadata() != null && data.metadata().size() > MAX_METADATA_ENTRIES) {
      throw new FieldValidationException(EErrorCode.TOO_LONG, "metadata");
    }
  }
}
