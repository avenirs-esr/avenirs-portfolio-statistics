package fr.avenirsesr.portfolio.statistics.event.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEvent;
import fr.avenirsesr.portfolio.statistics.event.domain.model.StatisticEventCountCriteria;
import fr.avenirsesr.portfolio.statistics.event.domain.port.output.repository.StatisticEventRepository;
import fr.avenirsesr.portfolio.statistics.event.infrastructure.adapter.mapper.StatisticEventMapper;
import fr.avenirsesr.portfolio.statistics.event.infrastructure.adapter.model.StatisticEventEntity;
import fr.avenirsesr.portfolio.statistics.event.infrastructure.adapter.specification.StatisticEventSpecification;
import org.springframework.stereotype.Repository;

@Repository
public class StatisticEventDatabaseRepository
    extends GenericJpaRepositoryAdapter<StatisticEvent, StatisticEventEntity>
    implements StatisticEventRepository {

  public StatisticEventDatabaseRepository(StatisticEventJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, StatisticEventEntity.class, StatisticEventMapper.INSTANCE);
  }

  @Override
  public long count(StatisticEventCountCriteria criteria) {
    return jpaSpecificationExecutor.count(StatisticEventSpecification.matching(criteria));
  }
}
