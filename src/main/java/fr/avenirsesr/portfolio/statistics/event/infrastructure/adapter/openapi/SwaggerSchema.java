package fr.avenirsesr.portfolio.statistics.event.infrastructure.adapter.openapi;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.statistic.domain.model.enums.EStatisticEventType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;

public final class SwaggerSchema {
  private SwaggerSchema() {}

  public static final Schema<String> statisticEventTypeSchema =
      new StringSchema()
          .name("EStatisticEventType")
          ._enum(Arrays.stream(EStatisticEventType.values()).map(Enum::name).toList())
          .description("Enum for statistic event type");
  public static final Schema<String> userCategorySchema =
      new StringSchema()
          .name("EUserCategory")
          ._enum(Arrays.stream(EUserCategory.values()).map(Enum::name).toList())
          .description("Enum for user category");
}
