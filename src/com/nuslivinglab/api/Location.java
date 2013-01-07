package com.nuslivinglab.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sg.edu.nus.idmi.amilab.db.DBFactory;
import sg.edu.nus.idmi.amilab.db.Database;

/**
 * Servlet implementation class Location
 */
public class Location extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final Logger Log = Logger.getLogger(Location.class.getName());
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Location() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String roomcode = null;
		roomcode = request.getParameter("room_code");
		
		String sql = "select st_x(the_geom), st_y(the_geom) from public.buildings where code = ?";
		new DBFactory();
		Database db = DBFactory.loadDatabase("com.nuslivinglab.db.db_pgsql");
		Connection conn = db.getConnection();
		
		try{
			PreparedStatement preSt = conn.prepareStatement(sql);
			preSt.setString(1, roomcode);
			ResultSet rs = preSt.executeQuery();
			if(rs.next()){
				PrintWriter out = response.getWriter();
				response.setContentType("text/plain");
				out.println(rs.getString(1)+","+rs.getString(2));
			}
		}catch (Exception e) {
			e.printStackTrace();
			Log.info("some error when query with "+roomcode);
		}finally{
			db.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
