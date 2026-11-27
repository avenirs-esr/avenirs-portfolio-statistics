package fr.avenirsesr.portfolio.statistics.event.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.statistics.event.domain.port.input.StatisticEventService;
import fr.avenirsesr.portfolio.statistics.event.domain.port.output.repository.StatisticEventRepository;
import fr.avenirsesr.portfolio.statistics.event.domain.service.StatisticEventServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StatisticEventServiceConfig {
  private final StatisticEventRepository statisticEventRepository;

  @Bean
  public StatisticEventService statisticEventService() {
    return new TransactionalStatisticEventService(
        new StatisticEventServiceImpl(statisticEventRepository));
  }
}
