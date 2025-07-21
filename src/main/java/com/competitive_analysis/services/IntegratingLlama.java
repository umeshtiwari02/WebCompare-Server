package com.competitive_analysis.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class IntegratingLlama {

	private static String staticApiUrl;
    private static String staticApiKey;

    @Value("${groq.api.url}")
    public void setApiUrl(String url) {
        staticApiUrl = url;
    }

    @Value("${groq.api.key}")
    public void setApiKey(String key) {
        staticApiKey = key;
    }

    public static String fetchTextFromUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(10000)
                .get();
        return doc.text(); // Extract all text from the page
    }

    public static String queryCloudLlama(String prompt) throws IOException {

        // Use text blocks for clean JSON formatting
        String requestBody = """
        {
          "model": "llama-3.3-70b-versatile",
          "messages": [
            {
              "role": "user",
              "content": "%s"
            }
          ],
          "temperature": 0.7
        }
        """.formatted(prompt.replace("\"", "\\\"")); // Escape quotes

        HttpURLConnection conn = (HttpURLConnection) new URL(staticApiUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + staticApiKey);
        conn.setRequestProperty("User-Agent", "Java/21");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes());
        }

        // Check response code first
        int status = conn.getResponseCode();
        if (status != 200) {
            try (BufferedReader er = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream()))) {
                String errorLine;
                while ((errorLine = er.readLine()) != null) {
                    System.err.println(errorLine);
                }
            }
            throw new IOException("API request failed with status: " + status);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        return response.toString();
    }

    public static String analyzeUrl(String url, String question) throws IOException {
        String pageText = fetchTextFromUrl(url);
        // In your analyzeUrl method:
        String prompt = String.format(
                "Answer this question based on the following text: Question: %s Text: %s", // Single line
                question,
                pageText.substring(0, Math.min(pageText.length(), 2000))
                        .replace("\"", "'")); // Replace quotes to avoid escaping
        return IntegratingLlama.queryCloudLlama(prompt);
    }
}
