package com.garden.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class CloudinaryConfig {

    private static String CLOUD_NAME; // CLOUD_NAME

    private static String API_KEY; // CLOUDINARY_API_KEY

    private static String API_SECRET; // CLOUDINARY_API_SECRET

    public CloudinaryConfig(@Value(value = "${cloud.name}") String cloudName,
            @Value(value = "${cloud.apikey}") String apiKey,
            @Value(value = "${cloud.apisecret}") String apiSecret) {
        CloudinaryConfig.CLOUD_NAME = cloudName;
        CloudinaryConfig.API_KEY = apiKey;
        CloudinaryConfig.API_SECRET = apiSecret;
    }

    public static Cloudinary configureCloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", CLOUD_NAME,
                "api_key", API_KEY,
                "api_secret", API_SECRET,
                "secure", true));
    }

}
