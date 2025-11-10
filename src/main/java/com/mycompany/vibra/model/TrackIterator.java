/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vibra.model;

/**
 *
 * @author robbiebelen
 */
public interface TrackIterator {
    
    //Retrieves the next track in the collection and advances the iterator.
    boolean hasNext();
    Track next();
    
    // Checks if there is a previous track in the collection
    boolean hasPrevious();
    Track previous();
   
}
