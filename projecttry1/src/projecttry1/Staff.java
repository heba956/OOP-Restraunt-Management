/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projecttry1;
import java.util.Date;
import java.util.List;
import enums.Role;
/**
 *
 * @author malak
 */
public abstract class Staff {
   
    private String username;
    private String password;
    private Role role;
    private int workingHours;
    private Date dateOfBirth;
    public  Staff(String username,String password,Role role,int workingHours,Date dateOfBirth){
        this.username=username;
        this.password=password;
        this.role=role;
        this.workingHours=workingHours;
        this.dateOfBirth=dateOfBirth;
    }
     public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password){
        this.password=password;
    }
    public String getPassword() {
        return password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }
    public void setWorkingHours(int workingHours) {
        this.workingHours = workingHours;
    }

    public int getWorkingHours() {
        return workingHours;
    }

    public void setDateOfBirth(Date DateOfBirth) {
        this.dateOfBirth = DateOfBirth;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void viewAllCustomers(List<Customer> customers) {
        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);
            System.out.println(c.getUsername() + ", " + c.getDateOfBirth() + ", " +
                    c.getBalance() + ", " + c.getLoyaltyPoints() + ", " + c.getDietaryPreferences());
        }
    }
}
