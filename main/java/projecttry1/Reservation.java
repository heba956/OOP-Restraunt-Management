
package projecttry1;
import java.time.LocalDateTime;
import java.time.LocalTime;
import enums.ReservationStatus;
import enums.TableStatus;

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

    public boolean updateReservation(Table newTable, LocalDateTime newDate, int newPartySize) {
        if (newTable == null || newDate == null) {
            return false;
        }
        if (newPartySize <= 0 || newPartySize > newTable.getCapacity()) {
            return false;
        }
        if (newDate.isBefore(LocalDateTime.now())) {
            return false;
        }
        LocalTime time = newDate.toLocalTime();
        if (time.isBefore(LocalTime.of(10, 0)) || time.isAfter(LocalTime.of(23, 0))) {
            return false;
        }

        this.table = newTable;
        this.date = newDate;
        this.partySize = newPartySize;
        return true;
    }
    public ReservationStatus getStatus() { //added set and get
    return status;
}

public void setStatus(ReservationStatus status) {
    this.status = status;
}

}



