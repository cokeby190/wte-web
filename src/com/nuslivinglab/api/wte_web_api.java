package com.nuslivinglab.api;
//add a comment for testing git
//testing GUI
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Normalizer;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sg.edu.nus.idmi.amilab.db.DBFactory;
import sg.edu.nus.idmi.amilab.db.Database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class wte_web_api
 */
public class wte_web_api extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//constructor
    public wte_web_api() { }

    private String canteen_query = "", location_query = "", store_type_query = "", 
			cuisine_query = "", halal_query = "", aircon_query="", distinct_query ="",
			query_key_str="*";
    
    private double lat=1.296469, lon=103.776373, radius = 2000;
    
    private String search, search_string, lat_str, lon_str, radius_str;
    
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
		throws ServletException, IOException    {
		String canteen = req.getParameter("canteen_name");
		String location = req.getParameter("location");
		String store_type = req.getParameter("store_type");
		String cuisine = req.getParameter("cuisine");
		String halal = req.getParameter("halal");
		String aircon = req.getParameter("aircon");
		String query_key = req.getParameter("query_key");
		String distinct = req.getParameter("distinct");
		lat_str = req.getParameter("lat");
		lon_str = req.getParameter("lon");
		radius_str = req.getParameter("radius");
		search = req.getParameter("search");
		search_string = req.getParameter("search_string");
		String output = req.getParameter("output");
		
		//Database db = DBFactory.loadDatabase("com.nuslivinglab.db.db");
		Database db = DBFactory.loadDatabase("com.nuslivinglab.db.db_pgsql");
		Statement st = db.getStatement();
		
		PrintWriter out = resp.getWriter();
		
		if (canteen != null) {
			canteen_query = " AND canteen_name LIKE '%" + canteen + "%'";
		}
		
		if (location != null) {
			location_query = " AND location LIKE '%" + location + "%'";
		} 
		
		if (store_type != null) {
			store_type_query = " AND store_type LIKE '%" + store_type + "%'";
		}
		
		if (cuisine != null) {
			cuisine_query = " AND cuisine LIKE '%" + cuisine + "%'";
		}
		
		if (halal != null) {
			halal_query = " AND halal ='" + halal + "'";
		}
		
		if (aircon != null) {
			aircon_query = " AND aircon ='" + aircon + "'"; 
		}
		
		if (distinct != null) {
			distinct_query = distinct; 
		}
		
		if (query_key != null) {
			query_key_str = query_key; 
		}
		
		if(search == null) {
			search="";
		}
		
		if(search_string == null) {
			search_string="";
		}
		
		//output in json format
		if(output!=null && output.equals("json")) {
			JsonArray jArray = new JsonArray();
			Gson gson = new GsonBuilder().serializeNulls().create();
			String query;
			
			query = query_process();
							
			try {
				ResultSet result = st.executeQuery(query);
				
				if (query_key_str.equals("*")) {
					
					if(search.equals("nearby")) { 
						
						while(result.next()) {
							
							JsonObject obj = new JsonObject();
							obj.addProperty("canteen_name", result.getString("canteen_name"));
							obj.addProperty("store_name", result.getString("store_name"));
							obj.addProperty("location", result.getString("location"));
							obj.addProperty("room_code", result.getString("room_code"));
							obj.addProperty("store_type", result.getString("store_type"));
							obj.addProperty("cuisine", result.getString("cuisine"));
							obj.addProperty("halal", result.getString("halal"));
							obj.addProperty("menu", result.getString("menu"));
							obj.addProperty("aircon", result.getString("aircon"));
							obj.addProperty("availability_weekday", result.getString("availability_weekday"));
							obj.addProperty("availability_weekend", result.getString("availability_weekend"));
							obj.addProperty("availability_vac_weekday", result.getString("availability_vac_weekday"));
							obj.addProperty("availability_vac_weekend", result.getString("availability_vac_weekend"));
							obj.addProperty("availability_pubhol", result.getString("availability_pubhol"));
							obj.addProperty("img_path", result.getString("img_path"));
							obj.addProperty("dist", result.getString(20));
							//obj.addProperty("cam_no", result.getString("cam_no"));
							jArray.add(obj);
						}
					}
					
					while(result.next()) {
						JsonObject obj = new JsonObject();
						obj.addProperty("canteen_name", result.getString("canteen_name"));
						obj.addProperty("store_name", result.getString("store_name"));
						obj.addProperty("location", result.getString("location"));
						obj.addProperty("room_code", result.getString("room_code"));
						obj.addProperty("store_type", result.getString("store_type"));
						obj.addProperty("cuisine", result.getString("cuisine"));
						obj.addProperty("halal", result.getString("halal"));
						obj.addProperty("menu", result.getString("menu"));
						obj.addProperty("aircon", result.getString("aircon"));
						obj.addProperty("availability_weekday", result.getString("availability_weekday"));
						obj.addProperty("availability_weekend", result.getString("availability_weekend"));
						obj.addProperty("availability_vac_weekday", result.getString("availability_vac_weekday"));
						obj.addProperty("availability_vac_weekend", result.getString("availability_vac_weekend"));
						obj.addProperty("availability_pubhol", result.getString("availability_pubhol"));
						obj.addProperty("img_path", result.getString("img_path"));
						//obj.addProperty("cam_no", result.getString("cam_no"));
						jArray.add(obj);
					}
					
				}  else if (!query_key_str.equals("*")) {
					
					while(result.next()) {
						JsonObject obj = new JsonObject();
						obj.addProperty(query_key_str, result.getString("query_key_str"));
						jArray.add(obj);
					}
				}
				
				int size =0;
				if (result != null) 
				{
				  result.beforeFirst();
				  result.last();
				  size = result.getRow();
				}
				
				if (size == 0) {
					out.println("Your Search did not match any food stalls!):");
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			resp.setContentType("application/json");
			out.print(gson.toJson(jArray));
			
			reset();
			
		} else {
			resp.setContentType("text/xml");
			out.println("<response>");
				
			String query;
			
			query = query_process();
					
			//out.println(query);
						
			try {
				ResultSet result = st.executeQuery(query);

				if (query_key_str.equals("*")) {
					
					if(search.equals("nearby")) { 
						
						while(result.next()) {
							out.println("<food_stall>");
							out.println("<canteen_name>" + process_string(result.getString("canteen_name")) + "</canteen_name>");
							out.println("<store_name>" + process_string(result.getString("store_name")) + "</store_name>");
							out.println("<location>" + process_string(result.getString("location")) + "</location>");
							out.println("<room_code>" + process_string(result.getString("room_code")) + "</room_code>");
							out.println("<store_type>" + process_string(result.getString("store_type")) + "</store_type>");
							out.println("<cuisine>" + process_string(result.getString("cuisine")) + "</cuisine>");
							out.println("<halal>" + process_string(result.getString("halal")) + "</halal>");
							out.println("<menu>" + process_string(result.getString("menu")) + "</menu>");
							out.println("<aircon>" + process_string(result.getString("aircon")) + "</aircon>");
							out.println("<availability_weekday>" + process_string(result.getString("availability_weekday")) + "</availability_weekday>");
							out.println("<availability_weekend>" + process_string(result.getString("availability_weekend")) + "</availability_weekend>");
							out.println("<availability_vac_weekday>" + process_string(result.getString("availability_vac_weekday")) + "</availability_vac_weekday>");
							out.println("<availability_vac_weekend>" + process_string(result.getString("availability_vac_weekend")) + "</availability_vac_weekend>");
							out.println("<availability_pubhol>" + process_string(result.getString("availability_pubhol")) + "</availability_pubhol>");
							out.println("<img_path>" + process_string(result.getString("img_path")) + "</img_path>");
							out.println("<dist>" + process_string(result.getString(20))  + " metres away from here!" +  "</dist>");
							//out.println("<cam_no>" + process_string(result.getString("cam_no")) + "</cam_no>");
							out.println("</food_stall>");
						}
					}
					
					while(result.next()) {
						out.println("<food_stall>");
						out.println("<canteen_name>" + process_string(result.getString("canteen_name")) + "</canteen_name>");
						out.println("<store_name>" + process_string(result.getString("store_name")) + "</store_name>");
						out.println("<location>" + process_string(result.getString("location")) + "</location>");
						out.println("<room_code>" + process_string(result.getString("room_code")) + "</room_code>");
						out.println("<store_type>" + process_string(result.getString("store_type")) + "</store_type>");
						out.println("<cuisine>" + process_string(result.getString("cuisine")) + "</cuisine>");
						out.println("<halal>" + process_string(result.getString("halal")) + "</halal>");
						out.println("<menu>" + process_string(result.getString("menu")) + "</menu>");
						out.println("<aircon>" + process_string(result.getString("aircon")) + "</aircon>");
						out.println("<availability_weekday>" + process_string(result.getString("availability_weekday")) + "</availability_weekday>");
						out.println("<availability_weekend>" + process_string(result.getString("availability_weekend")) + "</availability_weekend>");
						out.println("<availability_vac_weekday>" + process_string(result.getString("availability_vac_weekday")) + "</availability_vac_weekday>");
						out.println("<availability_vac_weekend>" + process_string(result.getString("availability_vac_weekend")) + "</availability_vac_weekend>");
						out.println("<availability_pubhol>" + process_string(result.getString("availability_pubhol")) + "</availability_pubhol>");
						out.println("<img_path>" + process_string(result.getString("img_path")) + "</img_path>");
						//out.println("<cam_no>" + process_string(result.getString("cam_no")) + "</cam_no>");
						out.println("</food_stall>");
					} 
				} else if (!query_key_str.equals("*")) {
					while(result.next()) {
						out.println("<food_list>");
						out.println("<" + query_key_str + ">" + process_string(result.getString(query_key_str)) + "</" + query_key_str + ">");
						out.println("</food_list>");
					}
				}
				
				int size =0;
				if (result != null) 
				{
				  result.beforeFirst();
				  result.last();
				  size = result.getRow();
				}
				
				out.println(size);
				
				if (size == 0) {
					out.println("<errormessage>Your Search did not match any food stalls!):</errormessage>");
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			out.println("</response>");
			
			reset();
		}
		db.close();
	}
	
	public static String process_string(String text) {
		//remove accents
		text = Normalizer.normalize(text, Normalizer.Form.NFD);
		
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	    String remove = pattern.matcher(text).replaceAll("");
		
		//replace '&' with '&amp;'
		text = remove.replace("&", "&amp;");
		
		return text;
	}
	
	private String query_process() {
		String query = null;
		
		Boolean where = false;
		
		if(lat_str != null)
			lat = Double.valueOf(lat_str);
		if(lon_str != null)
			lon = Double.valueOf(lon_str);
		if(radius_str != null)
			radius = Double.valueOf(radius_str);
		
		if((!canteen_query.equals("")) || (!location_query.equals("")) || (!store_type_query.equals("")) 
				|| (!cuisine_query.equals("")) || (!halal_query.equals("")) || (!aircon_query.equals("")) ){ 
				
				//check if distinct and check for query_word
				query = "SELECT " + distinct_query + " " + query_key_str + " from campus_food.campus_food WHERE menu='N' " + canteen_query 
							+ location_query + store_type_query + cuisine_query +halal_query + aircon_query 
							+ " ORDER BY canteen_name";
			
			if(search.equals("advanced")) {
				query = "SELECT "+ distinct_query + " " + query_key_str +" from campus_food.campus_food WHERE (canteen_name LIKE '%" + search_string + "%'" +
						" OR store_name LIKE '%" + search_string + "%' OR location LIKE '%" + search_string + "%' " +
						" OR room_code LIKE '%" + search_string + "%' OR store_type LIKE '%" + search_string + "%' " +
						" OR cuisine LIKE '%" + search_string + "%')" + "AND menu='N' " + canteen_query 
						+ location_query + store_type_query + cuisine_query + halal_query + aircon_query 
						+ " ORDER BY canteen_name";
			}
		} 
		else {
			
			if(search.equals("") || (search.equals("basic") && search_string.equals("")) || 
					(search.equals("nearby") && search_string.equals(""))) { 
				
				if(search.equals("basic") && search_string.equals(""))
					
					query = "SELECT "+ distinct_query + " " + query_key_str +" from campus_food.campus_food" 
								+ " ORDER BY canteen_name";
				
				else if (search.equals("nearby") && search_string.equals("")) {
					
					String distance = "round(st_distance(st_transform(ST_GeomFromText('POINT(" + lon + " " + lat + ")',4326),900913),st_transform(the_geom,900913)))";
					query = "SELECT " + distinct_query + " c.*, round(st_distance(st_transform(ST_GeomFromText('POINT(" + lon + " " + lat + ")',4326),900913),st_transform(the_geom,900913))) " + 
							"FROM campus_food.campus_food c, public.buildings b WHERE b.code = c.room_code AND " + distance + "< " + radius + " ORDER BY " + distance + ", c.store_name";
					
				} else
					query = "SELECT "+ distinct_query + " " + query_key_str +" from campus_food.campus_food";
				
			} else if((search.equals("basic") && (!search_string.equals("")))) {
				
				query = "SELECT "+ distinct_query + " " + query_key_str +" from campus_food.campus_food WHERE (canteen_name LIKE '%" + search_string + "%'" +
						" OR store_name LIKE '%" + search_string + "%' OR location LIKE '%" + search_string + "%' " +
						" OR room_code LIKE '%" + search_string + "%' OR store_type LIKE '%" + search_string + "%' " +
						" OR cuisine LIKE '%" + search_string + "%')"  
						+ " ORDER BY canteen_name";
				
			} else if (search.equals("nearby") && (!search_string.equals(""))) {
				
				String distance = "round(st_distance(st_transform(ST_GeomFromText('POINT(" + lon + " " + lat + ")',4326),900913),st_transform(the_geom,900913)))";
				query = "SELECT " + distinct_query + " c.*, round(st_distance(st_transform(ST_GeomFromText('POINT(" + lon + " " + lat + ")',4326),900913),st_transform(the_geom,900913))) " + 
						"FROM campus_food.campus_food c, public.buildings b WHERE b.code = c.room_code AND (" + distance + "< " + radius + ") AND " + 
								"(canteen_name LIKE '%" + search_string + "%'" +
								" OR store_name LIKE '%" + search_string + "%' OR location LIKE '%" + search_string + "%' " +
								" OR room_code LIKE '%" + search_string + "%' OR store_type LIKE '%" + search_string + "%' " +
								" OR cuisine LIKE '%" + search_string + "%')"  
								+ " ORDER BY " + distance + ", c.canteen_name";
				
				
			}
		}
	
		return query;
	}
	
	private void reset() {
		canteen_query = ""; 
		location_query = ""; 
		store_type_query = ""; 
		cuisine_query = ""; 
		halal_query = "";
		aircon_query="";
		distinct_query ="";
		query_key_str="*";
		search="";
		search_string="";
		lat=1.296469;
		lon=103.776373;
	}
}
