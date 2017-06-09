<%@ page language="java" 
import="java.io.*"
import="java.sql.*"
import="java.util.*"
%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Untitled Document</title>
<style type="text/css">
</style>
</head>

<body>

 <form action="searchservlet.do" enctype="application/x-www-form-urlencoded">

 <p> <input name = "username" type="text"  id="username" size="170" align="middle"/> </p>

 <p> <input type="submit" name="submit" id="submit" value="Go" align="middle"/> </p>

<hr />

<%String uname = "";
		/*String noinput = session.getAttribute("noinput").toString();
		if(noinput.equals("noinput")){ */
			%>
			   <strong> <%=  " Please Enter sth to Search :)" %></strong>  <%
		// } 
		TreeMap amap = (TreeMap)request.getAttribute("user");
		Iterator iterator = amap.keySet().iterator();  
   
while (iterator.hasNext()) {  
  String key = iterator.next().toString();  
   String value = amap.get(key).toString();  
   %>
   <strong> <%=  " "+value+ " " +key %></strong>  <%
}  %>

 </form>

 


<div id="footer">
<p align="center">&copy; 2011. All Rights Reserved.</p>
</div>
</body>
</html>

 


 

 