package org.examples.paylocity.resources;

import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import io.dropwizard.jersey.errors.ErrorMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.examples.paylocity.DAO.BenefitDAO;
import org.examples.paylocity.models.Benefit;
import org.jdbi.v3.core.Jdbi;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Api
@Path("/benefits")
@Produces(MediaType.APPLICATION_JSON)
public class BenefitsResource {
    private final BenefitDAO benefitDAO;

    public BenefitsResource(Jdbi jdbi) {
        benefitDAO = jdbi.onDemand(BenefitDAO.class);
    }

    @GET
    @Timed(name = "benefits.time")
    @Metered(name = "benefits.count")
    @ApiOperation(value = "List all available benefits", response = Benefit.class, responseContainer = "List")
    public Response listAllBenefits() {
        try {
            // Get all available benefits
            ArrayList<BenefitDAO.Benefit> all = benefitDAO.getAll();
            List<Benefit> benefits = all.stream()
                    .map(x -> new Benefit(x.id, x.name, x.price))
                    .toList();

            return Response
                    .ok(benefits)
                    .build();
        } catch (RuntimeException e) {
            return Response.
                    status(500).
                    entity(new ErrorMessage(e.getMessage())).
                    build();
        }
    }

    @GET
    @Path(":benefitId")
    @ApiOperation(value = "Get benefit by id", response = Benefit.class)
    public Response getBenefit(@ApiParam @PathParam("benefitId") Integer benefitId) {
        try {
            // Get get benefit from the db
            BenefitDAO.Benefit dbo = benefitDAO.get(benefitId);
            Benefit benefit = new Benefit(dbo.id, dbo.name, dbo.price);

            return Response
                    .ok(benefit)
                    .build();
        } catch (RuntimeException e) {
            return Response.
                    status(500).
                    entity(new ErrorMessage(e.getMessage())).
                    build();
        }
    }

    @DELETE
    @Path(":benefitId")
    @ApiOperation(value = "Delete benefit by id", response = Benefit.class)
    public Response deleteBenefit(@ApiParam @PathParam("benefitId") Integer benefitId) {
        try {
            // Delete the benefit with this Id from the db
            int rows = benefitDAO.delete(benefitId);
            if (rows == 0) {
                return Response.
                        status(404).
                        entity(new ErrorMessage("Unknown benefitId")).
                        build();
            }
            return Response
                    .accepted()
                    .build();
        } catch (RuntimeException e) {
            return Response.
                    status(500).
                    entity(new ErrorMessage(e.getMessage())).
                    build();
        }
    }
}
