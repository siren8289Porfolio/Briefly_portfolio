<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.briefly.fund.FundDto" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Briefly - 관리자 상품</title>
</head>
<body>
<h1>관리자 - 상품 관리</h1>
<% if (request.getAttribute("error") != null) { %>
<p style="color:red">${error}</p>
<% } %>
<ul>
<%
    List<FundDto> funds = (List<FundDto>) request.getAttribute("funds");
    if (funds != null) {
        for (FundDto fund : funds) {
%>
    <li><%= fund.getName() %> | 등급 <%= fund.getRiskGrade() %> | <%= fund.getStatus() %></li>
<%
        }
    }
%>
</ul>
<h2>상품 등록</h2>
<form method="post" action="${pageContext.request.contextPath}/admin/funds">
    <input name="name" placeholder="상품명" required><br>
    <textarea name="description" placeholder="설명"></textarea><br>
    <input name="riskGrade" type="number" min="1" max="5" placeholder="위험등급" required><br>
    <input name="expectedReturn" type="number" step="0.01" placeholder="예상수익률" required><br>
    <select name="status"><option value="ACTIVE">ACTIVE</option><option value="INACTIVE">INACTIVE</option></select><br>
    <button type="submit">등록</button>
</form>
<p><a href="${pageContext.request.contextPath}/admin/applications/status">신청 관리</a></p>
</body>
</html>
