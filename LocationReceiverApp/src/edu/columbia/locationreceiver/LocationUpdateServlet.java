package edu.columbia.locationreceiver;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet called each time a location or sensor reading is sent from a client.
 */
@WebServlet("/LocationUpdateServlet")
public class LocationUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static LocationService locationService = LocationService
			.getInstance();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LocationUpdateServlet() {
		super();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext context = getServletContext();
		String fileName = context.getInitParameter("PropertiesFile");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().write("You've reached the right place!");
		response.flushBuffer();
		response.setStatus(200);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			String json = null;
			BufferedReader reader = request.getReader();
			StringBuilder jBuilder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
				jBuilder.append(line);

			json = jBuilder.toString();

			JSONObject jsonObj = new JSONObject(json);
			locationService.saveLocationDetails(jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
