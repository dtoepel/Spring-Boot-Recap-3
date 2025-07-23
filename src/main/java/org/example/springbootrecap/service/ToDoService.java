package org.example.springbootrecap.service;

import org.example.springbootrecap.model.ToDo;
import org.example.springbootrecap.model.ToDoInputRecord;
import org.example.springbootrecap.repository.ToDoRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ToDoService {
    private final ToDoRepo toDoRepo;
    private final UndoRedoStack undoRedoStack;

    public ToDoService(ToDoRepo toDoRepo, UndoRedoStack undoRedoStack) {
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
        ToDo result = toDoRepo.save(new ToDo(id, toDo.description(), toDo.status()));
        undoRedoStack.logAdd(result);
        return result;
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
