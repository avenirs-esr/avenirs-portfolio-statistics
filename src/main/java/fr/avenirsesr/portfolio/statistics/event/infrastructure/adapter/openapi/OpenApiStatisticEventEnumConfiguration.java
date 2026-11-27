package fr.avenirsesr.portfolio.statistics.event.infrastructure.adapter.openapi;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiStatisticEventEnumConfiguration {
  @Bean
  public OpenApiCustomizer statisticEventEnumCustomizer() {
    return openApi ->
        openApi
            .getComponents()
            .addSchemas("EStatisticEventType", SwaggerSchema.statisticEventTypeSchema)
            .addSchemas("EUserCategory", SwaggerSchema.userCategorySchema);
  }
}
