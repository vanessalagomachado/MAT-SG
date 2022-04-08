/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufsc.model;

import java.util.Objects;

/**
 *
 * @author vanes
 */
public class Attribute {
    private String name;
    private int order;
    private SemanticType type;

    public Attribute(String name) {
        this.name = name;
    }

    public Attribute(String name, int order) {
        this.name = name.trim().toUpperCase();
        this.order = order;
    }
    
    public Attribute(String name, int order, SemanticType type) {
        this.name = name.trim().toUpperCase();
        this.order = order;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim().toUpperCase();
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
    
        public SemanticType getType() {
        return type;
    }

    public void setType(SemanticType type) {
        this.type = type;
    }
    

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Attribute other = (Attribute) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name; //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
