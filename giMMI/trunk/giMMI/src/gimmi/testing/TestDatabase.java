package gimmi.testing;

import gimmi.database.Table;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestDatabase extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Table tblSites;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws IOException, ServletException {
	response.setContentType("text/html");
	PrintWriter writer = response.getWriter();

	writer.println("<html>");
	writer.println("<head>");
	writer.println("<title>testDatabase</title>");
	writer.println("</head>");
	writer.println("<body bgcolor=white>");
	try {
	    this.tblSites = new Table("sites");
	    writer.println("Table 'sites' seems to be accessible!");

	} catch (SQLException e) {
	    writer.println("Cannot access table 'sites' .. see log!");
	    e.printStackTrace();
	}
	writer.println("</body>");
	writer.println("</html>");
    }
}
