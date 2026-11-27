package fr.avenirsesr.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackages = {"fr.avenirsesr.portfolio.statistics", "fr.avenirsesr.portfolio.common"})
public class AvenirsPortfolioStatisticsApplication {

  public static void main(String[] args) {
    SpringApplication.run(AvenirsPortfolioStatisticsApplication.class, args);
  }
}
