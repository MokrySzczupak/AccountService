package account.service;

import java.util.ArrayList;
import java.util.List;

public class BreachedPasswords {
    private final List<String> breachedPasswords = new ArrayList<>(List.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"));

    public boolean contains(String password) {
        return breachedPasswords.contains(password);
    }
}
