package org.example.springbootrecap.model;

import java.util.List;

public record OpenAiResponse(List<OpenAiChoices> choices) {
}
