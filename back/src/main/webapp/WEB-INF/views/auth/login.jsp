<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Briefly - 로그인</title>
</head>
<body>
<h1>로그인</h1>
<% if (request.getAttribute("error") != null) { %>
<p style="color:red">${error}</p>
<% } %>
<form method="post" action="${pageContext.request.contextPath}/login">
    <label>이메일 <input type="email" name="email" required></label><br>
    <label>비밀번호 <input type="password" name="password" required></label><br>
    <button type="submit">로그인</button>
</form>
<p><a href="${pageContext.request.contextPath}/signup">회원가입</a></p>
</body>
</html>
