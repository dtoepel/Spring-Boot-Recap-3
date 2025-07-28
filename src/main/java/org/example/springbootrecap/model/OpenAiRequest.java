package org.example.springbootrecap.model;

import java.util.List;

public record OpenAiRequest(String model, List<OpenAiMessage> messages) {
}
