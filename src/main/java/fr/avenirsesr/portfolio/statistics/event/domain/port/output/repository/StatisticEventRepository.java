package fr.avenirsesr.portfolio.statistics.event.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEvent;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEventCountCriteria;

public interface StatisticEventRepository extends GenericRepositoryPort<StatisticEvent> {
  long count(StatisticEventCountCriteria criteria);
}
