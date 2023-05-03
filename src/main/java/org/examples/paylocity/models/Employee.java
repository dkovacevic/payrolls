package org.examples.paylocity.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Employee {
    @JsonProperty
    public Integer id;

    @JsonProperty
    public String name;
    @JsonProperty
    public Float gross;
    public Float balance;
}
