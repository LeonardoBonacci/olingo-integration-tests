package guru.bonacci.olingo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Requires a running server!!
 */
public class CarResourceTests {

	String endpoint = "http://localhost:8080";
	RestTemplate client = new RestTemplate();

	@Test
	public void smoke() throws Exception {
		ResponseEntity<String> response = client.getForEntity(endpoint + "/example.svc", String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
	}

	@Test
	public void sdlClientCRD() throws Exception {
		String serviceUrl = endpoint + "/example.svc/Persons";

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
		requestJson = readFile("invalid.json");
		entity = new HttpEntity<String>(requestJson, headers);
		response = client.postForEntity(serviceUrl, entity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
		
		getResp = client.getForEntity(serviceUrl, String.class).getBody();
		count = StringUtils.countOccurrencesOf(getResp, "firstName");
		assertThat(count, equalTo(init + 3));
	}

	public String readFile(String fileName) throws IOException {
		InputStream resource = new ClassPathResource(fileName).getInputStream();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource))) {
			return reader.lines().collect(Collectors.joining("\n"));
		} 
	}
}
