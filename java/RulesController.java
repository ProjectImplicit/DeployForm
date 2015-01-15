package org.implicit.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.implicit.Implicit;
import org.implicit.random.Condition;
import org.json.JSONException;
import org.json.JSONObject;
import org.uva.dao.oracle.TaskDAO;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
/*
import org.jsoup.*;
import org.jsoup.helper.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
*/

public class RulesController extends HttpServlet{
	
	private static Category cat = Logger.getLogger(TaskDAO.class);
	String msg =new String("1");
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException { 
		try {
						
			processRequest(request,response);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	} 
	
	
	
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException{
		

			msg="1";
			doGet(request,response);
			
		
		
		
		  		
	}

		
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ParserConfigurationException{
		
		
		
		try {
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("text/html");
			String str;
			String key = null;
			String val= null;
			Object o = null;
			StringBuilder sb = new StringBuilder();
		    BufferedReader br = request.getReader();
		     
		    cat.debug("starting save file");		    
		    while( (str = br.readLine()) != null ){
		        sb.append(str);
		    }    
			try {
				JSONObject jObj = new JSONObject(sb.toString());
				Iterator it = jObj.keys(); //gets all the keys
				HashMap map = new HashMap(); 
				while(it.hasNext())
				{
					key = (String)it.next();
					val  = (String) jObj.get(key).toString();
					map.put(key,val);
				}
				writeXML(map,request);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				msg = e1.getMessage();
				e1.printStackTrace();
				
			}
					
						
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			msg =e.getMessage();
			if (cat.isDebugEnabled())
				cat.debug(e.getMessage());
			e.printStackTrace();
		}catch (IOException e){
			msg =e.getMessage();
			if (cat.isDebugEnabled())
				cat.debug(e.getMessage());
			e.printStackTrace();
			
		}
		
		PrintWriter out = response.getWriter();
		out.println(msg);
		out.close();
		
	
	}
	
	private String sendPost(String xml, String path,String ruleName,String submit,String overwrite,String realPath) throws Exception {
		 
		String url = "https://dw2.psyc.virginia.edu/implicit/rules";
		String USER_AGENT = "Mozilla/5.0";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		//HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		
		
        String datasubmit= "false";
        String datarealPath = "";
        String dataxml = xml;
        String dataoverwrite = overwrite;
        
        JSONObject jasonobj=new JSONObject();
        jasonobj.put("path",path);
        jasonobj.put("FileName",ruleName);
        jasonobj.put("submit",submit);
        jasonobj.put("xml",xml);
        jasonobj.put("overwrite",overwrite);
        jasonobj.put("realPath",realPath);
        
        
		String urlParameters = jasonobj.toString();
 
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
 
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
		
 
	}
	
	public void processHTML(String filepath,String tr) throws IOException{
		
			
		 FileWriter out;
		 out = new FileWriter(filepath, true);
         out.write(tr);
         out.close();
		
		
	}
	
	protected void writeXML(HashMap dataMap,HttpServletRequest request) throws ParserConfigurationException, IOException{
		
		String path = (String) dataMap.get("path");
		String fileName = (String) dataMap.get("FileName");
		String xml = (String) dataMap.get("xml");
		String submit = (String) dataMap.get("submit");
		String real = (String) dataMap.get("realPath");
		String overwrite = (String) dataMap.get("overwrite");
		String ending = new String(".xml");
		String pathlocale = org.uva.Implicit.REALPATH;
		String filePath = new String("");
		
		
		String cmd = (String) dataMap.get("cmd");
		if (cmd==null) cmd="not_set";
		
		String res = null;
		if (cmd.equals("post_to_old_dev2")){
		
			try {
				 msg =sendPost(xml, path,fileName,submit,overwrite,real);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{
			if (overwrite==null ) overwrite = "false";
			if (submit.equals("true")){
				ending = ".html";
				filePath = (pathlocale+"/"+path);
				processHTML(filePath,xml);
				
			}else{
				filePath = (pathlocale+"/"+path+"/"+fileName+ending);
				if (!real.equals("")){
					filePath=real+"/"+fileName+ending;
					
				}
				
				File file = new File(filePath);
				//File file = new File(path);
				
				 System.out.println(file.getAbsolutePath());
				// if file doesnt exists, then create it
				if (!file.exists()) {
					
					file.createNewFile();
					boolean bval = file.setExecutable(true,false);//added 09/03
					file.setReadable(true,false);////added 09/03
					FileWriter fw = new FileWriter(file.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(xml);
					bw.close();
				}else{
					if (overwrite.equals("true")){
						file.createNewFile();
						boolean bval = file.setExecutable(true,false);//added 09/03
						file.setReadable(true,false);////added 09/03
						FileWriter fw = new FileWriter(file.getAbsoluteFile());
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(xml);
						bw.close();
						
						
					}else{
						msg = "21";//code for file exist
						
					}
					
				}

			}
	
		}
		
	
		
	}


}
