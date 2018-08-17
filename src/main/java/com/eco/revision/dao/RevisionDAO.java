package com.eco.revision.dao;

import com.eco.revision.core.Revision;
import com.eco.utils.misc.Dict;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.Date;
import java.util.List;

/**
 * Created by neo on 8/12/18.
 */
public interface RevisionDAO {
    public static final String TABLE_NAME = "revision";

    @SqlUpdate("create table if not exists " + TABLE_NAME + " ("
            + "`" + Dict.ID + "` varchar(128) not null unique,"
            + "`" + Dict.BRANCH_NAME + "` varchar(64) not null,"
            + "`" + Dict.REVISION_ID + "` varchar(32) not null,"
            + "`" + Dict.TIME + "` date not null,"
            + "`" + Dict.AUTHOR + "` varchar(32) not null,"
            + "`" + Dict.IS_COMMITTED + "` boolean not null,"
            + "`" + Dict.COMMITTER + "` varchar(32),"
            + "`" + Dict.COMMIT_ID + "` varchar(32),"
            + "`" + Dict.EDIT_TIME + "` date,"
            + "`" + Dict.DATA + "` blob,"
            + "primary key (`" + Dict.ID + "`)"
            + ");"
    )
    void createTable();

    @SqlUpdate("drop table if exists " + TABLE_NAME)
    void dropTable();

    @SqlUpdate("insert into " + TABLE_NAME + " ("
            + Dict.ID
            + ", " + Dict.BRANCH_NAME
            + ", " + Dict.REVISION_ID
            + ", " + Dict.TIME
            + ", " + Dict.AUTHOR
            + ", " + Dict.IS_COMMITTED
            + ", " + Dict.COMMITTER
            + ", " + Dict.COMMIT_ID
            + ", " + Dict.EDIT_TIME
            + ", " + Dict.DATA
            + ") values ("
            + ":" + Dict.ID
            + ", :" + Dict.BRANCH_NAME
            + ", :" + Dict.REVISION_ID
            + ", :" + Dict.TIME
            + ", :" + Dict.AUTHOR
            + ", :" + Dict.IS_COMMITTED
            + ", :" + Dict.COMMITTER
            + ", :" + Dict.COMMIT_ID
            + ", :" + Dict.EDIT_TIME
            + ", :" + Dict.DATA
            + ")"
    )
    void insert(
            @Bind(Dict.ID) String id
            ,@Bind(Dict.BRANCH_NAME) String branchName
            ,@Bind(Dict.REVISION_ID) String revisionID
            ,@Bind(Dict.TIME) Date time
            ,@Bind(Dict.AUTHOR) String author
            ,@Bind(Dict.IS_COMMITTED) boolean isCommitted
            ,@Bind(Dict.COMMITTER) String committer
            ,@Bind(Dict.COMMIT_ID) String commitID
            ,@Bind(Dict.EDIT_TIME) Date editTime
            ,@Bind(Dict.DATA) byte[] data
    );

    @SqlUpdate("update " + TABLE_NAME + " set "
            + Dict.IS_COMMITTED + "= :" + Dict.IS_COMMITTED
            + ", " + Dict.COMMITTER + "= :" + Dict.COMMITTER
            + ", " + Dict.COMMIT_ID + "= :" + Dict.COMMIT_ID
            + ", " + Dict.EDIT_TIME + "= :" + Dict.EDIT_TIME
            + " where " + Dict.ID + "= :" + Dict.ID
    )
    void updateCommitInfoByID(@Bind(Dict.ID) String id
            , @Bind(Dict.IS_COMMITTED) boolean isCommitted
            , @Bind(Dict.COMMITTER) String committer
            , @Bind(Dict.COMMIT_ID) String commitID
            , @Bind(Dict.EDIT_TIME) Date editTime
    );

    @SqlQuery("select * from " + TABLE_NAME)
    @Mapper(RevisionMapper.class)
    List<Revision> findAll();

    @SqlQuery("select * from " + TABLE_NAME + " where " + Dict.ID + " = :" + Dict.ID)
    @Mapper(RevisionMapper.class)
    List<Revision> findByID(@Bind(Dict.ID) String id);

    @SqlUpdate("delete from " + TABLE_NAME + " where " + Dict.ID + " = :" + Dict.ID)
    void deleteByID(
            @Bind(Dict.ID) String id
    );
}
