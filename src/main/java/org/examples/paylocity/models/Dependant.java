package org.examples.paylocity.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Dependant {

    public Dependant(String name, Integer id, Integer employerId) {
        this.name = name;
        this.id = id;
        this.employerId = employerId;
    }

    @JsonProperty
    public String name;
    @JsonProperty
    public Integer id;
    @JsonProperty
    public Integer employerId;
}
