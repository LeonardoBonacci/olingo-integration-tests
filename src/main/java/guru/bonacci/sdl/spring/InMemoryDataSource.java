package guru.bonacci.sdl.spring;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.ODataSystemException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.processor.datasource.DataSource;
import com.sdl.odata.api.processor.datasource.ODataDataSourceException;
import com.sdl.odata.api.processor.datasource.TransactionalDataSource;
import com.sdl.odata.api.processor.link.ODataLink;

import guru.bonacci.sdl.spring.model.Person;
import scala.Option;

@Component
public class InMemoryDataSource implements DataSource {

    private ConcurrentMap<String, Person> personConcurrentMap = new ConcurrentHashMap<>();


    @Override
    public Object create(ODataUri oDataUri, Object o, EntityDataModel entityDataModel) throws ODataException {
        Person person = (Person) o;
    	System.out.println("INSERTING " + person);
        if(personConcurrentMap.putIfAbsent(person.getPersonId(), person) != null) {
            throw new ODataDataSourceException("Could not create person, already exists");
        }

        return person;
    }

    @Override
    public Object update(ODataUri oDataUri, Object o, EntityDataModel entityDataModel) throws ODataException {
        Person person = (Person) o;
        if(personConcurrentMap.containsKey(person.getPersonId())) {
            personConcurrentMap.put(person.getPersonId(), person);

            return person;
        } else {
            throw new ODataDataSourceException("Unable to update person, entity does not exist");
        }
    }

    @Override
    public void delete(ODataUri oDataUri, EntityDataModel entityDataModel) throws ODataException {
        Option<Object> person = ODataUriUtil.extractEntityWithKeys(oDataUri, entityDataModel);
        if(person.isDefined()) {
            Person p = (Person) person.get();
            personConcurrentMap.remove(p.getPersonId());
        }
    }

    @Override
    public TransactionalDataSource startTransaction() {
        throw new ODataSystemException("No support for transactions");
    }

    public ConcurrentMap<String, Person> getPersonConcurrentMap() {
        return personConcurrentMap;
    }

    @Override
    public void createLink(ODataUri oDataUri, ODataLink oDataLink, EntityDataModel entityDataModel) throws ODataException {

    }

    @Override
    public void deleteLink(ODataUri oDataUri, ODataLink oDataLink, EntityDataModel entityDataModel) throws ODataException {

    }
}
