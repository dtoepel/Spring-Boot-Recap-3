package org.example.springbootrecap.service;

import org.example.springbootrecap.model.*;
import org.example.springbootrecap.repository.ToDoRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ToDoService {
    private final ToDoRepo toDoRepo;
    private final UndoRedoStack undoRedoStack;
    private final RestClient aiClient;

    public ToDoService(ToDoRepo toDoRepo,
                       UndoRedoStack undoRedoStack,
                       RestClient.Builder restClientBuilder,
                       @Value("${OPENAI_API_KEY}") String apikey) {

        this.aiClient = restClientBuilder==null?null:restClientBuilder
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apikey)
                .build();
        this.toDoRepo = toDoRepo;
        this.undoRedoStack = undoRedoStack;

    }

    public List<ToDo> getAllToDos() {
        return toDoRepo.findAll();
    }

    public ToDo getToDoById(String id) {
        return toDoRepo.findById(id).orElse(null);
    }

    public ToDo addToDo(ToDoInputRecord toDo) {
        String id = UUID.randomUUID().toString();
        String text = toDo.description();
        try{
            text = improveSpelling(text);
        } catch(AIException e) {
            e.printStackTrace();
        }
        ToDo result = toDoRepo.save(new ToDo(id, text, toDo.status()));
        undoRedoStack.logAdd(result);
        return result;
    }

    private String improveSpelling(String text) throws AIException {
        String question =
                "Identify the language of the following paragraph. " +
                "Then check and correct the spelling of that paragraph according to the identified language. " +
                "If successful, only return the corrected paragraph! " +
                "If unsuccessful return a description of the problem preceded by the word ERROR in uppercase! " +
                "The text to be checked and corrected is as follows:\n\n" + text;

        OpenAiMessage message = new OpenAiMessage("user", question);
        List<OpenAiMessage> messages = new ArrayList<>();
        messages.add(message);
        OpenAiRequest request = new OpenAiRequest("gpt-4o-mini", messages);
        //OpenAiRequest request = new OpenAiRequest("gpt-4.1", messages);

        OpenAiResponse response = getResponse(request);

        if(response.choices().size() != 1) {throw new AIException("Chat malfunction");}
        String answer = response.choices().get(0).message().content();

        /*if(answer.startsWith("ERROR") && ! text.startsWith("ERROR")) {
            return answer;
        }

        if(answer.startsWith("ERROR")) {
            throw new AIException("Could not translate: " + answer);
        }*/

        if(answer.startsWith("ERROR")) {
            answer = answer + " (original was:" + text +")";
        }

        return answer;
    }

    private OpenAiResponse getResponse(OpenAiRequest openAiRequest) {
        return aiClient
                .post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(openAiRequest)
                .retrieve()
                .body(OpenAiResponse.class);
    }

    private static class AIException extends Exception {
        public AIException(String message) {super(message);}
        public AIException(String message, Throwable cause) {super(message, cause);}
        public AIException(Exception cause) {super(cause);}
    }

    public ToDo updateToDo(String id, ToDo toDo) {
        Optional<ToDo> response = toDoRepo.findById(id);
        if(response.isPresent()) {
            toDoRepo.save(toDo);
            undoRedoStack.logUpdate(response.get(), toDo);
            return toDo;
        }
        return null;
    }

    public boolean deleteToDo(String id) {
        Optional<ToDo> response = toDoRepo.findById(id);
        if(response.isPresent()) {
            toDoRepo.deleteById(id);
            undoRedoStack.logDelete(response.get());
            return true;
        }
        return false;
    }

    public void undo() throws UndoRedoStack.StackEmptyException {
        undoRedoStack.undo(toDoRepo);
    }

    public void redo() throws UndoRedoStack.StackEmptyException {
        undoRedoStack.redo(toDoRepo);
    }
}
