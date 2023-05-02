package org.examples.paylocity.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jersey.errors.ErrorMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.examples.paylocity.DAO.EmployeeBenefitDAO;
import org.examples.paylocity.DAO.EmployeeDAO;
import org.examples.paylocity.DAO.PaycheckDAO;
import org.examples.paylocity.models.Client;
import org.examples.paylocity.models.Employee;
import org.examples.paylocity.models.Paycheck;
import org.jdbi.v3.core.Jdbi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Api
@Path("/admin/payrolls")
@Produces(MediaType.APPLICATION_JSON)
public class PayrollResource {
    private final EmployeeBenefitDAO employeeBenefitDAO;
    private final PaycheckDAO paycheckDAO;
    private final EmployeeDAO employeeDAO;

    public PayrollResource(Jdbi jdbi) {
        employeeBenefitDAO = jdbi.onDemand(EmployeeBenefitDAO.class);
        paycheckDAO = jdbi.onDemand(PaycheckDAO.class);
        employeeDAO = jdbi.onDemand(EmployeeDAO.class);
    }

    @POST
    @ApiOperation(value = "")
    public Response runPayroll(@ApiParam @NotNull @Valid _RunPayroll runPayroll) {
        try {
            Paycheck paycheck = generatePaycheck(runPayroll.clientId, runPayroll.employeeId);

            PaycheckDAO.Paycheck dbo = new PaycheckDAO.Paycheck();
            dbo.clientId = paycheck.client.id;
            dbo.employeeId = paycheck.employee.id;

            int insert = paycheckDAO.insert(dbo);
            if (insert == 0) {
                return Response.
                        status(500).
                        entity(new ErrorMessage("Failed to insert new paycheck :'(")).
                        build();
            }

            return Response
                    .ok(paycheck)
                    .build();
        } catch (RuntimeException e) {
            return Response.
                    status(500).
                    entity(new ErrorMessage(e.getMessage())).
                    build();
        }
    }

    @GET
    @Path("preview")
    @ApiOperation(value = "Preview next paycheck")
    public Response previewNewPayroll(
            @ApiParam @NotNull @QueryParam("client") Integer clientId,
            @ApiParam @NotNull @QueryParam("employee") Integer employeeId) {
        try {
            Paycheck paycheck = generatePaycheck(clientId, employeeId);

            return Response
                    .ok(paycheck)
                    .build();
        } catch (RuntimeException e) {
            return Response.
                    status(500).
                    entity(new ErrorMessage(e.getMessage())).
                    build();
        }
    }

    static class _RunPayroll {
        @JsonProperty
        @NotNull
        public Integer clientId;

        @JsonProperty
        @NotNull
        public Integer employeeId;
    }

    private Paycheck generatePaycheck(Integer clientId, Integer employeeId) {
        Paycheck paycheck = new Paycheck();
        paycheck.employee = new Employee();
        paycheck.client = new Client();

        EmployeeDAO.Employee employee = employeeDAO.get(clientId, employeeId);
        paycheck.employee.id = employee.employeeId;
        paycheck.employee.name = employee.employeeName;
        paycheck.employee.gross = employee.gross;
        paycheck.client.id = employee.clientId;
        paycheck.client.name = employee.clientName;

        ArrayList<EmployeeBenefitDAO.Benefit> benefits = employeeBenefitDAO.selectAllBenefitsForEmployee(employeeId);
        for (EmployeeBenefitDAO.Benefit benefit : benefits) {
            employee.benefitBalance -= benefit.price;
            paycheck.benefits += benefit.price;
        }

        paycheck.gross = employee.gross;
        paycheck.net = paycheck.gross - (0.32f * paycheck.gross);
        paycheck.total = paycheck.net + paycheck.benefits;
        return paycheck;
    }
}
