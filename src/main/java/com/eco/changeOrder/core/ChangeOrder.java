package com.eco.changeOrder.core;

import com.eco.utils.misc.Dict;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.Valid;

/**
 * Created by neo on 9/1/18.
 */

@Entity
@Table(name = ChangeOrder.TABLE_NAME)
@NamedQueries({
        @NamedQuery(name = "com.eco.changeOrder.core.ChangeOrder.findAll", query = "select c from " + ChangeOrder.TABLE_NAME + " c")
})

public class ChangeOrder {
    public static final String TABLE_NAME = "ChangeOrder";


    @JsonProperty
    @NotEmpty
    @Column(name = Dict.ID, unique = true)
    @Id
    private String id;

    @JsonProperty
    @NotEmpty
    @Column(name = Dict.AUTHOR)
    private String author;

    @JsonProperty
    @Valid
    @Column(name = Dict.DATA)
    private ChangeOrderData data;

    public ChangeOrder() {}

    public ChangeOrder(String id, String author, ChangeOrderData data) {
        this.id = id;
        this.author = author;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ChangeOrderData getData() {
        return data;
    }

    public void setData(ChangeOrderData data) {
        this.data = data;
    }
}
