package com.defano.wyldcard.util;

import com.defano.hypertalk.exception.HtSemanticException;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public interface Hashable {

    default String calculateSha256Hash(String input) throws HtSemanticException {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digestData = md.digest(input.getBytes());
            String hash = new BigInteger(1, digestData).toString(16);

            while (hash.length() < 32) {
                hash = "0" + hash;
            }

            return hash;
        }

        catch (NoSuchAlgorithmException e) {
            throw new HtSemanticException("Cannot generate encrypted hashes on this system.");
        }
    }

}
