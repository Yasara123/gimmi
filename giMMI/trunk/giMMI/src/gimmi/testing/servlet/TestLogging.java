package gimmi.testing.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestLogging extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ServletContext context;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws IOException, ServletException {
	response.setContentType("text/html");
	PrintWriter writer = response.getWriter();

	this.context = this.getServletContext();
	this.context.log("Test LOG entry");

	writer.println("<html>");
	writer.println("<head>");
	writer.println("<title>test</title>");
	writer.println("</head>");
	writer.println("<body bgcolor=white>");
	writer.println("Log entry has been written!");
	writer.println("</body>");
	writer.println("</html>");
    }
}
