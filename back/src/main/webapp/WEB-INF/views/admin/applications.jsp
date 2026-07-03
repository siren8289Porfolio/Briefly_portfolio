<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.briefly.application.ApplicationDto" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Briefly - 신청 관리</title>
</head>
<body>
<h1>관리자 - 신청 상태 관리</h1>
<ul>
<%
    List<ApplicationDto> applications = (List<ApplicationDto>) request.getAttribute("applications");
    if (applications != null) {
        for (ApplicationDto app : applications) {
%>
    <li>
        #<%= app.getId() %> | 사용자 <%= app.getUserId() %> | 상품 <%= app.getFundId() %>
        | <%= app.getAmount() %>원 | <%= app.getStatus() %>
        <form method="post" action="${pageContext.request.contextPath}/admin/applications/status" style="display:inline">
            <input type="hidden" name="applicationId" value="<%= app.getId() %>">
            <select name="status">
                <option value="PENDING">PENDING</option>
                <option value="APPROVED">APPROVED</option>
                <option value="REJECTED">REJECTED</option>
                <option value="CANCELED">CANCELED</option>
            </select>
            <button type="submit">변경</button>
        </form>
    </li>
<%
        }
    }
%>
</ul>
<p><a href="${pageContext.request.contextPath}/admin/funds">상품 관리</a></p>
</body>
</html>
