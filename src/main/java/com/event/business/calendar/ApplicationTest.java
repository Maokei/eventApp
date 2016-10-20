package com.event.business.calendar;

import java.io.IOException;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;



/**
 * Assuming that you collected your refresh token via the ApplicationTest class,
 * you can now test if everything works. You could include parts of the code
 * below in order to get access to the calendars from within your enterprise
 * application.
 * 
 * @author Felix Dobslaw
 */
public class ApplicationTest {

	private final String clientId;
	private final String clientSecret;
	private final String refreshToken;

	private final NetHttpTransport httpTransport;
	private final com.google.api.client.json.jackson2.JacksonFactory jsonFactory;

	private final Calendar serviceTest;

	public ApplicationTest(String clientId, String clientSecret,
			String refreshToken) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.refreshToken = refreshToken;

		httpTransport = new NetHttpTransport();
		jsonFactory = new com.google.api.client.json.jackson2.JacksonFactory();

		serviceTest = initServiceTest();
	}

	private Calendar initServiceTest() {
		GoogleCredential credential = new GoogleCredential.Builder()
				.setClientSecrets(clientId, clientSecret)
				.setJsonFactory(jsonFactory).setTransport(httpTransport)
				.build().setRefreshToken(refreshToken);

		return new Calendar.Builder(httpTransport, jsonFactory, credential)
				.build();
	}

	public static void main(String[] args) throws IOException {
		String idStr = "1056802803092-rqvfnov3upnv0ljp7vgo5b0gu5kqnokh.apps.googleusercontent.com";
		String secretStr = "ZVkoxA6fV4fqQ0a9XV9ig6Tq";
		String tokenStr = "1/Xx0SXrxKeIjt_5pyXihlFboY2bSUjjcTMmGizar53ioCD_Q7to7ShlEp0m4ClkS0";
		ApplicationTest appTest = new ApplicationTest(idStr, secretStr,
				tokenStr);

		appTest.insertAndDeleteTestCalendar();
		appTest.listAllCalendars();
	}

	private void listAllCalendars() throws IOException {
		CalendarList calendarList = serviceTest.calendarList().list().execute();

		System.out.println("\nYour calendars:");
		for (CalendarListEntry calendarListEntry : calendarList.getItems())
			System.out.println(calendarListEntry.getSummary());
	}

	private void insertAndDeleteTestCalendar() throws IOException {

		com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();

		calendar.setSummary("testCalendar");
		calendar.setTimeZone("America/Los_Angeles");

		com.google.api.services.calendar.model.Calendar createdCalendar = serviceTest
				.calendars().insert(calendar).execute();

		String id = createdCalendar.getId();

		serviceTest.calendars().delete(id).execute();
	}
}
