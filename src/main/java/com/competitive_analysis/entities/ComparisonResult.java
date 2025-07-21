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
public class ComparisonResult {
    private double contentSimilarity;
    private boolean sameTitle;
    private boolean sameDescription;
    private List<String> commonHeadings;
    private List<String> commonLinks;
    private List<String> titleDifferences;
    private List<String> descriptionDifferences;
    // constructor/getters
}
