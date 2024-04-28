package org.clearsolutions.task.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for Swagger documentation.
 */
@Configuration
public class SwaggerConfig {
  @Value("${springdoc.swagger-ui.title}")
  private String title;
  @Value("${springdoc.swagger-ui.description}")
  private String description;

  /**
  * Configures custom OpenAPI settings.
  *
  * @return The custom OpenAPI configuration.
  */
  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI()
        .servers(servers())
        .info(apiInfo());
  }

  private List<Server> servers() {
    Server server = new Server();
    server.setUrl("/");
    server.setDescription("localhost:8080");
    return List.of(server);
  }

  private Info apiInfo() {
    return new Info()
        .title(title)
        .description(description);
  }

}
