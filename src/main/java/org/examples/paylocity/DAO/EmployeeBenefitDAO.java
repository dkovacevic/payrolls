package org.examples.paylocity.DAO;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public interface EmployeeBenefitDAO {
    @SqlUpdate("INSERT INTO Employee_Benefit (benefit_id, employee_id, price, dependant) " +
            "VALUES (:benefitId, :employeeId, :price, :dependant)")
    @GetGeneratedKeys
    int insert(@Bind("benefitId") Integer benefitId,
               @Bind("employeeId") Integer employeeId,
               @Bind("price") float price,
               @Bind("dependant") boolean dependant);


    @SqlQuery("SELECT " +
            "EB.benefit_id, " +
            "EB.price, " +
            "B.name AS Benefit, " +
            "EB.dependant, " +
            "EB.paid " +
            "FROM Employee_Benefit AS EB " +
            "JOIN Benefit AS B " +
            "USING(benefit_id) " +
            "WHERE EB.employee_id = :employeeId AND EB.paid = FALSE")
    @RegisterRowMapper(_Mapper.class)
    ArrayList<Benefit> selectAllBenefitsForEmployee(@Bind("employeeId") Integer employeeId);

    class _Mapper implements RowMapper<Benefit> {
        @Override
        public Benefit map(ResultSet resultSet, StatementContext ctx) throws SQLException {
            Benefit ret = new Benefit();
            ret.benefitId = resultSet.getInt("benefit_id");
            ret.benefitName = resultSet.getString("benefit");
            ret.price = resultSet.getFloat("price");
            ret.dependant = resultSet.getBoolean("dependant");
            ret.paid = resultSet.getBoolean("paid");
            return ret;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    class Benefit {
        public int benefitId;
        public float price;
        public String benefitName;
        public Boolean paid;
        public Boolean dependant;
    }
}
