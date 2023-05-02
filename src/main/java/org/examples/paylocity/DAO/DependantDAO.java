package org.examples.paylocity.DAO;

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

public interface DependantDAO {
    @SqlUpdate("INSERT INTO Dependant (name, employee_id) VALUES (:name, :employeeId)")
    @GetGeneratedKeys
    int insert(@Bind("name") String name, @Bind("employeeId") Integer employeeId);

    @SqlQuery("SELECT * FROM Dependant WHERE employee_id = :employeeId")
    @RegisterRowMapper(_Mapper.class)
    ArrayList<Dependant> selectAllDependants(@Bind("employeeId") Integer employeeId);

    @SqlQuery("SELECT * FROM Dependant WHERE dependant_id = :dependantId")
    @RegisterRowMapper(_Mapper.class)
    Dependant get(@Bind("dependantId") Integer dependantId);

    class _Mapper implements RowMapper<Dependant> {
        @Override
        public Dependant map(ResultSet resultSet, StatementContext ctx) throws SQLException {
            Dependant ret = new Dependant();
            ret.id = resultSet.getInt("dependant_id");
            ret.employeeId = resultSet.getInt("employee_id");
            ret.name = resultSet.getString("name");
            return ret;
        }
    }

    public class Dependant {
        public int id;
        public int employeeId;
        public String name;
    }
}
