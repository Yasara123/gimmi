package gimmi.web;

import gimmi.database.CorpusDatabaseException;
import gimmi.util.ConfigManagerException;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GimmiServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		if (request.getParameter("ajax") != null) {
			// delegate this request to the AJAX class
			RequestHandler ajax = new Ajax(request, response);
			try {
				ajax.parseRequest();
			} catch (SQLException | CorpusDatabaseException
					| ConfigManagerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ajax.getResponse();
		}
	}
}
