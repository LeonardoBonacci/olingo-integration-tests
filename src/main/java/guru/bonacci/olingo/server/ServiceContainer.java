package guru.bonacci.olingo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Import;

import com.sdl.odata.service.ODataServiceConfiguration;

import guru.bonacci.olingo.datasource.InMemoryDataSourceConfiguration;

@EnableAutoConfiguration(exclude = {HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class })
@Import({
        InMemoryDataSourceConfiguration.class,
        ODataServiceConfiguration.class
})
@SpringBootApplication
public class ServiceContainer {
	private static final Logger LOG = LoggerFactory.getLogger(ServiceContainer.class);

	public static void main(String[] args) {
		LOG.info("Starting Example Service Application container");
	
		SpringApplication.run(ServiceContainer.class, args);
		
		LOG.info("Example Service application container started");
	}
}
