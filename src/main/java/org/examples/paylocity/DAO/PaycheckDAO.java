package org.examples.paylocity.DAO;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.examples.paylocity.models.Paycheck;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public interface PaycheckDAO {
    @SqlQuery("SELECT * FROM Paycheck WHERE paycheck_id = :paycheckId")
    @RegisterRowMapper(_Mapper.class)
    Paycheck getById(@Bind("paycheckId") Integer paycheckId);

    @SqlQuery("SELECT * FROM Paycheck WHERE employee_id = :employeeId")
    @RegisterRowMapper(_Mapper.class)
    ArrayList<Paycheck> getByEmployee(@Bind("employeeId") Integer employeeId);

    @SqlUpdate("INSERT INTO Paycheck(client_id, employee_id, benefits_paid, gross, net) " +
            "VALUES(:p.clientId, :p.employeeId, :p.benefitsPaid, :p.grossSalary, :p.netSalary)")
    int insert(@BindBean("paycheck") Paycheck p);

    class _Mapper implements RowMapper<Paycheck> {
        @Override
        public Paycheck map(ResultSet resultSet, StatementContext ctx) throws SQLException {
            Paycheck ret = new Paycheck();
            ret.id = resultSet.getInt("paycheck_id");
            ret.start = resultSet.getDate("start_date");
            ret.end = resultSet.getDate("end_date");
            ret.employeeId = resultSet.getInt("employee_id");
            ret.clientId = resultSet.getInt("client_id");
            ret.benefitsPaid = resultSet.getFloat("benefits_paid");
            ret.netSalary = resultSet.getFloat("net");
            ret.grossSalary = resultSet.getFloat("gross");

            return ret;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Paycheck {
        public int id;
        public Date start;
        public Date end;
        public int employeeId;
        public int clientId;
        public Float benefitsPaid;
        public Float netSalary;
        public Float grossSalary;
    }
}
