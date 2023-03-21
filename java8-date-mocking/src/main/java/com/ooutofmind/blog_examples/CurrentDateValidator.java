package com.ooutofmind.blog_examples;

import java.time.LocalDate;

public class CurrentDateValidator {
    public boolean isToday(int dayOfMonth) {
        return LocalDate.now().getDayOfMonth() == dayOfMonth;
    }
}
