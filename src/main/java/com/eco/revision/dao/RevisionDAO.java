package com.eco.revision.dao;

import com.eco.revision.core.Revision;
import com.eco.utils.misc.Dict;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.util.StringColumnMapper;
import org.skife.jdbi.v2.util.StringMapper;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
            + "`" + Dict.STATUS + "` int not null,"
            + "`" + Dict.EDITOR + "` varchar(32),"
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
            + ", " + Dict.STATUS
            + ", " + Dict.EDITOR
            + ", " + Dict.COMMIT_ID
            + ", " + Dict.EDIT_TIME
            + ", " + Dict.DATA
            + ") values ("
            + ":" + Dict.ID
            + ", :" + Dict.BRANCH_NAME
            + ", :" + Dict.REVISION_ID
            + ", :" + Dict.TIME
            + ", :" + Dict.AUTHOR
            + ", :" + Dict.STATUS
            + ", :" + Dict.EDITOR
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
            ,@Bind(Dict.STATUS) int status
            ,@Bind(Dict.EDITOR) String editor
            ,@Bind(Dict.COMMIT_ID) String commitID
            ,@Bind(Dict.EDIT_TIME) Date editTime
            ,@Bind(Dict.DATA) byte[] data
    );

    @SqlBatch("insert into " + TABLE_NAME + " ("
            + Dict.ID
            + ", " + Dict.BRANCH_NAME
            + ", " + Dict.REVISION_ID
            + ", " + Dict.TIME
            + ", " + Dict.AUTHOR
            + ", " + Dict.STATUS
            + ", " + Dict.EDITOR
            + ", " + Dict.COMMIT_ID
            + ", " + Dict.EDIT_TIME
            + ", " + Dict.DATA
            + ") values ("
            + ":" + Dict.ID
            + ", :" + Dict.BRANCH_NAME
            + ", :" + Dict.REVISION_ID
            + ", :" + Dict.TIME
            + ", :" + Dict.AUTHOR
            + ", :" + Dict.STATUS
            + ", :" + Dict.EDITOR
            + ", :" + Dict.COMMIT_ID
            + ", :" + Dict.EDIT_TIME
            + ", :" + Dict.DATA
            + ")"
    )
    void insertBatch(
            @Bind(Dict.ID) List<String> id
            ,@Bind(Dict.BRANCH_NAME) List<String> branchName
            ,@Bind(Dict.REVISION_ID) List<String> revisionID
            ,@Bind(Dict.TIME) List<Date> time
            ,@Bind(Dict.AUTHOR) List<String> author
            ,@Bind(Dict.STATUS) List<Integer> status
            ,@Bind(Dict.EDITOR) List<String> editor
            ,@Bind(Dict.COMMIT_ID) List<String> commitID
            ,@Bind(Dict.EDIT_TIME) List<Date> editTime
            ,@Bind(Dict.DATA) List<byte[]> data
    );

    @SqlUpdate("update " + TABLE_NAME + " set "
            + Dict.STATUS + "= :" + Dict.STATUS
            + ", " + Dict.EDITOR + "= :" + Dict.EDITOR
            + ", " + Dict.COMMIT_ID + "= :" + Dict.COMMIT_ID
            + ", " + Dict.EDIT_TIME + "= :" + Dict.EDIT_TIME
            + " where " + Dict.ID + "= :" + Dict.ID
    )
    void updateByID(@Bind(Dict.ID) String id
            , @Bind(Dict.STATUS) int status
            , @Bind(Dict.EDITOR) String editor
            , @Bind(Dict.COMMIT_ID) String commitID
            , @Bind(Dict.EDIT_TIME) Date editTime
    );

    @SqlQuery("select * from " + TABLE_NAME)
    @Mapper(RevisionMapper.class)
    List<Revision> findAll();

    @SqlQuery("select * from " + TABLE_NAME + " where " + Dict.BRANCH_NAME + " = :" + Dict.BRANCH_NAME
            + " order by cast(`" + Dict.REVISION_ID + "` as bigint) desc")
    @Mapper(RevisionMapper.class)
    List<Revision> findByBranch(@Bind(Dict.BRANCH_NAME) String branchName);

    @SqlQuery("select * from " + TABLE_NAME + " where " + Dict.BRANCH_NAME + " = :" + Dict.BRANCH_NAME
            + " order by cast(`" + Dict.REVISION_ID + "` as bigint) desc limit :" + Dict.BEGIN + ",:" + Dict.END)
    @Mapper(RevisionMapper.class)
    List<Revision> findLimitByBranch(@Bind(Dict.BRANCH_NAME) String branchName
                                    , @Bind(Dict.BEGIN) long begin
                                    , @Bind(Dict.END) long end);

    @SqlQuery("select max( cast(`" + Dict.REVISION_ID + "` as bigint) ) from " + TABLE_NAME + " where " + Dict.BRANCH_NAME + " = :" + Dict.BRANCH_NAME)
    long findLargestRevisionID(@Bind(Dict.BRANCH_NAME) String branchName);

    @SqlQuery("select * from " + TABLE_NAME + " where " + Dict.ID + " = :" + Dict.ID)
    @Mapper(RevisionMapper.class)
    List<Revision> findByID(@Bind(Dict.ID) String id);

    @SqlUpdate("delete from " + TABLE_NAME + " where " + Dict.ID + " = :" + Dict.ID)
    void deleteByID(
            @Bind(Dict.ID) String id
    );
}
