package org.examples.paylocity.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class Benefit {
    @JsonCreator
    public Benefit(Integer id, String name, Float price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    @JsonProperty
    @NotNull
    public Integer id;

    @JsonProperty
    @NotNull
    public String name;

    @JsonProperty
    @NotNull
    public Float price;
}