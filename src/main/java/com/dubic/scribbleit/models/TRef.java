/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dubic.scribbleit.models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author dubem
 */
@Entity
@Table(name="tref")
public class TRef implements Serializable {
    @Id
    @Column(name = "id")
    private int id = 1;
    
    @Column(name = "tref",unique = true)
    private Long ref = 100L;

    public TRef() {
    }

    public TRef(Long ref) {
        this.ref = ref;
    }

    
    public Long getRef() {
        return ref;
    }

    public void setRef(Long ref) {
        this.ref = ref;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
}
