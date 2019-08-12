<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body onLoad="document.oidcForm.submit()">
    <form action="${endpointUrl}" method="get" name="oidcForm">
    <input type="hidden" name="request" value="${request}" />
    </form>
</body>
</html>
