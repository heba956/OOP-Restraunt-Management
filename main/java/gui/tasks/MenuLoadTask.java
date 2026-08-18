package gui.tasks;

import javafx.concurrent.Task;
import projecttry1.MenuItem;
import projecttry1.Database;

import java.util.List;

public class MenuLoadTask extends Task<List<MenuItem>> {

    @Override
    protected List<MenuItem> call() throws Exception {
        updateMessage("Loading menu...");
        return Database.getMenuItems();
    }
}
