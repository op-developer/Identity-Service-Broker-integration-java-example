<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE HTML>
<html>
    <head>
        <meta charset="UTF-8" />
        <title>Java Test Service Provider</title>
        <link href="https://fonts.googleapis.com/css?family=Oswald|PT+Sans" rel="stylesheet"/>
        <link href="/resources/css/style.css" type="text/css" rel="stylesheet">
    </head>
    <body>
        <div class="container">

		<header>
		  <h1>Demo Service Provider</h1>
		</header>
		
		<p>The information received depends on the scope of identification request and on what attributes are available. Do note that not all sources of information have given name and family name available as separate attributes.</p>
		<h2>Identification information</h2>

		<table>
		    <tr>
		        <th>Name</th>
		        <td>${identity.name}</td>
		    </tr>
		    <tr>
		        <th>Identity code</th>
		        <td>${identity.ssn}</td>
		    </tr>
		    <tr>
		        <th>Time of authentication</th>
		        <td>${timenow}</td>
		    </tr>
		</table>
		
		<h3>Raw data</h3>
		<pre>${identity.identityRawData} 
		</pre>  

        <p><a href="/${backurlprefix}">Try again</a></p>

        </div>
    </body>
</html>

