package com.eco.changeOrder.dao;

import com.eco.changeOrder.core.ChangeOrder;
import com.eco.changeOrder.core.ChangeOrderData;
import com.eco.utils.misc.Dict;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by neo on 9/1/18.
 */
public class ChangeOrderMapper implements ResultSetMapper<ChangeOrder> {
    private static final Logger _logger = LoggerFactory.getLogger(ChangeOrderMapper.class);
    @Override
    public ChangeOrder map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        ChangeOrder changeOrder = new ChangeOrder();

        changeOrder.setId(resultSet.getString(Dict.ID));
        changeOrder.setAuthor(resultSet.getString(Dict.AUTHOR));

        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(resultSet.getBinaryStream(Dict.DATA));
            changeOrder.setData(new ChangeOrderData().deserialize(objectInputStream));
        } catch (IOException e) {
            _logger.error("Error: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            _logger.error("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return changeOrder;
    }
}
