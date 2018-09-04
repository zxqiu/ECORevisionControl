package com.eco.changeOrder.core;

import com.eco.utils.misc.Dict;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;

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
    private static final Logger _logger = LoggerFactory.getLogger(ChangeOrder.class);

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

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        ChangeOrderData changeOrderData = new ChangeOrderData("test comment", new ArrayList<String>());
        ChangeOrder testChangeOrder = new ChangeOrder("testID", "testAuthor", changeOrderData);

        try {
            String json = objectMapper.writeValueAsString(testChangeOrder);
            _logger.info("original: " + json);
            ChangeOrder changeOrderCopy = objectMapper.readValue(json, ChangeOrder.class);
            _logger.info("copied : " + objectMapper.writeValueAsString(changeOrderCopy));
        } catch (JsonProcessingException e) {
            _logger.error(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            _logger.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
