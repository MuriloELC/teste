<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<html>
<head>
    <title>Logado</title>
</head>
<body>
    <h2>Bem-vindo, <%= session.getAttribute("username") %>!</h2>
    <p>Você está logado.</p>
    <a href="index.jsp">Sair</a>
</body>
</html>
