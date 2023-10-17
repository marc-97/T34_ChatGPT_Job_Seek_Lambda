package org.t34.util;

import org.junit.jupiter.api.Test;
import org.t34.exception.InvalidTokenException;

import static org.junit.jupiter.api.Assertions.*;


public class GeneralHelperTest {

    @Test
    public void testIsPasswordMatch() {
        String password = "testPassword";
        String hashedPassword = GeneralHelper.hashPassword(password);

        boolean isMatch = GeneralHelper.isPasswordMatch(password, hashedPassword);
        assertTrue(isMatch);
    }

    @Test
    public void testHashPassword() {
        String password = "testPassword";

        String hashedPassword = GeneralHelper.hashPassword(password);

        assertNotNull(hashedPassword);
        assertNotEquals(password, hashedPassword);
    }

    @Test
    public void testEncodeAndDecodeJWT() {
        String subject = "1";

        String jwt = GeneralHelper.encodeJWT(subject);

        assertNotNull(jwt);
        assertFalse(jwt.isEmpty());

        try {
            int decodedSubject = GeneralHelper.decodeJWT(jwt);

            assertEquals(subject, String.valueOf(decodedSubject));
        } catch (InvalidTokenException e) {
            fail("InvalidTokenException should not be thrown for a valid JWT.");
        }
    }

    @Test
    public void testDecodeInvalidJWT() {
        String invalidJWT = "invalid.jwt.string";

        Exception exception = assertThrows(InvalidTokenException.class, () -> GeneralHelper.decodeJWT(invalidJWT));
        assertTrue(exception.getMessage().contains("Unable to read JSON value"));
    }
}
