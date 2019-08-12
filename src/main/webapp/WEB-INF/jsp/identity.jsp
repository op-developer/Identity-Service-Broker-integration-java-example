<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body style="color: green; font-weight:bold;">
<p>Hello ${identity.name}!</p>
<p>Your personal identity code (HETU, SATU or Y-tunnus) is ${identity.ssn}.</p>
<p><a href="/">Try again</a></p>
</body>
</html>
