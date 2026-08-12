/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import enums.TableLocation;
import enums.TableStatus;

public class RestaurantTable {
    private int tableNumber;
    private int capacity;
    private TableLocation location;
    private TableStatus status;
    private String timeSlot;
    
    public RestaurantTable(int tableNumber , int capacity , TableLocation location , TableStatus status , String timeSlot) {
    this.tableNumber = tableNumber;
    this.capacity = capacity;
    this.location = location;
    this.status = status;
    this.timeSlot = timeSlot;
    }
    
    public int getTableNumber(){
        return tableNumber;
    }
    
    public int getCapacity(){
        return capacity;
    }
    
    public TableLocation getLocation(){
        return location;
    }
    
    public TableStatus getStatus(){
        return status;
    }   
    
    public String getTimeSlot(){
        return timeSlot;
    }   
    
    public void setTableNumber(int tableNumber){
        this.tableNumber = tableNumber;
    }
    
    public void setCapacity(int capacity){
        this.capacity = capacity;
    }
    
    public void setLocation(TableLocation location){
        this.location = location;
    }
    
    public void setStatus(TableStatus status){
        this.status = status;
    }
    
    public void setTimeSlot(String timeSlot){
        this.timeSlot = timeSlot;
    }
    
    @Override
    public String toString(){
        return "RestaurantTable{" + 
                "tableNumber =" + tableNumber + 
                ", capacity =" + capacity + 
                ", location =" + location + 
                ", status =" + status + "}";
    }
}
