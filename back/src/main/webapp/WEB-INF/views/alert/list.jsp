<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.briefly.alert.dto.AlertDto" %>
<%@ page import="com.briefly.ai.dto.AiExplanationDto" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Briefly - 위험 알림</title>
</head>
<body>
<h1>위험 알림</h1>
<%
    AiExplanationDto aiExplanation = (AiExplanationDto) request.getAttribute("aiExplanation");
    if (aiExplanation != null) {
%>
<section>
    <h2>최신 알림 쉬운 설명 (보조)</h2>
    <p><%= aiExplanation.getExplanation() %></p>
    <p><small><%= aiExplanation.getDisclaimer() %></small></p>
</section>
<%
    }
%>
<ul>
<%
    List<AlertDto> alerts = (List<AlertDto>) request.getAttribute("alerts");
    if (alerts == null || alerts.isEmpty()) {
%>
    <li>관심상품 기준 알림이 없습니다.</li>
<%
    } else {
        for (AlertDto alert : alerts) {
%>
    <li>
        <strong><%= alert.getTitle() %></strong> (상품 #<%= alert.getFundId() %>)
        <p><%= alert.getMessage() %></p>
        <p>등급 변경: <%= alert.getPreviousGrade() %> → <%= alert.getNewGrade() %></p>
    </li>
<%
        }
    }
%>
</ul>
<p><a href="${pageContext.request.contextPath}/funds">상품 목록</a></p>
</body>
</html>
