package com.MyRepMoney.dataloader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MyRepMoney.SourceDataSet;
import com.MyRepMoney.util.MyRepMoneyException;
import com.cdymek.util.CdymekException;
import com.cdymek.util.sql.ConnectionManager;

/**
 * This is the base class for the DataLoader process.  Specific implementations can be done for each source of the data, with their own
 * unique private and protected methods.  All must implement the process() method, however.
 * @author cdymek
 *
 */
public abstract class DataLoader {

	private static Logger m_logger = null;
	
	/**
	 * Constructor that initializes the logger class
	 */
	protected DataLoader() {
		m_logger = LogManager.getLogger(this.getClass());
	}
	
	/**
	 * This is the method invoked to process the loading of data
	 * @throws MyRepMoneyException
	 */
	public abstract void process(SourceDataSet sourceDataSet) throws MyRepMoneyException;
	
	/**
	 * Method to execute a sql statement that does not return a recordset
	 * @param sql
	 * @throws MyRepMoneyException
	 */
	protected void executeUpdate(String sql) throws MyRepMoneyException {
			
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = ConnectionManager.getInstance().getConnection();

			m_logger.debug("Executing SQL Statement|" + sql);
			pstmt = conn.prepareStatement(sql);	
			pstmt.executeUpdate();
		}
		catch (CdymekException e) {
			m_logger.error("Failed to get connection", e);
			throw new MyRepMoneyException("Failed to get connection", e);
		} catch (SQLException e) {
			m_logger.error("A sql error occurred|" + sql, e);
			throw new MyRepMoneyException("A sql error occurred|" + sql, e);
		}
		finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				conn = null;
			}
			catch (Throwable t) {
				m_logger.warn("Error during db connection cleanup", t);
			}
		}
	}		
	
}
