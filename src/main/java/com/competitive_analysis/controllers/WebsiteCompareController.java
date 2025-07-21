package com.competitive_analysis.controllers;

import com.competitive_analysis.entities.ComparisonResult;
import com.competitive_analysis.entities.UrlPair;
import com.competitive_analysis.entities.WebsiteData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.text.similarity.JaccardSimilarity;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/compare")
@CrossOrigin
public class WebsiteCompareController {

    @PostMapping
    public ComparisonResult compareWebsites(@RequestBody UrlPair urlPair) {
        try {
            WebsiteData data1 = fetchWebsiteData(urlPair.getUrl1());
            WebsiteData data2 = fetchWebsiteData(urlPair.getUrl2());

            return compareWebsiteData(data1, data2);
        } catch (IOException e) {
            throw new RuntimeException("Failed to compare websites: " + e.getMessage());
        }
    }

    private WebsiteData fetchWebsiteData(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        return new WebsiteData(
                doc.title(),
                Optional.ofNullable(doc.selectFirst("meta[name=description]"))
                        .map(el -> el.attr("content"))
                        .orElse(""),
                extractHeadings(doc),
                doc.body().text().replaceAll("\\s+", " ").trim(),
                extractLinks(doc)
        );
    }

    private List<String> extractHeadings(Document doc) {
        return doc.select("h1, h2, h3")
                .stream()
                .map(Element::text)
                .collect(Collectors.toList());
    }

    private List<String> extractLinks(Document doc) {
        return doc.select("a[href]")
                .stream()
                .map(el -> el.attr("abs:href"))
                .filter(link -> !link.isEmpty())
                .collect(Collectors.toList());
    }

    private ComparisonResult compareWebsiteData(WebsiteData data1, WebsiteData data2) {
        JaccardSimilarity jaccard = new JaccardSimilarity();

        // Compare main text content
        double contentSimilarity = jaccard.apply(data1.getText(), data2.getText());

        // Find common headings
        Set<String> commonHeadings = new HashSet<>(data1.getHeadings());
        commonHeadings.retainAll(data2.getHeadings());

        // Find common links
        Set<String> commonLinks = new HashSet<>(data1.getLinks());
        commonLinks.retainAll(data2.getLinks());

        return new ComparisonResult(
                contentSimilarity,
                data1.getTitle().equals(data2.getTitle()),
                data1.getMetaDescription().equals(data2.getMetaDescription()),
                new ArrayList<>(commonHeadings),
                new ArrayList<>(commonLinks),
                // Differences
                !data1.getTitle().equals(data2.getTitle()) ?
                        Arrays.asList(data1.getTitle(), data2.getTitle()) : null,
                !data1.getMetaDescription().equals(data2.getMetaDescription()) ?
                        Arrays.asList(data1.getMetaDescription(), data2.getMetaDescription()) : null
        );
    }
}
