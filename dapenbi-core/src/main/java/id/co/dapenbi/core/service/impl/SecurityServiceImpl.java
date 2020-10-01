package id.co.dapenbi.core.service.impl;

import java.net.URI;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import id.co.dapenbi.core.security.SecurityProfile;
import id.co.dapenbi.core.finder.UserFinder;
import id.co.dapenbi.core.model.User;
import id.co.dapenbi.core.service.SecurityService;
import id.co.dapenbi.core.util.DateUtil;
import id.co.dapenbi.core.util.EncryptionUtil;
import id.co.dapenbi.core.util.SessionUtil;

@Service
@Repository
public class SecurityServiceImpl implements SecurityService {

	private static final Logger logger = LoggerFactory.getLogger(SecurityServiceImpl.class);

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	Environment env;

	@Override
	public SecurityProfile getSecurityProfile() {
		// TODO Auto-generated method stub
		try {
			SecurityProfile securityProfile = (SecurityProfile) SecurityContextHolder.getContext().getAuthentication()
					.getDetails();
			return securityProfile;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public SecurityProfile getSecurityProfile(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getActiveUserByUsername(String username) throws Exception {
		// TODO Auto-generated method stub
		RestTemplate restTemplate = new RestTemplate();

		final String apiUrl = env.getProperty("dapenbi.api.login");
		final String programName = env.getProperty("dapenbi.api.programName").replace("_", " ");
		URI uri = new URI(apiUrl);

		UserFinder finder = new UserFinder();
		finder.setNip(username);
		finder.setProgramName(programName);
		
		User user = new User();
		
		try {
			ResponseEntity<String> result = restTemplate.postForEntity(uri, finder, String.class);

			JsonObject jsonObject = new JsonParser().parse(result.getBody()).getAsJsonObject();

			if (!jsonObject.get("data").isJsonNull()) {
				JsonObject dataObject = jsonObject.get("data").getAsJsonObject();

				if (dataObject != null) {
					Date dateTo = DateUtil.stringToDate(dataObject.get("dateTo").getAsString());
					Date dateFrom = DateUtil.stringToDate(dataObject.get("dateFrom").getAsString());

					if (DateUtil.isBetween(new Date(), dateFrom, dateTo)) {
						EncryptionUtil encryptionUtil = new EncryptionUtil();
						String password = encryptionUtil.decrypt(dataObject.get("password").getAsString());

						user.setNip(dataObject.get("nip").getAsString());
						user.setName(dataObject.get("name").getAsString());
						user.setPassword(password);
						user.setDateFrom(dateFrom);
						user.setDateTo(dateTo);
						user.setCreatedBy(dataObject.get("createdBy").getAsString());
						user.setCreationDate(DateUtil.stringToDate(dataObject.get("creationDate").getAsString()));
						user.setLastUpdatedBy(dataObject.get("lastUpdatedBy").getAsString());
						user.setLastUpdateDate(DateUtil.stringToDate(dataObject.get("lastUpdateDate").getAsString()));
						SessionUtil.setSession("satker", dataObject.get("satker").getAsString());
					}
				}
			} else {
				SessionUtil.setSession("loginErrorMessage", "Invalid username or password!");
			}
		} catch (Exception ex) {
			SessionUtil.setSession("loginErrorMessage", "Connection Timeout!");
		}

		return user;
	}

}
