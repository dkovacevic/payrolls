package org.examples.paylocity.DAO;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface EmployeeDAO {
    @SqlQuery("SELECT E.*, C.* " +
            "FROM Employee AS E " +
            "JOIN Client AS C " +
            "USING(employee_id) " +
            "WHERE employee_id = :employeeId AND client_id = :clientId")
    @RegisterRowMapper(_Mapper.class)
    Employee get(@Bind("clientId") Integer clientId, @Bind("employeeId") Integer employeeId);

    class _Mapper implements RowMapper<Employee> {
        @Override
        public Employee map(ResultSet resultSet, StatementContext ctx) throws SQLException {
            Employee ret = new Employee();
            ret.employeeId = resultSet.getInt("employee_id");
            ret.clientId = resultSet.getInt("client_id");
            ret.employeeName = resultSet.getString("E.name");
            ret.clientName = resultSet.getString("C.name");
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
