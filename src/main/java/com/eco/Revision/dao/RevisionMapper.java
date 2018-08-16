
public class RevisionMapper implements ResultSetMapper<Revision> {
    public Revision map(int i, ResultSet resultSet, StatementContext context) throws SQLException {
        Revision revision = new Revision();

        revision.set(resultSet.getString(Dict.ID));
        revision.set(resultSet.getString(Dict.BRANCH_NAME));
        revision.set(resultSet.getString(Dict.REVISION_ID));
        revision.set(resultSet.getString(Dict.TIME));
        revision.set(resultSet.getString(Dict.AUTHOR));
        revision.set(resultSet.getString(Dict.IS_COMMITTED));
        revision.set(resultSet.getString(Dict.COMMITTER));
        revision.set(resultSet.getString(Dict.COMMIT_ID));
        revision.set(resultSet.getString(Dict.EDIT_TIME));

		try {
			revision.setData(new BookData(resultSet.getBinaryStream(Dict.DATA)));
		} catch (Exception e) {
			throw new SQLException("Cannot map Revision from resultSet : " + e.getMessage());
		}
    }
}
