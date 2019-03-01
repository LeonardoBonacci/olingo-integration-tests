package guru.bonacci.olingo;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.junit.Test;

public class DemoResourceTests {


    @Test
    public void smoke() throws Exception {
    	Client client = ClientBuilder.newClient();
    	WebTarget tut = client.target("http://localhost:8080/DemoService/index.jsp");	
    	System.out.println(tut.request().get());
    }
}
