package guru.bonacci.olingo.server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sdl.odata.controller.AbstractODataController;

@Controller
@RequestMapping("/example.svc/**")
public class WebServiceController extends AbstractODataController {
}
