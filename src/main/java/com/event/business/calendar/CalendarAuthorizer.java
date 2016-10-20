package com.event.business.calendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;


/**
 * A class that helps you getting started with the google API. You have to run
 * this only once, please make sure to follow the instructions in the command
 * line. The output of the execution is a refresh token, which you have to put
 * into the static variable in the {@link ApplicationTest} to test if it works.
 * 
 * @author Felix Dobslaw
 * 
 */
public class CalendarAuthorizer {

	protected static String REDIRECT_URL = "urn:ietf:wg:oauth:2.0:oob";

	private final NetHttpTransport httpTransport;
	private final com.google.api.client.json.jackson2.JacksonFactory jsonFactory;
	private GoogleAuthorizationCodeFlow flow;
	private GoogleTokenResponse response;

	private String clientId;
	private String clientSecret;

	private String authorizationUrl;

	public CalendarAuthorizer(String clientId, String clientSecret) {
		httpTransport = new NetHttpTransport();
		jsonFactory = new com.google.api.client.json.jackson2.JacksonFactory();
		resetClient(clientId, clientSecret);
	}

	public String getRefreshToken(String code) throws IOException {
		response = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URL)
				.execute();
		return response.getRefreshToken();
	}

	public String getAuthorizationUrl() {
		return authorizationUrl;
	}

	public final void resetClient(String clientId, String clientSecret) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,
				jsonFactory, clientId, clientSecret,
				Arrays.asList(CalendarScopes.CALENDAR))
				.setAccessType("offline").setApprovalPrompt("auto").build();

		authorizationUrl = flow.newAuthorizationUrl()
				.setRedirectUri(REDIRECT_URL).setAccessType("offline").build();

	}

	public boolean isAuthorized() {
		GoogleCredential credential = new GoogleCredential.Builder()
				.setClientSecrets(clientId, clientSecret)
				.setJsonFactory(jsonFactory).setTransport(httpTransport)
				.build().setRefreshToken(response.getRefreshToken())
				.setAccessToken(response.getAccessToken());

		Calendar c = new Calendar.Builder(httpTransport, jsonFactory,
				credential).build();
		return c != null;
	}

	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Please, first enter your client ID:");
		String clientId = br.readLine();
		System.out.println("Now, enter your client secret:");
		String clientSecret = br.readLine();

		CalendarAuthorizer authorizer = new CalendarAuthorizer(clientId,
				clientSecret);
		String authUrl = authorizer.getAuthorizationUrl();

		System.out
				.println("Please open the following URL in your browser (Make sure to be logged in with your Google account).");
		System.out.println("  " + authUrl);
		System.out
				.println("Accept the license agreement and copy the posted authorization code here:");
		String code = br.readLine();

		String refreshToken = authorizer.getRefreshToken(code);
		System.out
				.println("\nMake sure to keep the following code, the so called refresh token (You need it for your application):");
		System.out.println(refreshToken);

		if (authorizer.isAuthorized())
			System.out
					.println("The Calendar works now, and you have registered your refresh token.");
		else
			System.out
					.println("An error occured, please carefully read the exception.");

	}
}
