package quiz.javadz18;
import java.io.Serializable;

/**
 * Класс, описывающий один вопрос викторины.
 * Поддерживает два типа вопросов:
 * 1) С выбором одного варианта из трёх (A/B/C) – поле correct хранит правильную букву.
 * 2) С текстовым вводом (пользователь сам пишет ответ) – поле correctText хранит эталонный ответ,
 *    поле textInput = true, варианты не используются.
 * Реализует Serializable для хранения в HttpSession.
 */
public class Question implements Serializable {
    private static final long serialVersionUID = 1L;

    private String category;      // категория вопроса (История, Спорт, ...)
    private String text;          // текст вопроса
    private String optionA;       // вариант A
    private String optionB;       // вариант B
    private String optionC;       // вариант C
    private char correct;         // правильный ответ для вариантов ('A','B','C')
    private boolean textInput;    // true – вопрос с текстовым вводом
    private String correctText;   // правильный ответ для текстового ввода

    /**
     * Конструктор для вопроса с выбором варианта.
     * @param category категория
     * @param text     текст вопроса
     * @param optionA  вариант A
     * @param optionB  вариант B
     * @param optionC  вариант C
     * @param correct  правильный ответ (A/B/C)
     */
    public Question(String category, String text, String optionA, String optionB, String optionC, char correct) {
        this.category = category;
        this.text = text;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.correct = correct;
        this.textInput = false;
        this.correctText = null;
    }

    /**
     * Конструктор для вопроса с текстовым вводом.
     * @param category    категория
     * @param text        текст вопроса
     * @param correctText эталонный правильный ответ (сравнение без учёта регистра и пробелов)
     */
    public Question(String category, String text, String correctText) {
        this.category = category;
        this.text = text;
        this.textInput = true;
        this.correctText = correctText;
        // варианты не используются
        this.optionA = null;
        this.optionB = null;
        this.optionC = null;
        this.correct = ' ';
    }

    // Геттеры
    public String getCategory() { return category; }
    public String getText() { return text; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public char getCorrect() { return correct; }
    public boolean isTextInput() { return textInput; }
    public String getCorrectText() { return correctText; }

    /**
     * Отладочный вывод вопроса в консоль.
     */
    public void display() {
        System.out.printf("[%s] %s\n", category, text);
        if (!textInput) {
            System.out.printf("A) %s\nB) %s\nC) %s\n", optionA, optionB, optionC);
        } else {
            System.out.println("(текстовый ответ)");
        }
    }
}