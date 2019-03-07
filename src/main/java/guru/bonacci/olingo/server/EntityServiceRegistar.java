package guru.bonacci.olingo.server;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.registry.ODataEdmRegistry;

import guru.bonacci.olingo.datasource.InMemoryDataSource;
import guru.bonacci.olingo.model.Person;

/**
 * @author rdevries
 */
@Component
public class EntityServiceRegistar {
    private static final Logger LOG = LoggerFactory.getLogger(EntityServiceRegistar.class);

    @Autowired
    private ODataEdmRegistry oDataEdmRegistry;

    @Autowired
    private InMemoryDataSource inMemoryDataSource;

    @PostConstruct
    public void registerEntities() throws ODataException {
        LOG.debug("Registering example entities");

        oDataEdmRegistry.registerClasses(Arrays.asList(
                Person.class,
                GetAverageAge.class
        ));

        List<Person> persons = Arrays.asList(
                new Person("MyHero", "Darkwing", "Duck", 23),
                new Person("Sidekick", "Launchpad", "McQuack", 35),
                new Person("Waddlemeyer", "Gosalyn", "Mallard", 9));

        for (Person person : persons) {
            inMemoryDataSource.create(null, person, null);
        }
    }
}
