package org.examples.paylocity.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Dependant {
    @JsonCreator
    public Dependant(Integer id,String name, Integer employerId) {
        this.name = name;
        this.id = id;
        this.employerId = employerId;
    }

    @JsonProperty
    public Integer id;

    @JsonProperty
    public String name;
    @JsonProperty
    public Integer employerId;
}
