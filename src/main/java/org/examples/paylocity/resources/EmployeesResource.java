package org.examples.paylocity.resources;

import io.dropwizard.jersey.errors.ErrorMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.examples.paylocity.DAO.EmployeeDAO;
import org.examples.paylocity.Util;
import org.examples.paylocity.models.Employee;
import org.jdbi.v3.core.Jdbi;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api
@Path("/employees")
@Produces(MediaType.APPLICATION_JSON)
public class EmployeesResource {
    private final EmployeeDAO employeeDAO;

    public EmployeesResource(Jdbi jdbi) {
        employeeDAO = jdbi.onDemand(EmployeeDAO.class);
    }

    @GET
    @Path("self")
    @ApiOperation(value = "Get myself", response = Employee.class)
    public Response getMyself(@ApiParam @NotNull @HeaderParam("p__employee_id") Integer employeeId,
                              @ApiParam @NotNull @HeaderParam("p__client_id") Integer clientId) {
        try {
            EmployeeDAO.Employee dbo = employeeDAO.get(clientId, employeeId);
            if (dbo == null) {
                return Response.
                        status(404).
                        entity(new ErrorMessage(404, "Unknown client or employee")).
                        build();
            }

            Employee employee = Util.toEmployee(dbo);

            return Response
                    .ok(employee)
                    .build();
        } catch (RuntimeException e) {
            return Response.
                    status(500).
                    entity(new ErrorMessage(e.getMessage())).
                    build();
        }
    }
}
