package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Entity.ChatUser;

public class LoginServlet extends ChatServlet {
    private static final long serialVersionUID = 1L;
    // Длительность сессии, в секундах
    private int sessionTimeout = 10*60;

    public void init() throws ServletException {
        super.init();
// Прочитать из конфигурации значение параметра SESSION_TIMEOUT
        String value =
                getServletConfig().getInitParameter("SESSION_TIMEOUT");
// Если он задан, переопределить длительность сессии по умолчанию
        if (value!=null) {
            sessionTimeout = Integer.parseInt(value);
        }
    }
    // Метод будет вызван при обращении к сервлету HTTP-методом GET
    // т.е. когда пользователь просто открывает адрес в браузере
    protected void doGet(HttpServletRequest request, HttpServletResponse
            response) throws ServletException, IOException {
// Проверить, есть ли уже в сессии заданное имя пользователя?
        String name = (String)request.getSession().getAttribute("name");
// Извлечь из сессии сведения о предыдущей ошибке (возможной)
        String errorMessage =
                (String)request.getSession().getAttribute("error");
// Идентификатор предыдущей сессии изначально пуст
        String previousSessionId = null;

        if (name!=null && !"".equals(name)) {
            errorMessage = processLogonAttempt(name, request, response);
        }
// Пользователю необходимо ввести имя. Показать форму
// Задать кодировку HTTP-ответа
        response.setCharacterEncoding("utf8");
// Получить поток вывода для HTTP-ответа
        PrintWriter pw = response.getWriter();
        pw.println("<html><head><title>Gay-website!</title><meta httpequiv='Content-Type' content='text/html; charset=utf-8'/></head>");
// Если возникла ошибка - сообщить о ней
        if (errorMessage!=null) {
            pw.println("<p><font color='red'>" + errorMessage +
                    "</font></p>");
        }
// Сбросить сообщение об ошибке в сессии
        request.getSession().setAttribute("error", null);
    }
    // Метод будет вызван при обращении к сервлету HTTP-методом POST
// т.е. когда пользователь отправляет сервлету данные
    protected void doPost(HttpServletRequest request, HttpServletResponse
            response) throws ServletException, IOException {
// Задать кодировку HTTP-запроса - очень важно!
// Иначе вместо символов будет абракадабра
        request.setCharacterEncoding("UTF-8");
// Извлечь из HTTP-запроса значение параметра 'name'
        String name = (String)request.getParameter("name");
// Полагаем, что изначально ошибок нет
        String errorMessage = null;
        if (name==null || "".equals(name)) {
// Пустое имя недопустимо - сообщить об ошибке
            errorMessage = "You get me mad now!!";
        } else {
// Если ия не пустое, то попытаться обработать запрос
            errorMessage = processLogonAttempt(name, request, response);
        }
    }
    // Возвращает текстовое описание возникшей ошибки или null
    String processLogonAttempt(String name, HttpServletRequest request,
                               HttpServletResponse response) throws IOException {
// Определить идентификатор Java-сессии пользователя
        String sessionId = request.getSession().getId();
// Извлечь из списка объект, связанный с этим именем
        ChatUser aUser = activeUsers.get(name);
        if (aUser==null) {
// Если оно свободно, то добавить
// нового пользователя в список активных
            aUser = new ChatUser(name,
                    Calendar.getInstance().getTimeInMillis(), sessionId);
// Так как одновременно выполняются запросы
// от множества пользователей
// то необходима синхронизация на ресурсе
            synchronized (activeUsers) {
                activeUsers.put(aUser.getName(), aUser);
            }
        } else {
// Сохранѐнное в сессии имя уже закреплено за кем-то другим.
// Извиниться, отказать и попросить ввести другое имя
            return "Sorry, but <strong>" + name + "</strong> allready enter our gay-bar!";
        }
    }
}