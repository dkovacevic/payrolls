package org.examples.paylocity.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jersey.errors.ErrorMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.examples.paylocity.DAO.EmployeeBenefitDAO;
import org.examples.paylocity.DAO.EmployeeDAO;
import org.examples.paylocity.DAO.PaycheckDAO;
import org.examples.paylocity.Util;
import org.examples.paylocity.models.Paycheck;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.HandleConsumer;
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
    @ApiOperation(value = "Run a payroll for the Client and its Employee",
            response = Paycheck.class)
    public Response runPayroll(
            @ApiParam @NotNull @HeaderParam("p__client_id") Integer clientId,
            @ApiParam @NotNull @Valid _RunPayroll runPayroll) {
        try {
            Paycheck paycheck = generatePaycheck(clientId, runPayroll.employeeId);

            //todo Run these 3 db updates in a transaction
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

            // Update Employee Balance
            employeeDAO.decreaseBalance(runPayroll.employeeId, paycheck.benefits);

            // Update Paid flag in Employee_Balance
            employeeBenefitDAO.markAllAsPaid(runPayroll.employeeId);

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
            if (employee == null) {
                return Response.
                        status(404).
                        entity(new ErrorMessage(404, "Unknown Client or Employee")).
                        build();
            }

            Paycheck ret = new Paycheck();
            ret.id = paycheck.id;
            ret.benefits = paycheck.benefitsPaid;
            ret.net = paycheck.netSalary;
            ret.gross = paycheck.grossSalary;
            ret.total = paycheck.benefitsPaid + paycheck.netSalary;

            ret.client = Util.toClient(employee);
            ret.employee = Util.toEmployee(employee);

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

        EmployeeDAO.Employee employee = employeeDAO.get(clientId, employeeId);
        if (employee == null) {
            throw new RuntimeException("Unknown client of employee");
        }

        // Calculate the remaining benefit balance for the employee and the total benefits paid by the Client
        ArrayList<EmployeeBenefitDAO.Benefit> benefits = employeeBenefitDAO.selectAllBenefitsForEmployee(employeeId);
        for (EmployeeBenefitDAO.Benefit benefit : benefits) {
            employee.benefitBalance -= benefit.price;
            paycheck.benefits += benefit.price;
        }

        paycheck.employee = Util.toEmployee(employee);
        paycheck.client = Util.toClient(employee);

        paycheck.gross = employee.gross;
        paycheck.net = paycheck.gross - (0.32f * paycheck.gross);
        paycheck.total = paycheck.net + paycheck.benefits;
        return paycheck;
    }
}
