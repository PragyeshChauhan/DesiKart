package com.desi.kart.desikart_backend.notification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

@Component
public class NotificationService {
	private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

	@PostConstruct
	public void init() throws IOException {
		try {
			FileInputStream serviceAccount = new FileInputStream("src/main/resources/desikart-de70f-firebase-adminsdk.json");
			System.out.println(serviceAccount != null ? "Loaded OK" : "File not found!");
			if (serviceAccount == null) {
				throw new IOException("Firebase service account file not found");
			}
			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.build();
			if (FirebaseApp.getApps().isEmpty()) {
				FirebaseApp.initializeApp(options);
				log.info("FirebaseApp initialized successfully");
			}
		} catch (IOException e) {
			log.error("Failed to initialize Firebase: {}", e.getMessage(), e);
			throw e;
		}
	}

	public void sendOtp(String deviceToken, String otp) throws FirebaseMessagingException {
		log.info("Sending OTP to device token: {}", deviceToken);
		Message message = Message.builder()
				.setToken(deviceToken)
				.putData("title", "Verify OTP")
				.putData("body", "Your OTP is " + otp)
				.build();
		String response = FirebaseMessaging.getInstance().send(message);
		log.info("FCM message sent successfully: {}", response);
	}
}