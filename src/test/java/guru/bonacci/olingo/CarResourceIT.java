package guru.bonacci.olingo;

import static com.palantir.docker.compose.logging.LogDirectory.circleAwareLogDirectory;
import static guru.bonacci.olingo.client.Printer.print;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.configuration.ProjectName;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;

import guru.bonacci.olingo.client.CRD;
import guru.bonacci.olingo.client.Printer;

public class CarResourceIT {

	// DO NOT FORGET (on windows)
	// DOCKER_COMPOSE_LOCATION=C:\Program Files\Docker\Docker\resources\bin\docker-compose.exe
	// DOCKER_LOCATION=C:\Program Files\Docker\Docker\resources\bin\docker.exe
	
	private static final int PORT = 8080;
    private static final String SERVICE = "car-service";

    @ClassRule
    public static DockerComposeRule docker = DockerComposeRule.builder()
            .file("src/test/resources/docker-compose-it.yml")
            .saveLogsTo(circleAwareLogDirectory(CarResourceIT.class))
            .projectName(ProjectName.random())
            .waitingForService(SERVICE, HealthChecks.toHaveAllPortsOpen())
            .build();

    static DockerPort dockerPort;
    
    @BeforeClass
    public static void initialize() {
        dockerPort = docker.containers()
		                .container(SERVICE)
		                .port(PORT);
    }

    @Test
    public void smoke() throws Exception {
    	String endpoint = String.format("http://%s:%s", dockerPort.getIp(), dockerPort.getExternalPort());
        Client client = ClientBuilder.newClient();
    	WebTarget tut = client.target(endpoint + "/CarService/index.jsp");	
    	assertThat(tut.request().get().getStatus(), is(equalTo(200)));

    	tut = client.target(endpoint + "/CarService/cars.svc/Cars");	
    	assertThat(tut.request(MediaType.APPLICATION_JSON).get().getStatus(), is(equalTo(200)));
    }
    
    @Test
    public void olingoClientCRD() throws Exception {
    	String endpoint = String.format("http://%s:%s", dockerPort.getIp(), dockerPort.getExternalPort());
    	CRD crd = new CRD(ODataClientFactory.getClient());
    	String serviceUrl = endpoint + "/CarService/cars.svc";
    	
    	Edm edm = crd.readEdm(serviceUrl);
        List<FullQualifiedName> ctFqns = new ArrayList<FullQualifiedName>();
        List<FullQualifiedName> etFqns = new ArrayList<FullQualifiedName>();
        for (EdmSchema schema : edm.getSchemas()) {
          for (EdmComplexType complexType : schema.getComplexTypes()) {
            ctFqns.add(complexType.getFullQualifiedName());
          }
          for (EdmEntityType entityType : schema.getEntityTypes()) {
            etFqns.add(entityType.getFullQualifiedName());
          }
        }

        assertThat(ctFqns, Matchers.contains(new FullQualifiedName("olingo.odata.sample.Address")));
        assertThat(etFqns, Matchers.containsInAnyOrder(new FullQualifiedName("olingo.odata.sample.Manufacturer")
        											 , new FullQualifiedName("olingo.odata.sample.Car")));
        
        print("\n----- Create Valid Entry ------------------------------");
        ClientEntity ce = crd.loadEntity("/mymanufacturer.json");
        assertNull(ce.getProperty("Id"));
        
        print("\n Sending entity for creation");
        Printer.prettyPrint(ce.getProperties(), 10);
        
        ClientEntity entry = crd.createEntity(edm, serviceUrl, "Manufacturers", ce);

        print("\n Receiving entity from creation");
        Printer.prettyPrint(entry.getProperties(), 10);
        assertNotNull(entry.getProperty("Id"));
        print("\n---- Creation finished ----------------------------------");


        print("\n----- Create Invalid Entry ------------------------------");
        ClientEntity ce2 = crd.loadEntity("/mymanufacturer2.json");

        print("\n Sending entity for creation");
        Printer.prettyPrint(ce2.getProperties(), 10);
        try {
        	crd.createEntity(edm, serviceUrl, "Manufacturers", ce2);
        } catch (ODataClientErrorException ex) {
        	// returns with http code 400
        	ex.printStackTrace();
        	fail("'Foo' can not be mapped as a property or an annotation.");
        }
        print("\n---- Creation finished ----------------------------------");

        
        print("\n----- Delete Entry ------------------------------");
        int sc = crd.deleteEntity(serviceUrl, "Manufacturers", 123);
        print("\n Deletion of Entry was successfully: " + sc);
        print("\n---- Deletion finished ----------------------------------");

    }    

}
