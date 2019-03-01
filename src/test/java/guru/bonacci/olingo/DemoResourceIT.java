package guru.bonacci.olingo;

import static com.palantir.docker.compose.logging.LogDirectory.circleAwareLogDirectory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.configuration.ProjectName;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;

public class DemoResourceIT {

	// DO NOT FORGET (on windows)
	// DOCKER_COMPOSE_LOCATION=C:\Program Files\Docker\Docker\resources\bin\docker-compose.exe
	// DOCKER_LOCATION=C:\Program Files\Docker\Docker\resources\bin\docker.exe
	
	private static final int PORT = 8080;
    private static final String SERVICE = "demo-service";

    @ClassRule
    public static DockerComposeRule docker = DockerComposeRule.builder()
            .file("src/test/resources/docker-compose-it.yml")
            .saveLogsTo(circleAwareLogDirectory(DemoResourceIT.class))
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
    	WebTarget tut = client.target(endpoint + "/index.jsp");	
    	System.out.println(tut.request().get().getStatus());

    	tut = client.target(endpoint + "/DemoService/DemoService.svc/Products(1)");	
    	System.out.println(tut.request(MediaType.APPLICATION_JSON).get().getStatus());
    }
}
