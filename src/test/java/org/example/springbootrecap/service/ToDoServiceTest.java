package org.example.springbootrecap.service;

import org.example.springbootrecap.model.ToDo;
import org.example.springbootrecap.model.ToDoStatus;
import org.example.springbootrecap.repository.ToDoRepo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

class ToDoServiceTest {

    @Test
    void emptyUndoTest() {
        UndoRedoStack undoRedoStack = new UndoRedoStack();
        ToDoRepo repo = Mockito.mock(ToDoRepo.class);
        ToDoService service = new ToDoService(repo, undoRedoStack);

        /* force error...
        ToDo todo = new ToDo("foo", "Foodoo....", ToDoStatus.OPEN);
        undoRedoStack.logAdd(todo);*/

        assertThrows(UndoRedoStack.StackEmptyException.class, () -> {
            service.undo();
        }, "Empty Stack should have thrown");
    }

    @Test
    void filledUndoTest() {
        UndoRedoStack undoRedoStack = new UndoRedoStack();
        ToDoRepo repo = Mockito.mock(ToDoRepo.class);
        ToDoService service = new ToDoService(repo, undoRedoStack);
        ToDo todo = new ToDo("foo", "Foooooo....", ToDoStatus.OPEN);
        undoRedoStack.logAdd(todo);
        //Mockito.when(repo.findAll()).thenReturn(new Vector<AsterixCharacter>());

        try{
            service.undo();
        } catch (UndoRedoStack.StackEmptyException e) {
            fail("Non-Empty Stack should not have thrown");
        }

        Mockito.verify(repo, Mockito.times(1)).deleteById("foo");
    }
}