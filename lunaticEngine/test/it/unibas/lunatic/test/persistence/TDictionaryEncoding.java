package it.unibas.lunatic.test.persistence;

import it.unibas.lunatic.persistence.encoding.DictionaryEncoder;
import junit.framework.TestCase;
import speedy.model.database.operators.dbms.IValueEncoder;

public class TDictionaryEncoding extends TestCase {

//    public void testEncode() {
//        IValueEncoder valueEncoder = new DictionaryEncoder("iBench-Ontology-256-mcscenario-dbms.xml");
//        valueEncoder.prepareForEncoding();
//        printEncodedValue("6088395", valueEncoder);
//        printEncodedValue("7718165", valueEncoder);
//        printEncodedValue("289", valueEncoder);
//    }

    public void testDecode() {
        IValueEncoder valueEncoder = new DictionaryEncoder("iBench-Ontology-256-mcscenario-dbms.xml");
        valueEncoder.prepareForDecoding();
        printDecodedValue("6088395", valueEncoder);
        printDecodedValue("7718165", valueEncoder);
        printDecodedValue("289", valueEncoder);
    }

    private void printEncodedValue(String original, IValueEncoder valueEncoder) {
        String encodedValue = valueEncoder.encode(original);
        System.out.println(original + " - " + encodedValue);
    }

    private void printDecodedValue(String encoded, IValueEncoder valueEncoder) {
        String original = valueEncoder.decode(encoded);
        System.out.println(encoded + " - " + original);
    }

}
