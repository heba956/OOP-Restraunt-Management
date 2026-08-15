/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projecttry1;
import java.time.LocalDateTime;
import java.time.LocalTime;
import enums.ReservationStatus;
import enums.TableStatus;
/**
 *
 * @author malak
 */
public class Reservation {
     private Customer customer;
    private Table table;
    private LocalDateTime date;
    private int partySize;
    private ReservationStatus status;

    public Reservation(){

    }
    public Reservation(Customer customer,Table table,LocalDateTime date,int partySize) {
        if (partySize <= 0 || partySize > table.getCapacity()) {
    throw new IllegalArgumentException("Invalid party size.");
    }
        this.customer = customer;
        this.table = table;
        this.date = date;
        this.partySize = partySize;
        this.status = ReservationStatus.ACTIVE;
        //table.setStatus(TableStatus.RESERVED);
    }
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Table getTable(){
        return table;
    }
    public void setTable(Table table){
        this.table = table;
    }

    public LocalDateTime getDate(){
        return date;
    }
    public void setDate(LocalDateTime date){
        this.date = date;
    }

    public int getPartySize(){
        return partySize;
    }
    public void setPartySize(int partySize){
        this.partySize = partySize;
    }

    public boolean isValidReservation() {

        if (partySize <= 0 || partySize > table.getCapacity()) {
          return false;
        }

        if (date.isBefore(LocalDateTime.now())) {
          return false;
        }

        LocalTime time = date.toLocalTime();

        if (time.isBefore(LocalTime.of(10, 0)) ||
          time.isAfter(LocalTime.of(23, 0))) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Customer: " + customer.getUsername() +
           "\nTable: " + table.getTableNumber() +
           "\nDate: " + date +
           "\nParty Size: " + partySize
        + "\nStatus: " + status;
    }

    public void cancelReservation() {
    status = ReservationStatus.CANCELLED;
    table.setStatus(TableStatus.AVAILABLE);
    }

    public boolean updateReservation(Table table, LocalDateTime date,int partySize) {

        if (partySize <= 0 || partySize > table.getCapacity())
            return false;

        if (date.isBefore(LocalDateTime.now()))
           return false;

        this.table = table;
        this.date = date;
        this.partySize = partySize;
        if (!isValidReservation()) {
        return false;}


        return true;
    }
    public ReservationStatus getStatus() { //added set and get
    return status;
}

public void setStatus(ReservationStatus status) {
    this.status = status;
}

}



