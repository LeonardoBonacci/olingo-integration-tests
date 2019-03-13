package guru.bonacci.sdl.spring;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.registry.ODataEdmRegistry;

import guru.bonacci.sdl.spring.model.Address;
import guru.bonacci.sdl.spring.model.Person;

@Component
public class EntityRegistrationService {
	private static final Logger LOG = LoggerFactory.getLogger(EntityRegistrationService.class);

	@Autowired
	private ODataEdmRegistry oDataEdmRegistry;

	@Autowired
	private InMemoryDataSource inMemoryDataSource;

	@PostConstruct
	public void registerEntities() throws ODataException {
		LOG.info("Registering example entities");

		oDataEdmRegistry.registerClasses(Arrays.asList(Person.class, Address.class, GetAverageAge.class));

		List<Person> persons = Arrays.asList(
				Person.builder().personId("MyHero").firstName("MyHero").lastName("Duck").age(23).address(new Address("Duck street", 1)).build(),
				Person.builder().personId("Sidekick").firstName("Launchpad").lastName("McQuack").age(35).address(new Address("McQuack street", 2)).build(),
				Person.builder().personId("Waddlemeyer").firstName("Gosalyn").lastName("Mallard").age(9).address(new Address("Mallard street", 3)).build());

		for (Person person : persons) 
			inMemoryDataSource.create(null, person, null);
	}
}
