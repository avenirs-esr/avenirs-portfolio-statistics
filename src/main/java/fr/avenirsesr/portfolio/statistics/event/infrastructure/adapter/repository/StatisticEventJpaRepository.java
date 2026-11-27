package fr.avenirsesr.portfolio.statistics.event.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.statistics.event.infrastructure.adapter.model.StatisticEventEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StatisticEventJpaRepository
    extends JpaRepository<StatisticEventEntity, UUID>,
        JpaSpecificationExecutor<StatisticEventEntity> {}
