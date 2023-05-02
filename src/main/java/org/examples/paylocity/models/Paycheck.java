package org.examples.paylocity.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Paycheck {
    @JsonProperty
    public Integer id;

    @JsonProperty
    public Client client;

    @JsonProperty
    public Employee employee;

    @JsonProperty
    public Float benefits;
    @JsonProperty
    public Float gross;
    @JsonProperty
    public Float net;

    public Float total;

}
