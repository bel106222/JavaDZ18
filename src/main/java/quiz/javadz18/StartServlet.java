package quiz.javadz18;

import java.io.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "startServlet", value = "/start-servlet")
public class StartServlet extends HttpServlet {
    private String message;
    // Создаём список доступных категорий
    private String[] CATEGORIES = {"История", "Спорт", "Поп-культура", "Космос"};

    public void init() {
        message = "Выбор категории вопросов:";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Выбор категории вопросов</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Выберите категорию вопросов</h1>");
        out.println("<form method='post' action=''>");  // отправка на этот же URL
        for (String category : CATEGORIES) {
            out.println("<label>");
            out.println("<input type='radio' name='category' value='" + category + "'> " + category);
            out.println("</label><br>");
        }
        out.println("<br><input type='submit' value='Выбрать'>");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String selectedCategory = request.getParameter("category");
        if (selectedCategory != null && !selectedCategory.isEmpty()) {
            // Редирект на другой сервлет (QuestionsServlet) с передачей категории в GET-параметре
            // Кодируем параметр для безопасной вставки в URL
            String encodedCategory = URLEncoder.encode(selectedCategory, StandardCharsets.UTF_8);
            String redirectUrl = request.getContextPath() + "/questions-servlet?category=" + encodedCategory;
            response.sendRedirect(redirectUrl);
        } else {
            // Если категория не выбрана – возвращаем на форму с сообщением
            PrintWriter out = response.getWriter();
            response.setContentType("text/html;charset=UTF-8");
            out.println("<html><head><meta charset='UTF-8'><title>Ошибка</title></head><body>");
            out.println("<h3>Ошибка: категория не выбрана.</h3>");
            out.println("<p><a href='" + request.getContextPath() + "/'>Вернуться назад</a></p>");
            out.println("</body></html>");
        }
    }

    public void destroy() {
    }
}