package gui.tasks;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import projecttry1.Database;
import projecttry1.Table;

import java.util.List;

public class TableAvailabilityService extends ScheduledService<List<Table>> {

    @Override
    protected Task<List<Table>> createTask() {
        return new Task<List<Table>>() {
            @Override
            protected List<Table> call() throws Exception {
                return Database.getTables();
            }
        };
    }
}
