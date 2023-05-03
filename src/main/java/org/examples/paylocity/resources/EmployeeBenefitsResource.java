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
@Path("/employees/benefits")
@Produces(MediaType.APPLICATION_JSON)
public class EmployeeBenefitsResource {
    private final BenefitDAO benefitDAO;
    private final EmployeeBenefitDAO employeeBenefitDAO;

    public EmployeeBenefitsResource(Jdbi jdbi) {
        benefitDAO = jdbi.onDemand(BenefitDAO.class);
        employeeBenefitDAO = jdbi.onDemand(EmployeeBenefitDAO.class);
    }

    @POST
    @ApiOperation(value = "Add new benefit for the Employee")
    @Metered
    public Response insertBenefit(@ApiParam @NotNull @HeaderParam("p__employee_id") Integer employeeId,
                                  @ApiParam @Valid OptInBenefit optInBenefit) {
        try {
            BenefitDAO.Benefit benefitDbo = benefitDAO.get(optInBenefit.benefitId);

            float price = benefitDbo.price;
            if (optInBenefit.name.startsWith("A") || optInBenefit.name.startsWith("a")) {
                price -= price / 10.0;
            }
            // todo: Should we check if the Employee has the budget for this benefit?

            // Insert into DB
            int id = employeeBenefitDAO.insert(benefitDbo.id, employeeId, price, optInBenefit.dependant);

            return Response
                    .ok()
                    .build();
        } catch (RuntimeException e) {
            return Response.
                    status(500).
                    entity(new ErrorMessage(e.getMessage())).
                    build();
        }
    }

    @GET
    @ApiOperation(value = "List all benefits for the Employee",
            responseContainer = "List",
            response = BenefitDAO.Benefit.class)
    public Response allBenefitsForEmployee(@ApiParam @NotNull @HeaderParam("p__employee_id") Integer employeeId,
                                           @ApiParam @QueryParam("paid") Boolean paid) {
        try {
            if (paid != null) {
                // Get all employee's benefits filtered by 'paid' status
                ArrayList<BenefitDAO.Benefit> all = benefitDAO.selectAllBenefitsForEmployee(employeeId, paid);

                return Response
                        .ok(all)
                        .build();
            }

            // Get all employee's benefits
            ArrayList<BenefitDAO.Benefit> all = benefitDAO.selectAllBenefitsForEmployee(employeeId);
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

    static class OptInBenefit {
        @JsonProperty
        @NotNull
        public Integer benefitId;
        @JsonProperty
        public boolean dependant;
        @JsonProperty
        @NotNull
        public String name;
    }
}
