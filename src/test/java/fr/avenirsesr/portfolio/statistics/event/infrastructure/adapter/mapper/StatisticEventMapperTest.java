package fr.avenirsesr.portfolio.statistics.event.infrastructure.adapter.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.statistics.event.infrastructure.fixture.StatisticEventFixture;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StatisticEventMapperTest {

  @Nested
  class WhenMappingADomainEventToAnEntityAndBack {

    @Test
    void thenEveryFieldShouldBeKept() {
      BddLogger.given("A statistic event");
      var event = StatisticEventFixture.create().toModel();

      BddLogger.when("It is mapped to an entity and back");
      var entity = StatisticEventMapper.INSTANCE.fromDomain(event);
      var mapped = StatisticEventMapper.INSTANCE.toDomain(entity);

      BddLogger.then("Every field is kept");
      assertThat(mapped).usingRecursiveComparison().isEqualTo(event);
    }
  }
}
