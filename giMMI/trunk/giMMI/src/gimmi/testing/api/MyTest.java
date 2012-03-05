package gimmi.testing.api;

import java.net.MalformedURLException;
import java.sql.SQLException;

import gimmi.api.Site;
import gimmi.database.CorpusDatabaseException;
import gimmi.util.ConfigManagerException;

public class MyTest {
	public static void main(String[] args) throws MalformedURLException, SQLException, ConfigManagerException, CorpusDatabaseException {
		Site site = new Site("http://www.foobar.de", 
							 "Deutsch", 
							 "Österreich", 
							 "index.php", 
							 "Foo Seite", 
							 null,
							 "Bar", 
							 null,
							 "/home/knut/foo/bar");
		//site.write();
	}
}
