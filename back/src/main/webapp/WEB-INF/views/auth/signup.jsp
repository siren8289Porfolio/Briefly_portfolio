<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Briefly - 회원가입</title>
</head>
<body>
<h1>회원가입</h1>
<% if (request.getAttribute("error") != null) { %>
<p style="color:red">${error}</p>
<% } %>
<form method="post" action="${pageContext.request.contextPath}/signup">
    <label>이메일 <input type="email" name="email" required></label><br>
    <label>비밀번호 <input type="password" name="password" required></label><br>
    <label>이름 <input type="text" name="name" required></label><br>
    <button type="submit">가입</button>
</form>
<p><a href="${pageContext.request.contextPath}/login">로그인</a></p>
</body>
</html>
