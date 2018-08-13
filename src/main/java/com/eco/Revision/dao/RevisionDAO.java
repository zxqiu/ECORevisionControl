package com.eco.Revision.dao;

import com.eco.utils.misc.Dict;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 * Created by neo on 8/12/18.
 */
public interface RevisionDAO {
    public static final String TABLE_NAME = "revision";

    @SqlUpdate("create table if not exists " + TABLE_NAME + " ("
            + "`" + Dict.ID + "` varchar(192) not null unique,"
            + "primary key (`" + Dict.ID + "`"
            + ") DEFAULT CHARSET=utf8 collate=utf8_unicode_ci;"
    )
    void createTable();
}
