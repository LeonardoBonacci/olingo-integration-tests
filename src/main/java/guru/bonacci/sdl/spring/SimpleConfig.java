package guru.bonacci.sdl.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.sdl.odata.service.ODataServiceConfiguration;

@Import(ODataServiceConfiguration.class)
@Configuration
@ComponentScan //works without
public class SimpleConfig {
}