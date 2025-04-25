package com.desi.kart.desikart_backend.utility;

import java.util.Random;

public class Utility {

	public static String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000)); // 6-digit OTP
    }
}
