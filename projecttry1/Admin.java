/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projecttry1;
import java.util.Date;
import enums.TableStatus;
import enums.Role;
/**
 *
 * @author malak
 */
public class Admin extends Staff {
    public Admin(String username, String password, int workingHours, Date DateOfBirth){
        super(username, password, Role.ADMIN, workingHours, DateOfBirth);}
    public void createTable(Table table) {
        Database.tables.add(table);
    }
   public Table readTable(int tableNumber) {
        for (Table t : Database.tables) {
            if (t.getTableNumber() == tableNumber) {
                return t;
            }
        }
        return null;
    }

    public void updateTableStatus(int tableNumber, TableStatus newStatus) {
        Table t = readTable(tableNumber);
        if (t != null) {
            t.setStatus(newStatus);
        }
    }

    public void deleteTable(int tableNumber) {
        Table t = readTable(tableNumber);
        if (t != null) {
            Database.tables.remove(t);
        }
    }

    
    public void createMenuItem(MenuItem item) {
        Database.menuItems.add(item);
    }

    public void updateMenuItemPrice(MenuItem item, double newPrice) {
        item.setPrice(newPrice);
    }

    public void updateMenuItemAvailability(MenuItem item, boolean available) {
        item.setAvailable(available);
    }

    public void deleteMenuItem(MenuItem item) {
        Database.menuItems.remove(item);
    }


    public void createMenuCategory(MenuCategory category) {
        Database.menuCategories.add(category);
    }

    public void deleteMenuCategory(MenuCategory category) {
        Database.menuCategories.remove(category);
    }
} 

