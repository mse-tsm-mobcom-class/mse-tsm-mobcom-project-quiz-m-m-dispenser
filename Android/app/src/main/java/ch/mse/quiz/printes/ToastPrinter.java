package ch.mse.quiz.printes;

public interface ToastPrinter {

    void print(String message);

    void printError(String message);

    void printError(int id);
}
