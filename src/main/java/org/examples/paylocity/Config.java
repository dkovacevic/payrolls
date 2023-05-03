package org.examples.paylocity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

import javax.validation.constraints.NotNull;

public class Config extends Configuration {
    @JsonProperty
    public Float gross;

    @JsonProperty
    public Float allowance;

    @JsonProperty
    public Float allowanceDependant;

    @JsonProperty
    @NotNull
    public DataSourceFactory database;

    @JsonProperty
    public SwaggerBundleConfiguration swagger;

    public SwaggerBundleConfiguration getSwagger() {
        return swagger;
    }
}