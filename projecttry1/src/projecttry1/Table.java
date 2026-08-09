/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projecttry1;
import enums.TableType;
import enums.TableStatus;

/**
 *
 * @author malak
 */
public class Table {
     private int tableNumber;
    private int capacity;
    private TableType tableType;
    private TableStatus status;
    
    public Table(int tableNumber , int capacity , TableType tableType , TableStatus status ) {
    this.tableNumber = tableNumber;
    if(capacity <= 0)
    throw new IllegalArgumentException("Capacity must be positive.");
    this.capacity = capacity;   
    this.tableType = tableType;
    this.status = status;
    }
    
    public int getTableNumber(){
        return tableNumber;
    }
    
    public int getCapacity(){
        return capacity;
    }
    
    public TableType getTableType(){
        return tableType;
    }
    
    public TableStatus getStatus(){
        return status;
    }   
    
    public void setTableNumber(int tableNumber){
        this.tableNumber = tableNumber;
    }
    
    public void setCapacity(int capacity){
        if (capacity > 0) {
        this.capacity = capacity;
    }
    }
    
    public void setTableType(TableType location){
        this.tableType = location;
    }
    
    public void setStatus(TableStatus status){
        this.status = status;
    }
    
    @Override
    public String toString(){
        return "Table{" + 
                "tableNumber =" + tableNumber + 
                ", capacity =" + capacity + 
                ", location =" + tableType + 
                ", status =" + status + "}";
    }
}
