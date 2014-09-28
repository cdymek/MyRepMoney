package com.MyRepMoney.dataloader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

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
		m_logger.debug("Initializing DataLoader");
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
		
		try {
			conn = ConnectionManager.getInstance().getConnection();
			executeUpdate(conn, sql);
		}
		catch (CdymekException e) {
			m_logger.error("Failed to get connection", e);
			throw new MyRepMoneyException("Failed to get connection", e);
		}
		finally {

			try {
				if (conn != null) {
					conn.close();
				}
			}
			catch (Throwable t) {
				m_logger.warn("Error during db connection cleanup", t);
			}
		}
	}	
	
	/**
	 * Method used to execute a SQL Statement when using a provided Connection object as part of a larger transaction
	 * @param conn
	 * @param sql
	 * @throws MyRepMoneyException
	 */
	protected void executeUpdate(Connection conn, String sql) throws MyRepMoneyException {
		
		PreparedStatement pstmt = null;
		long startTimeMillis = System.currentTimeMillis();
		
		try {
			m_logger.debug("Executing SQL Statement|" + sql);
			pstmt = conn.prepareStatement(sql);	
			pstmt.executeUpdate();
			m_logger.debug("SQL Statement Executed|" + sql);
		}
		catch (SQLException e) {
			m_logger.error("A sql error occurred|" + sql, e);
			throw new MyRepMoneyException("A sql error occurred|" + sql, e);
		}
		finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			}
			catch (Throwable t) {
				m_logger.warn("Error during db connection cleanup", t);
			}
			long runTime = System.currentTimeMillis() - startTimeMillis;
			m_logger.debug("Query Processing Time|" + sql + "|" + runTime + " ms");
		}
	}	
	/**
	 * Method which executes a sql statement and returns a generic object array containing the results
	 * @param sql
	 * @return
	 * @throws CdymekException
	 */
	protected Object[] execute(String sql) throws MyRepMoneyException {
		Object resultSet[] = new Object[0];
		Connection conn = null;
		
		try {
			conn = ConnectionManager.getInstance().getConnection();
			resultSet = execute(conn, sql);
		}
		catch (CdymekException e) {
			m_logger.error("Failed to get connection", e);
			throw new MyRepMoneyException("Failed to get connection|" + sql, e);
		} 
		finally {

			try {
				if (conn != null) {
					conn.close();
				}
			}
			catch (Throwable t) {
				m_logger.warn("Error during db connection cleanup", t);
			}
		}
		return resultSet;
	}

	/**
	 * Method which executes a sql statement using the Connection object provided.  Used as part of a transaction.
	 * @param conn
	 * @param sql
	 * @return
	 * @throws MyRepMoneyException
	 */
	protected Object[] execute(Connection conn, String sql) throws MyRepMoneyException {
		Object resultSet[] = new Object[0];
		PreparedStatement pstmt = null;
		long startTimeMillis = System.currentTimeMillis();
		
		try {

			m_logger.debug("Executing SQL Statement|" + sql);
			pstmt = conn.prepareStatement(sql);	
			ResultSet rs = pstmt.executeQuery();
			resultSet = formatResults(rs);
			m_logger.debug("SQL Statement Executed|" + sql);
		}
		catch (SQLException e) {
			m_logger.error("A sql error occurred|" + sql, e);
			throw new MyRepMoneyException("A sql error occurred|" + sql, e);
		}
		finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			}
			catch (Throwable t) {
				m_logger.warn("Error during db connection cleanup", t);
			}
			long runTime = System.currentTimeMillis() - startTimeMillis;
			m_logger.debug("Query Processing Time|" + sql + "|" + runTime + " ms");
		}

		return resultSet;
	}
	
	/**
	 * Method that formats a resultSet in to a generic array
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private Object[] formatResults(ResultSet rs) throws SQLException {
		ArrayList alResultSet = new ArrayList();
		
		//Define the array size
		ResultSetMetaData rsm = rs.getMetaData();		
		int cols = rsm.getColumnCount();
		
		while (rs.next()) {
			ArrayList alRow = new ArrayList();
			
			for (int i = 1; i <= cols; i++) {
				alRow.add(rs.getObject(i));
			}
			Object row[] = new Object[alRow.size()];
			row = (Object[])alRow.toArray(row);
			alResultSet.add(row);
		}
		Object resultSet[] = new Object[alResultSet.size()];
		resultSet = (Object[])alResultSet.toArray(resultSet);
		return resultSet;
	}
}
