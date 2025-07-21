package com.competitive_analysis.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteData {
    private String title;
    private String metaDescription;
    private List<String> headings;
    private String text;
    private List<String> links;
    // constructor/getters
}
