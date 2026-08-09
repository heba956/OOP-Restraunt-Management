/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projecttry1;

/**
 *
 * @author malak
 */
public class MenuItem {
   
    private String name;
    private double price;
    private String description;
    private MenuCategory category;
    private boolean available;

    public MenuItem( String name , double price , String description , MenuCategory category , boolean available ) {
    if (price >= 0) {
        this.price = price;
    }
    if (name == null || name.isBlank()) {
    throw new IllegalArgumentException("Name cannot be empty.");
}
        this.name = name;
    this.price = price;
    this.description = description;
    this.category = category;
    this.available = available;
    }
    
    public String getName(){
        return name;
    }
    
    public double getPrice(){
        return price;
    }
    
    public String getDescription(){
        return description;
    }
    
    public MenuCategory getCategory(){
        return category;
    }
    
    public boolean isAvailable(){
        return available;
    }
    
    public void setName(String name){
        if (name == null || name.isBlank()) {
    throw new IllegalArgumentException("Name cannot be empty.");
}
        this.name = name;
    }
    
    public void setPrice(double price){
        if (price >= 0) {
        this.price = price;
    }
    }
    
    public void setDescription(String description){
        this.description = description;
    }
    
    public void setCategory(MenuCategory category){
        this.category = category;
    }
    
    public void setAvailable(boolean available){
        this.available = available;
    }
    
    @Override
    public String toString(){
        return "MenuItem{" + 
                "name =" + name + 
                ", price =" + price + 
                ", description =" + description + 
                ", category =" + category +
                ", available =" + available + "}";
    }
    }
