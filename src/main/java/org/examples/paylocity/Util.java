package org.examples.paylocity;

import org.examples.paylocity.DAO.EmployeeDAO;
import org.examples.paylocity.models.Client;
import org.examples.paylocity.models.Employee;

public class Util {
    public static Employee toEmployee(EmployeeDAO.Employee dbo) {
        Employee employee = new Employee();
        employee.id = dbo.employeeId;
        employee.name = dbo.employeeName;
        employee.gross = dbo.gross;
        employee.balance = dbo.benefitBalance;
        return employee;
    }

    public static Client toClient(EmployeeDAO.Employee dbo) {
        Client client = new Client();
        client.id = dbo.clientId;
        client.name = dbo.clientName;
        return client;
    }
}
