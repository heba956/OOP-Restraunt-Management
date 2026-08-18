package projecttry1;

import enums.TableType;
import enums.TableStatus;


public class Table {
    private int tableNumber;
    private int capacity;
    private TableType tableType;
    private TableStatus status;
    private String timeSlot;

    public Table(int tableNumber, int capacity, TableType tableType, TableStatus status) {
        this(tableNumber, capacity, tableType, status, "12:00 PM");
    }

    public Table(int tableNumber, int capacity, TableType tableType, TableStatus status, String timeSlot) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive.");
        }
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.tableType = tableType;
        this.status = status;
        this.timeSlot = timeSlot != null ? timeSlot : "12:00 PM";
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public TableType getTableType() {
        return tableType;
    }

    public TableType getLocation() {
        return tableType;
    }

    public TableStatus getStatus() {
        return status;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public void setCapacity(int capacity) {
        if (capacity > 0) {
            this.capacity = capacity;
        }
    }

    public void setTableType(TableType tableType) {
        this.tableType = tableType;
    }

    public void setLocation(TableType tableType) {
        this.tableType = tableType;
    }

    public void setStatus(TableStatus status) {
        this.status = status;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    @Override
    public String toString() {
        return "Table #" + tableNumber + " (" + capacity + " seats, " + tableType + ", " + status + ")";
    }
}
