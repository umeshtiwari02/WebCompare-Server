package com.competitive_analysis.controllers;

import com.competitive_analysis.services.IntegratingLlama;
import com.competitive_analysis.services.URLExistenceChecker;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class UrlContentController {

    @Autowired
    private URLExistenceChecker urlExistenceChecker;

    private final IntegratingLlama integratingLlama;

    // Default question if none provided
    private static final String DEFAULT_QUESTION =
            "summarize the main content of this website in maximum 200 words";

    public UrlContentController(IntegratingLlama integratingLlama) {
        this.integratingLlama = integratingLlama;
    }

    @PostMapping("/get-content")
    public String getUrlContent(@RequestBody Map<String, String> request) {
        String url = request.get("url");

        try {
            // checking whether the url exists or not
            boolean isUrlExist = urlExistenceChecker.doesURLExist(url);

            if (isUrlExist) {
                String answer = integratingLlama.analyzeUrl(url, DEFAULT_QUESTION);
                JSONObject json = new JSONObject(answer);
                return json.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            }
            else {
                System.out.println("Url doesn't exists. Please enter correct url.");
                return "Url doesn't exists. Please enter correct url.";
            }
        } catch (IOException e) {
            return "Error processing your request: " + e.getMessage();
        }
    }

    @PostMapping("/get-urls-texts")
    public List<String> getAssociatedLinks(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        List<String> allUrlAndText = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(url).get();
            Elements allLinks = doc.select("a");

            System.out.println("------------");
            for (Element link : allLinks) {
                String href = link.attr("href");
                String text = link.text();

                // Check if it's an external link
                if (href.startsWith("https://") || href.startsWith("http://")) {
                    allUrlAndText.add(href +"\n");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return allUrlAndText;
    }
}
