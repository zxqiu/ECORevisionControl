package com.eco.DBI;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * Created by neo on 8/17/18.
 */
public interface TestDAO {
    public static final String TABLE_NAME = "JDBITestTable";

    @SqlUpdate("create table " + TABLE_NAME + " ("
                    + "strItem varchar(256) primary key,"
                    + "intItem int,"
                    + "dateItem time"
                    + ")")
    void createTable();

    @SqlUpdate("drop table if exists " + TABLE_NAME)
    void dropTable();

    @SqlUpdate("insert into " + TABLE_NAME + " ("
            + "strItem"
            + ", intItem"
            + ", dateItem"
            + ") values ("
            + ":strItem"
            + ", :intItem"
            + ", :dateItem"
            + ")"
    )
    void insert(
            @Bind("strItem") String strItem
            ,@Bind("intItem") int intItem
            ,@Bind("dateItem") Date dateItem
    );

    @SqlQuery("select strItem from " + TABLE_NAME + " where strItem = :strItem")
    String findByName(@Bind("strItem") String strItem);

    @SqlQuery("select intItem from " + TABLE_NAME + " order by intItem asc")
    List<Integer> findAllInt();

    @SqlQuery("select distinct intItem from " + TABLE_NAME + " order by intItem asc")
    Set<Integer> findAllDistinctInt();
}
