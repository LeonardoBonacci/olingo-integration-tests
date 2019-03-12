package guru.bonacci.sdl;


import static com.palantir.docker.compose.logging.LogDirectory.circleAwareLogDirectory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.configuration.ProjectName;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;


public class PersonResourceIT {

	// DO NOT FORGET (on windows)
	// DOCKER_COMPOSE_LOCATION=C:\Program Files\Docker\Docker\resources\bin\docker-compose.exe
	// DOCKER_LOCATION=C:\Program Files\Docker\Docker\resources\bin\docker.exe

	private static final int PORT = 8080;
	private static final String SERVICE = "person-service";

	RestTemplate client = new RestTemplate();

	@ClassRule
	public static DockerComposeRule docker = DockerComposeRule.builder()
			.file("src/test/resources/docker-compose-it.yml").saveLogsTo(circleAwareLogDirectory(PersonResourceIT.class))
			.projectName(ProjectName.random()).waitingForService(SERVICE, HealthChecks.toHaveAllPortsOpen()).build();

	static DockerPort dockerPort;

	@BeforeClass
	public static void initialize() {
		dockerPort = docker.containers().container(SERVICE).port(PORT);
	}

	@Test
	public void smoke() throws Exception {
		String endpoint = String.format("http://%s:%s", dockerPort.getIp(), dockerPort.getExternalPort());
		String serviceUrl = endpoint + "/demo/example.svc";

		Thread.sleep(3000);

		ResponseEntity<String> response = client.getForEntity(serviceUrl, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
	}
	
	@Test
	public void sdlClientCRD() throws Exception {
		String endpoint = String.format("http://%s:%s", dockerPort.getIp(), dockerPort.getExternalPort());
		String serviceUrl = endpoint + "/demo/example.svc/Persons";

		Thread.sleep(3000);

		// count
		String getResp = client.getForEntity(serviceUrl, String.class).getBody();
		int count = StringUtils.countOccurrencesOf(getResp, "firstName");
		int init = 3;
		assertThat(count, equalTo(init));
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String requestJson = readFile("donald.json");
		HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
		ResponseEntity<String> response = client.postForEntity(serviceUrl, entity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
		
		requestJson = readFile("mickey.json");
		entity = new HttpEntity<String>(requestJson, headers);
		response = client.postForEntity(serviceUrl, entity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));

		// this json contains extra fields
		requestJson = readFile("too-much.json");
		entity = new HttpEntity<String>(requestJson, headers);
		response = client.postForEntity(serviceUrl, entity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));

		// this json lacks a nullable field
		requestJson = readFile("too-little.json");
		entity = new HttpEntity<String>(requestJson, headers);
		response = client.postForEntity(serviceUrl, entity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));

		// count again
		getResp = client.getForEntity(serviceUrl, String.class).getBody();
		count = StringUtils.countOccurrencesOf(getResp, "firstName");
		assertThat(count, equalTo(init + 4));
		
		// this json lacks a non-nullable field field
		requestJson = readFile("invalid.json");
		entity = new HttpEntity<String>(requestJson, headers);
		try {
			response = client.postForEntity(serviceUrl, entity, String.class);
			fail();
			} catch(HttpClientErrorException ex) {
			// bad request...
		}
	}

	@Test
	public void functionCall() throws Exception {
		String endpoint = String.format("http://%s:%s", dockerPort.getIp(), dockerPort.getExternalPort());
		String serviceUrl = endpoint + "demo/example.svc/Persons/SDL.OData.Example.GetAverageAge()";

		Thread.sleep(3000);

		ResponseEntity<String> response = client.getForEntity(serviceUrl, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		
		String getResp = response.getBody();
		System.out.println(getResp);
		Assert.assertTrue(getResp.toString().contains("Edm.Double"));

	}
	
	public String readFile(String fileName) throws IOException {
		InputStream resource = new ClassPathResource(fileName).getInputStream();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource))) {
			return reader.lines().collect(Collectors.joining("\n"));
		} 
	}
}
