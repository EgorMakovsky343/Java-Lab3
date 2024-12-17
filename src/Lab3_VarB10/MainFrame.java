package Lab3_VarB10;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    private static final int WIDTH = 700;
    private static final int HEIGHT = 500;
    // Массив коэффициентов многочлена
    private Double[] coefficients;
    // Объект диалогового окна для выбора файлов
    // Компонент не создается изначально, т.к. может и не понадобиться
    // пользователю если тот не собирается сохранять данные в файл
    private JFileChooser fileChooser = null;

    // Элементы меню вынесены в поля данных класса, так как ими необходимо
    // манипулировать из разных мест
    private JMenuItem saveToTextMenuItem;
    private JMenuItem saveToGraphicsMenuItem;
    private JMenuItem searchValueMenuItem;
    private JMenuItem aboutProgramMenuItem;


    // Поля ввода для считывания значений переменных
    private JTextField textFieldFrom;
    private JTextField textFieldTo;
    private JTextField textFieldStep;

    private Box hBoxResult;

    // Визуализатор ячеек таблицы
    private GornerTableCellRenderer renderer = new GornerTableCellRenderer();
    // Модель данных с результатами вычислений
    private GornerTableModel data;

    public MainFrame(Double[] coefficients) {
        super("Табулирование многочлена на отрезке по схеме Горнера");
        // Запомнить во внутреннем поле переданные коэффициенты
        this.coefficients = coefficients;
        // Установить размеры окна
        setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        // Отцентрировать окно приложения на экране
        setLocation((kit.getScreenSize().width - WIDTH)/2,
                (kit.getScreenSize().height - HEIGHT)/2);


        // Создать меню
        JMenuBar menuBar = new JMenuBar();
        // Установить меню в качестве главного меню приложения
        setJMenuBar(menuBar);
        // Добавить в меню пункт меню "Файл"
        JMenu fileMenu = new JMenu("Файл");
        // Добавить его в главное меню
        menuBar.add(fileMenu);

        // Создать пункт меню "Таблица"
        JMenu tableMenu = new JMenu("Таблица");
        // Добавить его в главное меню
        menuBar.add(tableMenu);

        JMenu helpMenu = new JMenu("Справка");
        menuBar.add(helpMenu);

        Action aboutProgramAction = new AbstractAction("О программе") {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainFrame.this,
                        "Автор: Маковский Егор 9 группа", "О программе",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        };
        aboutProgramMenuItem = helpMenu.add(aboutProgramAction);


        // Создать новое "действие" по сохранению в текстовый файл
        Action saveToTextAction = new AbstractAction("Сохранить в текстовый файл") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser==null) {
                    // Если экземпляр диалогового окна "Открыть файл" ещё не создан,
                    // то создать его
                    fileChooser = new JFileChooser();
                    // и инициализировать текущей директорией
                    fileChooser.setCurrentDirectory(new File("."));
                }
                // Показать диалоговое окно
                if (fileChooser.showSaveDialog(MainFrame.this)==
                        JFileChooser.APPROVE_OPTION) {
                    // Если результат его показа успешный,
                    // сохранить данные в текстовый файл
                    saveToTextFile(fileChooser.getSelectedFile());
                }
            }
        };
// Добавить соответствующий пункт подменю в меню "Файл"
        saveToTextMenuItem = fileMenu.add(saveToTextAction);
// По умолчанию пункт меню является недоступным (данных ещё нет)
        saveToTextMenuItem.setEnabled(false);


// Создать новое "действие" по сохранению в текстовый файл
        Action saveToGraphicsAction = new AbstractAction("Сохранить данные для построения графика") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser==null) {
                    // Если экземпляр диалогового окна "Открыть файл" ещё не создан,
                    // то создать его
                    fileChooser = new JFileChooser();
                    // и инициализировать текущей директорией
                    fileChooser.setCurrentDirectory(new File("."));
                }
                // Показать диалоговое окно
                if (fileChooser.showSaveDialog(MainFrame.this)== JFileChooser.APPROVE_OPTION) {
                    // Если результат его показа успешный,
                    // сохранить данные в двоичный файл
                    saveToGraphicsFile(
                            fileChooser.getSelectedFile());
                }
            }
        };
// Добавить соответствующий пункт подменю в меню "Файл"
        saveToGraphicsMenuItem = fileMenu.add(saveToGraphicsAction);
// По умолчанию пункт меню является недоступным (данных ещё нет)
        saveToGraphicsMenuItem.setEnabled(false);

        Action searchValueAction = new AbstractAction("Найти значение многочлена") {
            public void actionPerformed(ActionEvent event) {
                // Запросить пользователя ввести искомую строку
                String value =
                        JOptionPane.showInputDialog(MainFrame.this, "Введите значение для поиска",
                                "Поиск значения", JOptionPane.QUESTION_MESSAGE);
                // Установить введенное значение в качестве иголки
                renderer.setNeedle(value);
                // Обновить таблицу
                getContentPane().repaint();
            }
        };
        // Добавить действие в меню "Таблица"
        searchValueMenuItem = tableMenu.add(searchValueAction);
// По умолчанию пункт меню является недоступным (данных ещё нет)
        searchValueMenuItem.setEnabled(false);

// Создать область с полями ввода для границ отрезка и шага
// Создать подпись для ввода левой границы отрезка
        JLabel labelForFrom = new JLabel("X изменяется на интервале от:");
// Создать текстовое поле для ввода значения длиной в 10 символов
// со значением по умолчанию 0.0
        textFieldFrom = new JTextField("0.0", 10);
// Установить максимальный размер поля ввода равный предпочитаемому, чтобы
// предотвратить увеличение размера поля ввода
        textFieldFrom.setMaximumSize(textFieldFrom.getPreferredSize());
// Создать подпись для ввода левой границы отрезка
        JLabel labelForTo = new JLabel("до:");
// Создать текстовое поле для ввода значения длиной в 10 символов
// со значением по умолчанию 1.0
        textFieldTo = new JTextField("1.0", 10);
// Установить максимальный размер поля ввода равный предпочитаемому, чтобы
// предотвратить увеличение размера поля ввода
        textFieldTo.setMaximumSize(textFieldTo.getPreferredSize());
// Создать подпись для ввода шага табулирования
        JLabel labelForStep = new JLabel("с шагом:");
// Создать текстовое поле для ввода значения длиной в 10 символов
// со значением по умолчанию 1.0
        textFieldStep = new JTextField("0.1", 10);
// Установить максимальный размер поля ввода равный предпочитаемому, чтобы
// предотвратить увеличение размера поля ввода
        textFieldStep.setMaximumSize(textFieldStep.getPreferredSize());
// Создать контейнер 1 типа "коробка с горизонтальной укладкой"
        Box hBoxRange = Box.createHorizontalBox();
// Задать для контейнера тип рамки "объёмная"
        hBoxRange.setBorder(BorderFactory.createBevelBorder(1));
// Добавить "клей" С1-H1
        hBoxRange.add(Box.createHorizontalGlue());
// Добавить подпись "От"
        hBoxRange.add(labelForFrom);
// Добавить "распорку" C1-H2
        hBoxRange.add(Box.createHorizontalStrut(10));
// Добавить поле ввода "От"
        hBoxRange.add(textFieldFrom);
// Добавить "распорку" C1-H3
        hBoxRange.add(Box.createHorizontalStrut(20));
// Добавить подпись "До"
        hBoxRange.add(labelForTo);
// Добавить "распорку" C1-H4
        hBoxRange.add(Box.createHorizontalStrut(10));
// Добавить поле ввода "До"
        hBoxRange.add(textFieldTo);
// Добавить "распорку" C1-H5
        hBoxRange.add(Box.createHorizontalStrut(20));
// Добавить подпись "с шагом"
        hBoxRange.add(labelForStep);
// Добавить "распорку" C1-H6
        hBoxRange.add(Box.createHorizontalStrut(10));
// Добавить поле для ввода шага табулирования
        hBoxRange.add(textFieldStep);
// Добавить "клей" C1-H7
        hBoxRange.add(Box.createHorizontalGlue());
// Установить предпочтительный размер области равным удвоенному
// минимальному, чтобы при компоновке области совсем не сдавили
        hBoxRange.setPreferredSize(new Dimension(
                Double.valueOf(hBoxRange.getMaximumSize().getWidth()).intValue(),
                Double.valueOf(hBoxRange.getMinimumSize().getHeight()).intValue()*2));
// Установить область в верхнюю (северную) часть компоновки
        getContentPane().add(hBoxRange, BorderLayout.SOUTH);

// Создать кнопку "Вычислить"
        JButton buttonCalc = new JButton("Вычислить");
// Задать действие на нажатие "Вычислить" и привязать к кнопке
        buttonCalc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
                    // Считать значения начала и конца отрезка, шага
                    Double from = Double.parseDouble(textFieldFrom.getText());
                    Double to = Double.parseDouble(textFieldTo.getText());
                    Double step = Double.parseDouble(textFieldStep.getText());

                    // На основе считанных данных создать новый
                    // экземпляр модели таблицы
                    data = new GornerTableModel(from, to, step, MainFrame.this.coefficients);
                    // Создать новый экземпляр таблицы
                    JTable table = new JTable(data);
                    // Установить в качестве визуализатора ячеек для
                    // класса Double разработанный визуализатор
                    table.setDefaultRenderer(Double.class, renderer);
                    // Установить размер строки таблицы в 30 пикселов
                    table.setRowHeight(30);
                    // Удалить все вложенные элементы из контейнера
                    hBoxResult.removeAll();
                    // Добавить в контейнер таблицу, "обёрнутую" в
                    // панель с полосами прокрутки
                    hBoxResult.add(new JScrollPane(table));
                    // Обновить область содержания главного окна
                    getContentPane().validate();
                    // Пометить ряд элементов меню как доступные
                    saveToTextMenuItem.setEnabled(true);
                    saveToGraphicsMenuItem.setEnabled(true);
                    searchValueMenuItem.setEnabled(true);
                } catch (NumberFormatException ex) {
                    // В случае ошибки преобразования чисел показать
                    // сообщение об ошибке
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Ошибка в формате записи числа с плавающей точкой", "Ошибочный формат числа",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });


// Создать кнопку "Очистить поля"
        JButton buttonReset = new JButton("Очистить поля");
// Задать действие на нажатие "Очистить поля" и привязать к кнопке
        buttonReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                // Установить в полях ввода значения по умолчанию
                textFieldFrom.setText("0.0");
                textFieldTo.setText("1.0");
                textFieldStep.setText("0.1");
                // Удалить все вложенные элементы контейнера
                hBoxResult.removeAll();
// Добавить в контейнер пустую панель
                hBoxResult.add(new JPanel());
// Пометить элементы меню как недоступные
                saveToTextMenuItem.setEnabled(false);
                saveToGraphicsMenuItem.setEnabled(false);
                searchValueMenuItem.setEnabled(false);
// Обновить область содержания главного окна
                getContentPane().validate();
            }
        }); // Закрывающая скобка для ActionListener у buttonReset

        // Поместить созданные кнопки в контейнер
        Box hBoxButtons = Box.createHorizontalBox();
        hBoxButtons.setBorder(BorderFactory.createBevelBorder(1));
        hBoxButtons.add(Box.createHorizontalGlue());
        hBoxButtons.add(buttonCalc);
        hBoxButtons.add(Box.createHorizontalStrut(30));
        hBoxButtons.add(buttonReset);
        hBoxButtons.add(Box.createHorizontalGlue());
        // Установить предпочтительный размер области равным удвоенному
        // минимальному, чтобы при
        // компоновке окна область совсем не сдавили
        hBoxButtons.setPreferredSize(new Dimension(
                Double.valueOf(hBoxButtons.getMaximumSize().getWidth()).intValue(),
                Double.valueOf(hBoxButtons.getMinimumSize().getHeight()).intValue()*2));


        // Разместить контейнер с кнопками в нижней (южной) области
// граничной компоновки
        getContentPane().add(hBoxButtons, BorderLayout.NORTH);
// Создать область для вывода результата, пока что пустая
        hBoxResult = Box.createHorizontalBox();
        hBoxResult.add(new JPanel());
        // Установить контейнер hBoxResult в главной (центральной) области
// граничной компоновки
        getContentPane().add(hBoxResult, BorderLayout.CENTER);
    }

    protected void saveToGraphicsFile(File selectedFile) {
        try {
// Создать новый байтовый поток вывода, направленный в
            //указанный файл
            DataOutputStream out = new DataOutputStream(new
                    FileOutputStream(selectedFile));
// Записать в поток вывода попарно значение X в точке,
            //значение многочлена в точке
            for (int i = 0; i<data.getRowCount(); i++) {
                out.writeDouble((Double)data.getValueAt(i,0));
                out.writeDouble((Double)data.getValueAt(i,1));
            }
// Закрыть поток вывода
            out.close();
        } catch (Exception e) {
// Исключительную ситуацию "ФайлНеНайден" в данном случае
            //можно не обрабатывать,
// так как мы файл создаѐм, а не открываем для чтения
        }
    }

    protected void saveToTextFile(File selectedFile) {
        try {
// Создать новый символьный поток вывода, направленный в
            //указанный файл
            PrintStream out = new PrintStream(selectedFile);
// Записать в поток вывода заголовочные сведения
            out.println("Результаты табулирования многочлена по схеме Горнера");
            out.print("Многочлен: ");
            for (int i=0; i<coefficients.length; i++) {
                out.print(coefficients[i] + "*X^" +
                        (coefficients.length-i-1));
                if (i!=coefficients.length-1)
                    out.print(" + ");
            }
            out.println("");
            out.println("Интервал от " + data.getFrom() + " до " +
                    data.getTo() + " с шагом " + data.getStep());
            out.println("====================================================");
// Записать в поток вывода значения в точках
            for (int i = 0; i<data.getRowCount(); i++) {
                out.println("Значение в точке " + data.getValueAt(i,0)
                        + " равно " + data.getValueAt(i,1));
            }
// Закрыть поток
            out.close();
        } catch (FileNotFoundException e) {
// Исключительную ситуацию "ФайлНеНайден" можно не
// обрабатывать, так как мы файл создаѐм, а не открываем
        }
    }


    public static void main(String[] args) {
        // Если не задано ни одного аргумента командной строки -
        // Продолжать вычисления невозможно, коэффициенты неизвестны
        if (args.length == 0) {
            System.out.println("Невозможно табулировать многочлен, для которого не задано ни одного коэффициента!");
            System.exit(-1);
        }
        // Зарезервировать места в массиве коэффициентов столько, сколько
        // аргументов командной строки
        Double[] coefficients = new Double[args.length];
        int i = 0;
        try {
            // Перебрать аргументы, пытаясь преобразовать их в Double
            for (String arg : args) {
                coefficients[i++] = Double.parseDouble(arg);
            }
        }
        catch(NumberFormatException ex){
            // Если преобразование невозможно - сообщить об ошибке и
            // завершиться
            System.out.println("Ошибка преобразования строки '" +
                    args[i] + "' в число типа Double");
            System.exit(-2);
        }

        // Создать экземпляр главного окна, передав ему коэффициенты
        MainFrame frame = new MainFrame(coefficients);
        // Задать действие, выполняемое при закрытии окна
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
