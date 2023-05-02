package org.examples.paylocity.resources;

import com.codahale.metrics.annotation.Metered;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jersey.errors.ErrorMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.examples.paylocity.DAO.BenefitDAO;
import org.examples.paylocity.DAO.EmployeeBenefitDAO;
import org.jdbi.v3.core.Jdbi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Api
@Path("/benefits")
@Produces(MediaType.APPLICATION_JSON)
public class BenefitsResource {
    private final BenefitDAO benefitDAO;

    public BenefitsResource(Jdbi jdbi) {
        benefitDAO = jdbi.onDemand(BenefitDAO.class);
    }

    @GET
    @ApiOperation(value = "List all available benefits")
    public Response listAllBenefits() {
        try {
            // Get all available benefits
            ArrayList<BenefitDAO.Benefit> all = benefitDAO.getAll();

            return Response
                    .ok(all)
                    .build();
        } catch (RuntimeException e) {
            return Response.
                    status(500).
                    entity(new ErrorMessage(e.getMessage())).
                    build();
        }
    }
}
