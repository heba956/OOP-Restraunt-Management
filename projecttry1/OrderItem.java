/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projecttry1;
import java.util.Objects;
/**
 *
 * @author malak
 */
public class OrderItem {
    private MenuItem menuItem;
    private int quantity;
    private String notes; 

    public OrderItem(MenuItem menuItem, int quantity, String notes) {
        if (menuItem == null) {
            throw new IllegalArgumentException("MenuItem cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.notes = notes;
    }

    public MenuItem getMenuItem(){
        return menuItem;
    }
    public void setMenuItem(MenuItem menuItem){
         if (menuItem == null) {
        throw new IllegalArgumentException("MenuItem cannot be null");
        }
        this.menuItem = menuItem;
    }
    public int getQuantity(){
        return quantity;
    }
    public void setQuantity(int quantity){
        if (quantity <= 0) {
        throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = quantity;
    }
    public String getNotes(){
        return notes;
    }
    public void setNotes(String notes){
        this.notes = notes;
    }
    @Override
    public String toString(){
        String notePart = (notes == null || notes.isEmpty()) ? "" : " (Note: " + notes + ")";
        return quantity + "x " + menuItem.getName() + notePart + " - $" + String.format("%.2f", getSubtotal());
    }
    public double getSubtotal() {
        return menuItem.getPrice() * quantity;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OrderItem that = (OrderItem) obj;
        return quantity == that.quantity && 
               Objects.equals(menuItem, that.menuItem) &&
               Objects.equals(notes, that.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuItem, quantity, notes);
    }
  

}
