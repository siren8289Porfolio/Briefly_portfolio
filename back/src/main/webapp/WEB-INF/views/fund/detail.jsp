<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.briefly.fund.FundDto" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Briefly - 상품 상세</title>
</head>
<body>
<%
    FundDto fund = (FundDto) request.getAttribute("fund");
    Boolean watched = (Boolean) request.getAttribute("watched");
%>
<h1><%= fund.getName() %></h1>
<% if (request.getAttribute("error") != null) { %>
<p style="color:red">${error}</p>
<% } %>
<p><%= fund.getDescription() %></p>
<p>위험등급: <%= fund.getRiskGrade() %> | 예상 수익률: <%= fund.getExpectedReturn() %>%</p>

<% if (session.getAttribute("loginUser") != null) { %>
<form method="post" action="${pageContext.request.contextPath}/watchlist/toggle">
    <input type="hidden" name="fundId" value="<%= fund.getId() %>">
    <button type="submit"><%= Boolean.TRUE.equals(watched) ? "관심 해제" : "관심 등록" %></button>
</form>
<form method="post" action="${pageContext.request.contextPath}/applications">
    <input type="hidden" name="fundId" value="<%= fund.getId() %>">
    <label>신청 금액 <input type="number" name="amount" min="1" step="1000" required></label>
    <button type="submit">모의가입 신청</button>
</form>
<% } else { %>
<p><a href="${pageContext.request.contextPath}/login">로그인</a> 후 관심등록·모의가입이 가능합니다.</p>
<% } %>

<p>
    <a href="${pageContext.request.contextPath}/reports?fundId=<%= fund.getId() %>">운용 브리프</a> |
    <a href="${pageContext.request.contextPath}/funds">목록으로</a>
</p>
</body>
</html>
