package de.uniks.pioneers.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CryptServiceTest {

    final CryptService cryptService = new CryptService();

    @Test
    void encrypt() {

        String encrypted = cryptService.encrypt("string");
        assertEquals(encrypted, "NIkMHlzNox2IE8W2v3kWGg==");

    }

    @Test
    void decrypt() {

        String decrypted = cryptService.decrypt("NIkMHlzNox2IE8W2v3kWGg==");
        assertEquals(decrypted, "string");

    }
}