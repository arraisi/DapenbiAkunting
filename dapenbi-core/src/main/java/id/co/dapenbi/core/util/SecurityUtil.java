package id.co.dapenbi.core.util;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import id.co.dapenbi.core.finder.UserPrivilegeFinder;
import id.co.dapenbi.core.model.Privilege;

@Component
public class SecurityUtil {

	private static Environment env;

	@Autowired
	Environment _env;

	@PostConstruct
	public void init() {
		env = _env;
	}

	public static String getUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		return authentication.getName();
	}

	public static Privilege getUserPrivilege(String username, String menuUrl) throws URISyntaxException {
		RestTemplate restTemplate = new RestTemplate();

		final String apiUrl = env.getProperty("dapenbi.api.getPrivilege");
		final String programName = env.getProperty("dapenbi.api.programName").replace("_", " ");

		URI uri = new URI(apiUrl);

		UserPrivilegeFinder finder = new UserPrivilegeFinder();
		finder.setUsername(username);
		finder.setMenuUrl(menuUrl);
		finder.setProgramName(programName);

		ResponseEntity<String> result = null;

		result = restTemplate.postForEntity(uri, finder, String.class);

		JsonObject jsonObject = new JsonParser().parse(result.getBody()).getAsJsonObject();

		if (jsonObject.get("error").getAsBoolean()) {
			menuUrl = menuUrl.substring(1, menuUrl.length());
			menuUrl = menuUrl.substring(menuUrl.indexOf("/"), menuUrl.length());
			finder.setMenuUrl(menuUrl);

			result = restTemplate.postForEntity(uri, finder, String.class);
		}

		jsonObject = new JsonParser().parse(result.getBody()).getAsJsonObject();

		Privilege privilege = new Privilege();
		if (!jsonObject.get("data").isJsonNull()) {

			JsonObject dataObject = jsonObject.get("data").getAsJsonObject();

			if (dataObject != null) {
				privilege.setFlagRead(Boolean.parseBoolean(dataObject.get("flagRead").getAsString()));
				privilege.setFlagEdit(Boolean.parseBoolean(dataObject.get("flagEdit").getAsString()));
				privilege.setFlagDelete(Boolean.parseBoolean(dataObject.get("flagDelete").getAsString()));
				privilege.setFlagValidation(Boolean.parseBoolean(dataObject.get("flagValidation").getAsString()));
				privilege.setFlagPrint(Boolean.parseBoolean(dataObject.get("flagPrint").getAsString()));
			}
		}

		return privilege;
	}
}
