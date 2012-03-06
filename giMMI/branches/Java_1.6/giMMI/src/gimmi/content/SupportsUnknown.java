package gimmi.content;

import gimmi.database.CorpusDatabaseException;

import java.sql.SQLException;

public interface SupportsUnknown {
	Number getIdForUnknown() throws SQLException, CorpusDatabaseException;
}
