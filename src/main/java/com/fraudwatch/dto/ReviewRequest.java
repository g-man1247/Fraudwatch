package com.fraudwatch.dto;

import com.fraudwatch.model.enums.Outcome;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequest {
    @NotNull(message = "Outcome is required")
    private Outcome outcome;

    private String reviewer;
    private String comments;
}
