package com.financialeducation.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try (Connection con = com.financialeducation.model.Conexao.getConnection()) {
            PreparedStatement sl = con.prepareStatement("SELECT * FROM users WHERE username=?");
            sl.setString(1, username);
            ResultSet rs = sl.executeQuery();
            
            if (!rs.next()) {
	            PreparedStatement ps = con.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
	            ps.setString(1, username);
	            ps.setString(2, BCrypt.hashpw(password, BCrypt.gensalt(12)));
	            ps.executeUpdate();
	            
	            response.sendRedirect("index.jsp?register=success");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("register.jsp?error=1");
        }
    }
}
