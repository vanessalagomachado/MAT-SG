/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufsc.model;

/**
 *
 * @author vanes
 */
public enum SemanticType {
    CATEGORICAL("categorical"), NUMERICAL("numerical");
    
    
    public String description;

    private SemanticType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
    
    
    
    
}
