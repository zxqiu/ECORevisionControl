package com.eco.revision.dao;

import com.eco.revision.core.Revision;
import com.eco.revision.core.RevisionData;
import com.eco.utils.misc.Dict;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RevisionMapper implements ResultSetMapper<Revision> {
    public Revision map(int i, ResultSet resultSet, StatementContext context) throws SQLException {
        Revision revision = new Revision();

        revision.setId(resultSet.getString(Dict.ID));
        revision.setBranchName(resultSet.getString(Dict.BRANCH_NAME));
        revision.setRevisionId(resultSet.getString(Dict.REVISION_ID));
        revision.setTime(resultSet.getDate(Dict.TIME));
        revision.setAuthor(resultSet.getString(Dict.AUTHOR));
        revision.setCommitted(resultSet.getBoolean(Dict.IS_COMMITTED));
        revision.setCommitter(resultSet.getString(Dict.COMMITTER));
        revision.setCommitId(resultSet.getString(Dict.COMMIT_ID));
        revision.setEditTime(resultSet.getDate(Dict.EDIT_TIME));

		try {
			revision.setData(new RevisionData(resultSet.getBinaryStream(Dict.DATA)));
		} catch (Exception e) {
			throw new SQLException("Cannot map revision from resultSet : " + e.getMessage());
		}

		return revision;
    }
}
