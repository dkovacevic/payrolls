package org.examples.paylocity.DAO;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public interface BenefitDAO {
    @SqlQuery("SELECT * FROM Benefit WHERE benefit_id = :benefitId")
    @RegisterRowMapper(_Mapper.class)
    Benefit get(@Bind("benefitId") Integer benefitId);

    @SqlQuery("SELECT * FROM Benefit")
    @RegisterRowMapper(_Mapper.class)
    ArrayList<Benefit> getAll();

    @SqlQuery("SELECT B.benefit_id, EB.price, B.name, EB.dependant, EB.paid " +
            "FROM Employee_Benefit AS EB " +
            "JOIN Benefit AS B " +
            "USING(benefit_id) " +
            "WHERE employee_id = :employeeId")
    @RegisterRowMapper(_Mapper2.class)
    ArrayList<Benefit> selectAllBenefitsForEmployee(@Bind("employeeId") Integer employeeId);

    @SqlQuery("SELECT B.benefit_id, EB.price, B.name, EB.dependant, EB.paid " +
            "FROM Employee_Benefit AS EB " +
            "JOIN Benefit AS B " +
            "USING(benefit_id) " +
            "WHERE employee_id = :employeeId AND EB.paid = :paid")
    @RegisterRowMapper(_Mapper2.class)
    ArrayList<Benefit> selectAllBenefitsForEmployee(@Bind("employeeId") Integer employeeId,
                                                    @Bind("paid") boolean paid);

    @SqlUpdate("DELETE FROM Benefit WHERE benefit_id = :benefitId")
    int delete(@Bind("benefitId") Integer benefitId);

    class _Mapper implements RowMapper<Benefit> {
        @Override
        public Benefit map(ResultSet resultSet, StatementContext ctx) throws SQLException {
            Benefit ret = new Benefit();
            ret.id = resultSet.getInt("benefit_id");
            ret.name = resultSet.getString("name");
            ret.price = resultSet.getFloat("price");
            return ret;
        }
    }

    class _Mapper2 implements RowMapper<Benefit> {
        @Override
        public Benefit map(ResultSet resultSet, StatementContext ctx) throws SQLException {
            Benefit ret = new Benefit();
            ret.id = resultSet.getInt("benefit_id");
            ret.name = resultSet.getString("name");
            ret.price = resultSet.getFloat("price");
            ret.dependant = resultSet.getBoolean("dependant");
            ret.paid = resultSet.getBoolean("paid");
            return ret;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    class Benefit {
        public int id;
        public float price;
        public String name;
        public Boolean paid;
        public Boolean dependant;
    }
}
