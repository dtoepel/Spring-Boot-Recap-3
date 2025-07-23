package org.example.springbootrecap.service;

import org.example.springbootrecap.model.ToDo;
import org.example.springbootrecap.repository.ToDoRepo;
import org.springframework.stereotype.Service;

import java.util.Stack;


@Service
public class UndoRedoStack {
    private final Stack<ToDoChange> undoStack = new Stack<>();
    private final Stack<ToDoChange> redoStack = new Stack<>();

    public void logAdd(ToDo toDo) { log(null, toDo); }
    public void logUpdate(ToDo oldToDo, ToDo newToDo) { log(oldToDo, newToDo); }
    public void logDelete(ToDo toDo) { log(toDo, null); }

    private void log(ToDo oldToDo, ToDo newToDo) {
        redoStack.clear();
        undoStack.push(new ToDoChange(oldToDo, newToDo));
    }

    public void undo(ToDoRepo toDoRepo) throws StackEmptyException {
        undoOrRedo(undoStack, redoStack, toDoRepo, "Undo Stack Empty");
    }

    public void redo(ToDoRepo toDoRepo) throws StackEmptyException {
        undoOrRedo(redoStack, undoStack, toDoRepo, "Redo Stack Empty");
    }

    private void undoOrRedo(
            Stack<ToDoChange> fromStack, Stack<ToDoChange> toStack,
            ToDoRepo toDoRepo, String stackEmptyMessage)
            throws StackEmptyException {
        if(fromStack.empty()) throw new StackEmptyException(stackEmptyMessage);

        ToDoChange a = fromStack.pop();
        toStack.push(a.invert());

        perform(a, toDoRepo);
    }

    private void perform(ToDoChange a, ToDoRepo toDoRepo) {
        if(a.oldToDo == null && a.newToDo != null) { // object has been added, remove to undo
            toDoRepo.deleteById(a.newToDo.id());
        } else
        if(a.oldToDo != null && a.newToDo != null) { // object has been updated, update to old
            toDoRepo.save(a.oldToDo);
        } else
        if(a.oldToDo != null && a.newToDo == null) { // object has been deleted, save old
            toDoRepo.save(a.oldToDo);
        } else {
            throw new IllegalArgumentException("Das darf nicht sein...");
        }
    }

    private record ToDoChange(ToDo oldToDo, ToDo newToDo) {
        public ToDoChange invert() {
            return new ToDoChange(newToDo, oldToDo);
        }
    }

    public static class StackEmptyException extends Exception{
        public StackEmptyException(String message) {
            super(message);
        }
    }
}
