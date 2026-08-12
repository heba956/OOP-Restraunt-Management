/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

public class MenuCategory {
    private String categoryName;
    private String description;
    
    public MenuCategory(String categoryName , String description ) {
        this.categoryName = categoryName;
        this.description = description;
    }
    
    public String getCategoryName(){
        return categoryName;
    }
    
    public String getDescription(){
        return description;
    }
    
    public void setCategoryName(String categoryName){
        this.categoryName = categoryName;
    }
    
    public void setDescription(String description){
        this.description = description;
    }
    
     @Override
    public String toString(){
        return "MenuCategory{" + 
                "categoryName =" + categoryName + 
                ", description =" + description + "}";
    }

}
