package com.nuslivinglab.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sg.edu.nus.idmi.amilab.db.DBFactory;
import sg.edu.nus.idmi.amilab.db.Database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

/**
 * Servlet implementation class List
 */
public class List extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static final Logger Log = Logger.getLogger(List.class.getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public List() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String key;
		
		key = request.getParameter("key");
		
		Database db = DBFactory.loadDatabase("com.nuslivinglab.db.db_pgsql");
		Connection conn = db.getConnection();
		String sql = null;
		
		if(key.equals("cuisine")){
			sql = "select distinct cuisine from campus_food.campus_food";
		}else if (key.equals("store_type")){
			sql = "select distinct store_type from campus_food.campus_food";
			
		}else if (key.equals("location")){
			sql = "select distinct location from campus_food.campus_food";
			
		}else if (key.equals("room_code")){
			sql = "select distinct room_code from campus_food.campus_food";
		}else{
			sql = "";
		}
		try{
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			JsonArray jArray = new JsonArray();
			while (rs.next()){
				JsonPrimitive jp = new JsonPrimitive(rs.getString(1));
				jArray.add(jp);				
			}
			
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			
			Gson gson = new GsonBuilder().serializeNulls().create();
			out.print(gson.toJson(jArray));
			
		}catch (Exception e) {
			Log.info("something wrong with the list query with the key as " + key);
			e.printStackTrace();
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
	
	public enum Column {
		CUISINE,
		STORETYPE,
		LOCATION
	}

}
