package org.example.springbootrecap.controller;

import org.example.springbootrecap.model.ToDo;
import org.example.springbootrecap.model.ToDoInputRecord;
import org.example.springbootrecap.service.ToDoService;
import org.example.springbootrecap.service.UndoRedoStack;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/todo")
public class ToDoController {

    private final ToDoService toDoService;

    public ToDoController(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    @GetMapping
    public List<ToDo> getAllToDos() {
        return toDoService.getAllToDos();
    }

    @GetMapping("/{id}")
    public ToDo getToDoById(@PathVariable String id) {
        ToDo result = toDoService.getToDoById(id);
        if(result == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Object does not exist");
        } else {
            return result;
        }
    }

    @PostMapping
    public ResponseEntity<ToDo> addToDo(@RequestBody ToDoInputRecord toDo) {
        return new ResponseEntity<ToDo>(
                toDoService.addToDo(toDo),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ToDo> updateToDo(@PathVariable String id, @RequestBody ToDo character) {
        ToDo result =  toDoService.updateToDo(id, character);
        if(result == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Object does not exist");
        } else {
            return new ResponseEntity<ToDo>(result, HttpStatus.CREATED);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteToDo(@PathVariable String id) {
        if(!toDoService.deleteToDo(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Object does not exist");
    }

    @PostMapping("/undo")
    public void undo() {
        try{
            toDoService.undo();
        } catch (UndoRedoStack.StackEmptyException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        }
    }

    @PostMapping("/redo")
    public void redo() {
        try{
            toDoService.redo();
        } catch (UndoRedoStack.StackEmptyException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        }
    }
}