package com.nec.middleware.rbacAuth.rbac.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Generates cryptographically secure passwords with configurable complexity requirements.
 *
 * <p>Features:
 * - Uppercase letters (A-Z)
 * - Lowercase letters (a-z)
 * - Numbers (0-9)
 * - Special characters (!@#$%^&*)
 * - Configurable minimum length (default: 12 characters)
 * - Uses SecureRandom for cryptographic strength
 *
 * <p>Example usage:
 * <pre>
 *   @Autowired
 *   private SecurePasswordGenerator passwordGenerator;
 *
 *   String password = passwordGenerator.generatePassword();  // 12-character password
 *   String longPassword = passwordGenerator.generatePassword(16);  // 16-character password
 * </pre>
 */
@Component
public class SecurePasswordGenerator {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*-_=+";

    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + NUMBERS + SPECIAL;
    private static final int DEFAULT_PASSWORD_LENGTH = 12;

    private final SecureRandom random;

    public SecurePasswordGenerator() {
        this.random = new SecureRandom();
    }

    /**
     * Generates a password with default length of 12 characters.
     *
     * @return a secure password containing uppercase, lowercase, numbers, and special characters
     */
    public String generatePassword() {
        return generatePassword(DEFAULT_PASSWORD_LENGTH);
    }

    /** Alias for bulk-upload flows expecting {@code generateSecurePassword()}. */
    public String generateSecurePassword() {
        return generatePassword();
    }

    /**
     * Generates a password with the specified length.
     * Ensures at least one character from each character class (uppercase, lowercase, number, special).
     *
     * @param length the desired password length (minimum 4)
     * @return a secure password
     * @throws IllegalArgumentException if length < 4
     */
    public String generatePassword(int length) {
        if (length < 4) {
            throw new IllegalArgumentException("Password length must be at least 4 characters");
        }

        StringBuilder password = new StringBuilder(length);

        // Ensure at least one character from each required character class
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        // Fill the rest randomly from all characters
        for (int i = 4; i < length; i++) {
            password.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        // Shuffle the password to randomize the position of required characters
        return shuffle(password.toString());
    }

    /**
     * Randomly shuffles the characters in a string using Fisher-Yates algorithm.
     *
     * @param str the string to shuffle
     * @return the shuffled string
     */
    private String shuffle(String str) {
        char[] chars = str.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // Swap
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    /**
     * Validates if a password meets the minimum complexity requirements.
     *
     * @param password the password to validate
     * @return true if password contains at least one uppercase, one lowercase, one number, and one special character
     */
    public boolean isPasswordValid(String password) {
        if (password == null || password.length() < DEFAULT_PASSWORD_LENGTH) {
            return false;
        }

        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*\\-_=+].*");

        return hasUppercase && hasLowercase && hasNumber && hasSpecial;
    }
}



