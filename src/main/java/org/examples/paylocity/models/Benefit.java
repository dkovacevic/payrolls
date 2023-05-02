package org.examples.paylocity.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

class Benefit {
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