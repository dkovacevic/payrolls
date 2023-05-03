package org.examples.paylocity.DAO;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface EmployeeDAO {
    @SqlQuery("SELECT E.*, C.*, E.name as employee, C.name AS Client " +
            "FROM Client AS C " +
            "JOIN Employee AS E " +
            "USING(client_id) " +
            "WHERE client_id = :clientId AND employee_id = :employeeId" )
    @RegisterRowMapper(_Mapper.class)
    Employee get(@Bind("clientId") Integer clientId, @Bind("employeeId") Integer employeeId);

    class _Mapper implements RowMapper<Employee> {
        @Override
        public Employee map(ResultSet resultSet, StatementContext ctx) throws SQLException {
            Employee ret = new Employee();
            ret.employeeId = resultSet.getInt("employee_id");
            ret.clientId = resultSet.getInt("client_id");
            ret.employeeName = resultSet.getString("Employee");
            ret.clientName = resultSet.getString("Client");
            ret.clientId = resultSet.getInt("client_id");
            ret.benefitBalance = resultSet.getFloat("benefit_balance");
            ret.gross = resultSet.getFloat("gross");
            return ret;
        }
    }

    class Employee {
        public Integer employeeId;
        public Integer clientId;

        public String clientName;

        public String employeeName;
        public Float benefitBalance;
        public Float gross;

    }
}
