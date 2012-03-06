package gimmi.testing.api;

import gimmi.api.Site;
import gimmi.database.CorpusDatabaseException;
import gimmi.util.ConfigManagerException;

import java.net.MalformedURLException;
import java.sql.SQLException;

public class MyTest {
	public static void main(String[] args) throws MalformedURLException,
			SQLException, ConfigManagerException, CorpusDatabaseException {
		Site site = new Site(
				"http://www.foobar.de/lkjfglkk/djyxbcsfd/foo.html", "Deutsch",
				"Österreich", "index.php",
				// "Foo Seite",
				"Foo", "Bar", null, "/home/knut/foo/bar");
		// site.write();
	}
}
