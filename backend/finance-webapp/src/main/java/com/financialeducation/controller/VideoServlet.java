package com.financialeducation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.financialeducation.model.Video;
import com.financialeducation.model.VideoDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/videos")
public class VideoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private VideoDAO videoDAO;

    public void init() {
    	try {
    		VideoDAO videoDAO = new VideoDAO();
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Video> videos = videoDAO.listarVideos();

        // Definir o primeiro vídeo como padrão
        Video videoAtual = videos.isEmpty() ? null : videos.get(0);

        request.setAttribute("videos", videos);
        request.setAttribute("videoAtual", videoAtual);

        request.getRequestDispatcher("view/videos.jsp").forward(request, response);
    }
}
