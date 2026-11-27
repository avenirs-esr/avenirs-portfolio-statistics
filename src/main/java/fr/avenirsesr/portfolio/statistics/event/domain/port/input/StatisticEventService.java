package fr.avenirsesr.portfolio.statistics.event.domain.port.input;

import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEvent;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEventCountCriteria;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEventData;

public interface StatisticEventService {

  /**
   * Records a usage event. When the emitter provides an event id already recorded, the stored event
   * is returned and nothing is written.
   */
  StatisticEvent record(StatisticEventData data);

  long count(StatisticEventCountCriteria criteria);
}
