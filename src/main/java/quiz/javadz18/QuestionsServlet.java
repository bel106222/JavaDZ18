package quiz.javadz18;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

/**
 * Основной сервлет викторины.
 * Обрабатывает показ вопросов по одному с таймером 30 секунд.
 * Поддерживает вопросы с вариантами ответов и текстовым вводом.
 * Состояние викторины хранится в HTTP-сессии.
 */
@WebServlet(name = "questionsServlet", value = "/questions-servlet")
public class QuestionsServlet extends HttpServlet {

    // Статический список всех вопросов, заполняется один раз при загрузке класса
    private static final List<Question> ALL_QUESTIONS = new ArrayList<>();

    static {
        // ============ История (10 обычных + 2 текстовых) ============
        ALL_QUESTIONS.add(new Question("История", "В каком году началась Вторая мировая война?", "1937", "1939", "1941", 'B'));
        ALL_QUESTIONS.add(new Question("История", "Кто был первым императором Российской империи?", "Иван Грозный", "Пётр I", "Екатерина II", 'B'));
        ALL_QUESTIONS.add(new Question("История", "Какое древнее сооружение считается одним из Семи чудес света и находится в Гизе?", "Храм Артемиды", "Висячие сады Семирамиды", "Пирамида Хеопса", 'C'));
        ALL_QUESTIONS.add(new Question("История", "Кто написал «Капитал» — фундаментальный труд по политической экономии?", "Фридрих Энгельс", "Карл Маркс", "Владимир Ленин", 'B'));
        ALL_QUESTIONS.add(new Question("История", "Как назывался первый в мире пилотируемый космический корабль, на котором Юрий Гагарин совершил полёт?", "Союз", "Восход", "Восток-1", 'C'));
        ALL_QUESTIONS.add(new Question("История", "В каком веке произошла Куликовская битва?", "XII век", "XIV век", "XVI век", 'B'));
        ALL_QUESTIONS.add(new Question("История", "Какая цивилизация известна своей письменностью в виде клинописи и висячими садами?", "Древний Египет", "Древняя Греция", "Месопотамия (Вавилон)", 'C'));
        ALL_QUESTIONS.add(new Question("История", "Кто был лидером большевиков в 1917 году?", "Лев Троцкий", "Иосиф Сталин", "Владимир Ленин", 'C'));
        ALL_QUESTIONS.add(new Question("История", "Какое событие положило начало Великой французской революции?", "Взятие Бастилии", "Казнь Людовика XVI", "Поход на Версаль", 'A'));
        ALL_QUESTIONS.add(new Question("История", "Как назывался мирный договор, завершивший Первую мировую войну для Германии?", "Версальский договор", "Трианонский договор", "Брестский мир", 'A'));
        // Текстовые вопросы История
        ALL_QUESTIONS.add(new Question("История", "В каком году распался Советский Союз?", "1991"));
        ALL_QUESTIONS.add(new Question("История", "Как звали жену Петра I, ставшую императрицей Екатериной I?", "Марта Скавронская"));

        // ============ Спорт (10 обычных + 2 текстовых) ============
        ALL_QUESTIONS.add(new Question("Спорт", "Сколько игроков в одной команде на поле в классическом футболе (включая вратаря)?", "10", "11", "12", 'B'));
        ALL_QUESTIONS.add(new Question("Спорт", "Кто из этих спортсменов удерживает рекорд по количеству золотых олимпийских медалей в истории?", "Майкл Фелпс", "Усэйн Болт", "Лариса Латынина", 'A'));
        ALL_QUESTIONS.add(new Question("Спорт", "Какой теннисный турнир считается самым престижным и проводится на травяных кортах в Лондоне?", "Открытый чемпионат США", "Уимблдон", "Roland Garros", 'B'));
        ALL_QUESTIONS.add(new Question("Спорт", "В каком виде спорта используется снаряд «штанга» и упражнения «рывок» и «толчок»?", "Пауэрлифтинг", "Тяжёлая атлетика", "Бодибилдинг", 'B'));
        ALL_QUESTIONS.add(new Question("Спорт", "Как называется высшая лига по баскетболу в Северной Америке?", "NBA", "NCAA", "FIBA", 'A'));
        ALL_QUESTIONS.add(new Question("Спорт", "Какой футбольный клуб выиграл Лигу чемпионов УЕФА больше всех раз (на 2024 год)?", "Бавария", "Милан", "Реал Мадрид", 'C'));
        ALL_QUESTIONS.add(new Question("Спорт", "Что означает «фальстарт» в лёгкой атлетике?", "Преждевременное начало движения до выстрела стартёра", "Финиш не в своей дорожке", "Падение после старта", 'A'));
        ALL_QUESTIONS.add(new Question("Спорт", "Кто является самым титулованным гонщиком «Формулы-1» по числу чемпионских титулов?", "Айртон Сенна", "Михаэль Шумахер и Льюис Хэмилтон (по 7)", "Себастьян Феттель", 'B'));
        ALL_QUESTIONS.add(new Question("Спорт", "В каком виде спорта используется клюшка, шайба и ворота с сеткой?", "Хоккей на траве", "Хоккей с шайбой", "Кёрлинг", 'B'));
        ALL_QUESTIONS.add(new Question("Спорт", "Какое максимальное количество очков можно набрать за один бросок в дартсе?", "180", "60", "100", 'B'));
        // Текстовые вопросы Спорт
        ALL_QUESTIONS.add(new Question("Спорт", "Как называется вид спорта, сочетающий лыжную гонку и стрельбу?", "биатлон"));
        ALL_QUESTIONS.add(new Question("Спорт", "Сколько игроков в команде по водному поло в бассейне одновременно?", "7"));

        // ============ Поп-культура (10 обычных + 2 текстовых) ============
        ALL_QUESTIONS.add(new Question("Поп-культура", "Какой певец известен как «Король поп-музыки»?", "Prince", "Michael Jackson", "Freddie Mercury", 'B'));
        ALL_QUESTIONS.add(new Question("Поп-культура", "Кто сыграл роль Железного человека в киновселенной Marvel?", "Крис Эванс", "Роберт Дауни мл.", "Крис Хемсворт", 'B'));
        ALL_QUESTIONS.add(new Question("Поп-культура", "Как называется фэнтези-серия книг Дж. К. Роулинг о юном волшебнике?", "Властелин колец", "Хроники Нарнии", "Гарри Поттер", 'C'));
        ALL_QUESTIONS.add(new Question("Поп-культура", "Какой фильм режиссёра Джеймса Кэмерона стал первым в истории, собравшим более $2 млрд в мировом прокате?", "Титаник", "Аватар", "Терминатор 2", 'A'));
        ALL_QUESTIONS.add(new Question("Поп-культура", "Кто из этих музыкантов был участником группы The Beatles?", "Элтон Джон", "Пол Маккартни", "Дэвид Боуи", 'B'));
        ALL_QUESTIONS.add(new Question("Поп-культура", "Как зовут персонажа «Звёздных войн», который произносит фразу «Да пребудет с тобой Сила»?", "Дарт Вейдер", "Оби-Ван Кеноби", "Хан Соло", 'B'));
        ALL_QUESTIONS.add(new Question("Поп-культура", "Кто исполнил заглавную песню для фильма «Титаник» – «My Heart Will Go On»?", "Мэрайя Кэри", "Селин Дион", "Уитни Хьюстон", 'B'));
        ALL_QUESTIONS.add(new Question("Поп-культура", "Какой мультсериал создал Стивен Хилленберг про морскую губку?", "Губка Боб Квадратные Штаны", "Лагерь Лазер", "Рик и Морти", 'A'));
        ALL_QUESTIONS.add(new Question("Поп-культура", "Какой видеоигрой управляет персонаж по имени Марио?", "Sonic the Hedgehog", "Super Mario Bros.", "The Legend of Zelda", 'B'));
        ALL_QUESTIONS.add(new Question("Поп-культура", "Кто исполнил роль Джокера в фильме «Тёмный рыцарь» (2008)?", "Джаред Лето", "Хоакин Феникс", "Хит Леджер", 'C'));
        // Текстовые вопросы Поп-культура
        ALL_QUESTIONS.add(new Question("Поп-культура", "Назовите группу, выпустившую альбом 'The Dark Side of the Moon'.", "Pink Floyd"));
        ALL_QUESTIONS.add(new Question("Поп-культура", "Кто написал роман '1984'?", "Джордж Оруэлл"));

        // ============ Космос (10 обычных + 2 текстовых) ============
        ALL_QUESTIONS.add(new Question("Космос", "Какая планета Солнечной системы самая большая по диаметру?", "Сатурн", "Юпитер", "Нептун", 'B'));
        ALL_QUESTIONS.add(new Question("Космос", "Как называется галактика, в которой находится Земля?", "Туманность Андромеды", "Млечный Путь", "Большое Магелланово Облако", 'B'));
        ALL_QUESTIONS.add(new Question("Космос", "Кто был первым человеком, ступившим на Луну?", "Юрий Гагарин", "Нил Армстронг", "Базз Олдрин", 'B'));
        ALL_QUESTIONS.add(new Question("Космос", "Что такое «чёрная дыра» простыми словами?", "Область с очень высокой плотностью и гравитацией, которая не выпускает свет", "Звезда, взорвавшаяся сверхновой", "Пустота между галактиками", 'A'));
        ALL_QUESTIONS.add(new Question("Космос", "Сколько планет в Солнечной системе?", "8", "9", "10", 'A'));
        ALL_QUESTIONS.add(new Question("Космос", "Как называется ближайшая к Солнцу планета?", "Венера", "Меркурий", "Марс", 'B'));
        ALL_QUESTIONS.add(new Question("Космос", "Какое космическое тело потеряло статус планеты в 2006 году?", "Плутон", "Эрида", "Церера", 'A'));
        ALL_QUESTIONS.add(new Question("Космос", "Что означает «сверхновая»?", "Новая планета", "Взрыв звезды в конце её жизни с огромным выделением энергии", "Рождение чёрной дыры без взрыва", 'B'));
        ALL_QUESTIONS.add(new Question("Космос", "Как называется российский (советский) многоразовый космический корабль, похожий на американский шаттл?", "Буран", "Союз", "Прогресс", 'A'));
        ALL_QUESTIONS.add(new Question("Космос", "На какой планете идут «алмазные дожди»?", "Юпитер", "Сатурн", "Нептун", 'C'));
        // Текстовые вопросы Космос
        ALL_QUESTIONS.add(new Question("Космос", "Как называется самая высокая гора в Солнечной системе, находящаяся на Марсе?", "Олимп"));
        ALL_QUESTIONS.add(new Question("Космос", "Назовите фамилию первого космонавта, вышедшего в открытый космос.", "Леонов"));
    }

    /**
     * Обрабатывает GET-запросы.
     * Если пришёл параметр category – начинается новая викторина (сохраняется в сессию).
     * Иначе продолжается текущая: показывается очередной вопрос или результаты.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String category = request.getParameter("category");
        HttpSession session = request.getSession(); // получаем или создаём сессию

        // --- Ветка 1: Начало новой викторины (пришёл параметр category) ---
        if (category != null && !category.isEmpty()) {
            // Фильтруем все вопросы по выбранной категории
            List<Question> categoryQuestions = ALL_QUESTIONS.stream()
                    .filter(q -> q.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());

            // Если категория не найдена – выводим сообщение об ошибке
            if (categoryQuestions.isEmpty()) {
                response.setContentType("text/html;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.println("<html><body><h1>Вопросы категории \"" + category + "\" не найдены.</h1>");
                out.println("<a href='" + request.getContextPath() + "/'>На главную</a></body></html>");
                return;
            }

            // Сохраняем состояние новой викторины в сессии
            session.setAttribute("quizQuestions", categoryQuestions);         // список вопросов
            session.setAttribute("currentIndex", 0);                         // начинаем с первого вопроса
            session.setAttribute("results", new boolean[categoryQuestions.size()]); // массив результатов
            session.setAttribute("category", category);                      // название категории

            // Перенаправляем на этот же сервлет без параметров, чтобы показать первый вопрос
            // (избегаем зацикливания при обновлении страницы)
            response.sendRedirect(request.getContextPath() + "/questions-servlet");
            return;
        }

        // --- Ветка 2: Продолжение существующей викторины ---
        List<Question> quizQuestions = (List<Question>) session.getAttribute("quizQuestions");
        Integer currentIndex = (Integer) session.getAttribute("currentIndex");
        String quizCategory = (String) session.getAttribute("category");

        // Если состояние отсутствует (пользователь зашёл напрямую) – на стартовую страницу
        if (quizQuestions == null || currentIndex == null) {
            response.sendRedirect(request.getContextPath() + "/start-servlet");
            return;
        }

        // Если все вопросы пройдены – показываем результаты
        if (currentIndex >= quizQuestions.size()) {
            showResults(request, response, session, quizQuestions, quizCategory);
            // Очищаем сессию, чтобы результаты не показывались повторно
            session.removeAttribute("quizQuestions");
            session.removeAttribute("currentIndex");
            session.removeAttribute("results");
            session.removeAttribute("category");
            return;
        }

        // Иначе отображаем текущий вопрос
        showQuestion(request, response, session, quizQuestions, currentIndex, quizCategory);
    }

    /**
     * Обрабатывает POST-запросы – приход ответа от пользователя или автоматическая отправка при таймауте.
     * Определяет правильность ответа, сохраняет результат, увеличивает индекс и перенаправляет на GET.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        List<Question> quizQuestions = (List<Question>) session.getAttribute("quizQuestions");
        Integer currentIndex = (Integer) session.getAttribute("currentIndex");
        boolean[] results = (boolean[]) session.getAttribute("results");

        // Если состояние потеряно – возвращаем на выбор категории
        if (quizQuestions == null || currentIndex == null || results == null) {
            response.sendRedirect(request.getContextPath() + "/start-servlet");
            return;
        }

        // Получаем параметры из формы
        String answerParam = request.getParameter("answer");   // для обычных: "A"/"B"/"C", для текстовых: строка
        String timeoutParam = request.getParameter("timeout"); // "true" или "false"

        boolean isTimeout = "true".equals(timeoutParam);
        Question currentQ = quizQuestions.get(currentIndex);

        // Определяем правильность ответа
        boolean isCorrect = false;
        if (!isTimeout) {   // если таймаут – ответ автоматически неверный
            if (currentQ.isTextInput()) {
                // Текстовый вопрос: сравниваем строки без учёта регистра и лишних пробелов
                if (answerParam != null) {
                    String userAnswer = answerParam.trim();
                    String correctAns = currentQ.getCorrectText().trim();
                    if (userAnswer.equalsIgnoreCase(correctAns)) {
                        isCorrect = true;
                    }
                }
            } else {
                // Вопрос с вариантами: проверяем, что ответ – одна буква и совпадает с correct
                if (answerParam != null && answerParam.length() == 1) {
                    char userAnswer = answerParam.charAt(0);
                    if (userAnswer == currentQ.getCorrect()) {
                        isCorrect = true;
                    }
                }
            }
        }
        // Если isTimeout == true или ответ не выбран – isCorrect остаётся false

        // Сохраняем результат и переходим к следующему вопросу
        results[currentIndex] = isCorrect;
        currentIndex++;

        // Обновляем состояние в сессии
        session.setAttribute("currentIndex", currentIndex);
        session.setAttribute("results", results);

        // PRG-паттерн: POST -> Redirect -> GET, чтобы избежать повторной отправки формы
        response.sendRedirect(request.getContextPath() + "/questions-servlet");
    }

    /**
     * Генерирует HTML-страницу с одним вопросом и таймером на 30 секунд.
     * Для вопросов с вариантами – радиокнопки, для текстовых – поле ввода.
     * JavaScript-таймер автоматически отправляет форму при истечении времени.
     */
    private void showQuestion(HttpServletRequest request, HttpServletResponse response,
                              HttpSession session, List<Question> questions, int index, String category)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Question q = questions.get(index);

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Викторина – " + category + "</title>");
        out.println("<style>");
        out.println("#timer { font-size: 24px; font-weight: bold; margin-bottom: 15px; }");
        out.println(".question-text { font-size: 20px; margin-bottom: 15px; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<h2>Категория: " + category + "</h2>");
        out.println("<div id='timer'>Осталось: 30 сек.</div>");
        out.println("<div class='question-text'><b>Вопрос " + (index + 1) + " из " + questions.size() + ":</b><br>" + q.getText() + "</div>");

        // Форма для отправки ответа
        out.println("<form id='quizForm' method='post' action='" + request.getContextPath() + "/questions-servlet'>");
        out.println("<input type='hidden' name='timeout' id='timeoutFlag' value='false'>");

        // В зависимости от типа вопроса выводим либо радиокнопки, либо текстовое поле
        if (q.isTextInput()) {
            out.println("<label>Ваш ответ: <input type='text' name='answer' id='answerInput' placeholder='Введите ответ'></label><br><br>");
        } else {
            out.println("<label><input type='radio' name='answer' value='A'> " + q.getOptionA() + "</label><br>");
            out.println("<label><input type='radio' name='answer' value='B'> " + q.getOptionB() + "</label><br>");
            out.println("<label><input type='radio' name='answer' value='C'> " + q.getOptionC() + "</label><br><br>");
        }

        out.println("<input type='submit' id='submitBtn' value='Ответить'>");
        out.println("</form>");

        // --------------------- JavaScript-таймер и логика отправки ---------------------
        out.println("<script>");
        out.println("var timeLeft = 30;");                           // начальное время в секундах
        out.println("var timerDisplay = document.getElementById('timer');");
        out.println("var timeoutFlag = document.getElementById('timeoutFlag');");
        out.println("var form = document.getElementById('quizForm');");
        out.println("var submitBtn = document.getElementById('submitBtn');");

        // Автофокус на текстовое поле, если вопрос текстовый
        if (q.isTextInput()) {
            out.println("document.getElementById('answerInput').focus();");
        }

        // Запускаем таймер: каждую секунду обновляем отображение
        out.println("var timerId = setInterval(function() {");
        out.println("  timeLeft--;");
        out.println("  timerDisplay.innerText = 'Осталось: ' + timeLeft + ' сек.';");
        out.println("  if (timeLeft <= 0) {");                       // Время вышло
        out.println("    clearInterval(timerId);");                 // Останавливаем таймер
        out.println("    timeoutFlag.value = 'true';");             // Флаг таймаута
        out.println("    submitBtn.disabled = true;");              // Блокируем кнопку
        out.println("    form.submit();");                          // Автоматическая отправка
        out.println("  }");
        out.println("}, 1000);");

        // При ручной отправке (нажатие кнопки) останавливаем таймер, чтобы избежать двойной отправки
        out.println("form.addEventListener('submit', function() {");
        out.println("  clearInterval(timerId);");
        out.println("  submitBtn.disabled = true;");
        out.println("});");
        out.println("</script>");

        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Выводит итоговую страницу с количеством правильных/неправильных ответов и детализацией.
     */
    private void showResults(HttpServletRequest request, HttpServletResponse response,
                             HttpSession session, List<Question> questions, String category)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        boolean[] results = (boolean[]) session.getAttribute("results");

        // Подсчитываем правильные ответы
        int correctCount = 0;
        for (boolean b : results) {
            if (b) correctCount++;
        }
        int total = questions.size();
        int incorrectCount = total - correctCount;

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><meta charset='UTF-8'><title>Результаты – " + category + "</title></head>");
        out.println("<body>");
        out.println("<h1>Викторина завершена!</h1>");
        out.println("<h2>Категория: " + category + "</h2>");
        out.println("<p><b>Правильных ответов:</b> " + correctCount + " из " + total + "</p>");
        out.println("<p><b>Неправильных ответов (или пропущено):</b> " + incorrectCount + "</p>");
        out.println("<hr>");
        out.println("<h3>Детализация:</h3>");
        out.println("<ol>");

        // Перебираем все вопросы и выводим результат
        for (int i = 0; i < total; i++) {
            Question q = questions.get(i);
            out.println("<li>");
            out.println(q.getText() + "<br>");
            if (q.isTextInput()) {
                out.println(results[i] ? "✅ Верно" : "❌ Неверно (правильный ответ: " + q.getCorrectText() + ")");
            } else {
                out.println(results[i] ? "✅ Верно" : "❌ Неверно (правильный: " + q.getCorrect() + ")");
            }
            out.println("</li><br>");
        }
        out.println("</ol>");
        out.println("<p><a href='" + request.getContextPath() + "/start-servlet'>Пройти ещё раз</a></p>");
        out.println("</body>");
        out.println("</html>");
    }
}