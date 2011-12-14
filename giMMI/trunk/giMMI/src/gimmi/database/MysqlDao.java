package gimmi.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlDao {
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	private static MysqlDao instance = null;

	private String database = "opinion-crawling";
	private String user = "root";
	private String password = "";

	/**
	 * 
	 * @throws SQLException
	 */
	private MysqlDao() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager
					.getConnection("jdbc:mysql://localhost/" + database + "?"
							+ "user=" + user + "&password=" + password);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gibt Instanz zurueck
	 * 
	 * @return MysqlDao
	 */
	public static MysqlDao getInstance() {
		if (instance == null) {
			try {
				instance = new MysqlDao();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return instance;
	}
	
	public Connection getConnection() {
		return connection;
	}

	public ResultSet getResultset(String query) {
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultSet;
	}

	/**
	 * Speichert einen Datensatz in einer spezifizierten Tabelle und gibt die erzeugte
	 * auto_increment ID zurueck
	 * Bei einem nicht erfolgreichen Speichern, bzw wenn kein auto_increment Wert erzeugt wurde
	 * wurde wird -1 zurueckgegeben
	 * @param table
	 * @param values
	 * @return int
	 */
	public int insert(String table, Row values) {
		StringBuffer cols = new StringBuffer();
		StringBuffer vals = new StringBuffer();
		String[] value = new String[2];

		for (int i = 0; i < (values.getNumOfCols() - 1); i++) {
			value = values.next();
			cols.append("`" + value[0] + "`, ");
			vals.append("'" + value[1] + "', ");
		}
		value = values.next();
		cols.append("`" + value[0] + "`");
		vals.append("'" + value[1] + "'");

		String insert = "INSERT INTO `" + table + "`" + "(" + cols.toString()
				+ ")" + "VALUES" + "(" + vals.toString() + ");";
		System.out.println(insert);
		
		int autoIncKeyFromApi = -1;
		try {
			Statement st = connection.createStatement();
			st.executeUpdate(insert, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = st.getGeneratedKeys();
			if (rs.next()) {
		        autoIncKeyFromApi = rs.getInt(1);
		    } else {
		    	throw new SQLException("Es konnt kein Auto Increment Wert gefunden werden!");
		    	//kreative fehlerbehandlung
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return autoIncKeyFromApi;
	}
}
