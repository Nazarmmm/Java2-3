package Servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Entity.ChatUser;
//import bsu.rfe.java.teacher.entity.ChatUser;
public class LogoutServlet extends ChatServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse
            response) throws ServletException, IOException {
        String name = (String) request.getSession().getAttribute("name");
// Если в сессии имеется имя пользователя...
        if (name!=null) {
// Получить объект, описывающий пользователя с таким именем
            ChatUser aUser = activeUsers.get(name);
// Если идентификатор сессии пользователя, вошедшего под
// этим именем, совпадает с идентификатором сессии
// пользователя, пытающегося выйти из чата
// (т.е. выходит тот же, кто и входил)
            if (aUser.getSessionId().equals((String)
                    request.getSession().getId())) {
// Удалить пользователя из списка активных
// Т.к. запросы обрабатываются одновременно,
// нужна синхронизация
                synchronized (activeUsers) {
                    activeUsers.remove(name);
                }
// Сбросить имя пользователя в сессии
                request.getSession().setAttribute("name", null);
// Сбросить ID сессии в cookie
                response.addCookie(new Cookie("sessionId", null));
            } else {
                response.sendRedirect(response.encodeRedirectURL("/mychat/"));
            }
        }
    }
}
