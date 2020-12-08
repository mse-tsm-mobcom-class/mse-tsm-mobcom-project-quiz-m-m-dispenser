// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

package ch.mse.quiz.printes;

public interface ToastPrinter {

    void print(String message);

    void printError(String message);

    void printError(int id);
}
