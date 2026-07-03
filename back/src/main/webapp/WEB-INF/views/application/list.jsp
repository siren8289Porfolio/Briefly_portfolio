<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.briefly.application.ApplicationDto" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Briefly - 신청 내역</title>
</head>
<body>
<h1>모의가입 신청 내역</h1>
<ul>
<%
    List<ApplicationDto> applications = (List<ApplicationDto>) request.getAttribute("applications");
    if (applications == null || applications.isEmpty()) {
%>
    <li>신청 내역이 없습니다.</li>
<%
    } else {
        for (ApplicationDto app : applications) {
%>
    <li>상품 #<%= app.getFundId() %> | <%= app.getAmount() %>원 | <%= app.getStatus() %></li>
<%
        }
    }
%>
</ul>
<p><a href="${pageContext.request.contextPath}/funds">상품 목록</a></p>
</body>
</html>
