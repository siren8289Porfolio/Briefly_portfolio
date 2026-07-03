<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.briefly.fund.FundDto" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Briefly - 투자상품</title>
</head>
<body>
<h1>투자상품 목록</h1>
<ul>
<%
    List<FundDto> funds = (List<FundDto>) request.getAttribute("funds");
    if (funds != null) {
        for (FundDto fund : funds) {
%>
    <li>
        <a href="${pageContext.request.contextPath}/funds/detail?id=<%= fund.getId() %>">
            <%= fund.getName() %> (위험등급 <%= fund.getRiskGrade() %>, 예상수익 <%= fund.getExpectedReturn() %>%)
        </a>
    </li>
<%
        }
    }
%>
</ul>
<p>
    <a href="${pageContext.request.contextPath}/applications">내 신청 내역</a> |
    <a href="${pageContext.request.contextPath}/alerts">위험 알림</a>
</p>
</body>
</html>
