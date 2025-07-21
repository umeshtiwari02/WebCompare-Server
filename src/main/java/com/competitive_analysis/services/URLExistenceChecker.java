package com.competitive_analysis.services;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Component
public class URLExistenceChecker {
    public boolean doesURLExist(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD"); // Use HEAD for efficiency
            int responseCode = connection.getResponseCode();

            System.out.println("Url exist");
            // 200-level status codes generally indicate success
            return (responseCode >= 200 && responseCode < 300);

        } catch (MalformedURLException e) {
            System.err.println("Invalid URL format: " + urlString);
            return false;
        } catch (IOException e) {
            System.err.println("Error connecting to URL: " + urlString + " - " + e.getMessage());
            return false;
        }
    }
}
