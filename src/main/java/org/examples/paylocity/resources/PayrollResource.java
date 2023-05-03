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
import java.util.Date;

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
    @ApiOperation(value = "Run a payroll for the Client and its Employee",
            response = Paycheck.class)
    public Response runPayroll(
            @ApiParam @NotNull @HeaderParam("p__client_id") Integer clientId,
            @ApiParam @NotNull @Valid _RunPayroll runPayroll) {
        try {
            Paycheck paycheck = generatePaycheck(clientId, runPayroll.employeeId);

            paycheck.id = paycheckDAO.insert(clientId,
                    paycheck.employee.id,
                    runPayroll.start,
                    runPayroll.end,
                    paycheck.benefits,
                    paycheck.gross,
                    paycheck.net);

            if (paycheck.id == 0) {
                return Response.
                        status(500).
                        entity(new ErrorMessage("Failed to insert new paycheck :'(")).
                        build();
            }

            // todo update Employee Balance
            // todo Update Paid flag in Employee_Balance

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
    @ApiOperation(value = "Preview the next paycheck", response = Paycheck.class)
    public Response previewNewPayroll(
            @ApiParam @NotNull @HeaderParam("p__client_id") Integer clientId,
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

    @GET
    @Path("paychecks")
    @ApiOperation(value = "Get the Paycheck by id", response = Paycheck.class)
    public Response getPayroll(
            @ApiParam @NotNull @HeaderParam("p__client_id") Integer clientId,
            @ApiParam @NotNull @QueryParam("id") Integer paycheckId) {
        try {
            PaycheckDAO.Paycheck paycheck = paycheckDAO.getById(clientId, paycheckId);
            if (paycheck == null) {
                return Response.
                        status(404).
                        entity(new ErrorMessage("Unknown paycheck")).
                        build();
            }
            EmployeeDAO.Employee employee = employeeDAO.get(clientId, paycheck.employeeId);

            Paycheck ret = new Paycheck();
            ret.id = paycheck.id;
            ret.benefits = paycheck.benefitsPaid;
            ret.net = paycheck.netSalary;
            ret.gross = paycheck.grossSalary;
            ret.total = paycheck.benefitsPaid + paycheck.netSalary;

            ret.client = new Client();
            ret.client.id = employee.clientId;
            ret.client.name = employee.clientName;

            ret.employee = new Employee();
            ret.employee.id = employee.employeeId;
            ret.employee.name = employee.employeeName;
            ret.employee.gross = employee.gross;
            ret.employee.balance = employee.benefitBalance;

            return Response
                    .ok(ret)
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
        public Integer employeeId;

        @JsonProperty
        @NotNull
        public String start;

        @JsonProperty
        @NotNull
        public String end;
    }

    private Paycheck generatePaycheck(Integer clientId, Integer employeeId) {
        Paycheck paycheck = new Paycheck();
        paycheck.employee = new Employee();
        paycheck.client = new Client();

        EmployeeDAO.Employee employee = employeeDAO.get(clientId, employeeId);
        paycheck.employee.id = employee.employeeId;
        paycheck.employee.name = employee.employeeName;
        paycheck.employee.gross = employee.gross;
        paycheck.employee.balance = employee.benefitBalance;
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
