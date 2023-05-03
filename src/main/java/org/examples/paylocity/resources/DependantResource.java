package org.examples.paylocity.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jersey.errors.ErrorMessage;
import io.dropwizard.validation.OneOf;
import io.swagger.annotations.Api;
import org.examples.paylocity.App;
import org.examples.paylocity.DAO.DependantDAO;
import org.examples.paylocity.DAO.EmployeeDAO;
import org.examples.paylocity.models.Dependant;
import org.jdbi.v3.core.Jdbi;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.*;

@Api
@Path("/dependants")
@Produces(MediaType.APPLICATION_JSON)
public class DependantResource {
    private final DependantDAO dependantDAO;
    private final EmployeeDAO employeeDAO;

    public DependantResource(Jdbi jdbi) {
        dependantDAO = jdbi.onDemand(DependantDAO.class);
        employeeDAO = jdbi.onDemand(EmployeeDAO.class);
    }

    @POST
    @ApiOperation(value = "Add new dependant for the Employee", response = Dependant.class)
    public Response insertDependant(@ApiParam @NotNull @HeaderParam("p__employee_id") Integer employeeId,
                                    @ApiParam @Valid _NewDependant dependant) {
        try {
            //todo run all db updates as a transaction

            // Insert new dependant into DB
            int dependantId = dependantDAO.insert(dependant.name, employeeId);

            // Update Employee's balance after adding new dependant
            employeeDAO.increaseBalance(employeeId, App.config.allowanceDependant);

            // Fetch from the DB in order to return the db object exactly as it is
            DependantDAO.Dependant dbo = dependantDAO.get(dependantId);

            // Copy dbo into model class
            Dependant res = new Dependant(dbo.id, dbo.name, dbo.employeeId);

            return Response
                    .ok(res)
                    .build();
        } catch (RuntimeException e) {
            return Response.
                    status(500).
                    entity(new ErrorMessage(e.getMessage())).
                    build();
        }
    }

    @GET
    @ApiOperation(value = "Get all dependants for the Employee", response = Dependant.class, responseContainer = "List")
    public Response listDependants(@ApiParam @NotNull @HeaderParam("p__employee_id") Integer employeeId) {
        try {
            // Get all dbo for the employeeID
            ArrayList<DependantDAO.Dependant> dependants = dependantDAO.selectAllDependants(employeeId);

            // Copy dbo into model class
            List<Dependant> result = dependants.stream()
                    .map(x -> new Dependant(x.id, x.name, x.employeeId))
                    .toList();

            return Response
                    .ok(result)
                    .build();
        } catch (RuntimeException e) {
            return Response.
                    status(500).
                    entity(new ErrorMessage(e.getMessage())).
                    build();
        }
    }

    static class _NewDependant {
        @JsonProperty
        @NotEmpty
        public String name;

        @OneOf(value = {"spouse", "child"}, ignoreCase = true, ignoreWhitespace = true)
        @NotNull
        public String type;

    }
}
