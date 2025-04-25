package com.desi.kart.desikart_backend.notification;

import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

@Component
public class NotificationService {
	   @PostConstruct
	    public void init() throws IOException {
	        FileInputStream serviceAccount = new FileInputStream("firebase-service-account.json");

	        FirebaseOptions options = FirebaseOptions.builder()
	            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
	            .build();

	        if (FirebaseApp.getApps().isEmpty()) {
	            FirebaseApp.initializeApp(options);
	        }
	    }

	   public void sendOtp(String deviceToken, String otp) throws FirebaseMessagingException {
	        Message message = Message.builder()
	            .setToken(deviceToken)
	            .putData("title", "Verify OTP")
	            .putData("body", "Your OTP is " + otp)
	            .build();

	        FirebaseMessaging.getInstance().send(message);
	    }
}
