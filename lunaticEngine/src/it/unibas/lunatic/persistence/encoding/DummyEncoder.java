package it.unibas.lunatic.persistence.encoding;

import speedy.model.database.operators.dbms.IValueEncoder;

public class DummyEncoder implements IValueEncoder {

    public String encode(String original) {
        return original;
    }

    public String decode(String encoded) {
        return encoded;
    }

    public void prepareForEncoding() {

    }

    public void closeEncoding() {
    }

    public void prepareForDecoding() {

    }

    public void closeDecoding() {
    }

    public void waitingForEnding() {
    }

    public void removeExistingEncoding() {
    }

}
