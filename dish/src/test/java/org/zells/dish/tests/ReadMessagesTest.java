package org.zells.dish.tests;

import org.junit.Test;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.messages.*;
import org.zells.dish.util.ByteArray;

import java.util.Arrays;
import java.util.HashSet;

public class ReadMessagesTest {

    @Test
    public void nullMessage() {
        Message m = new NullMessage();

        assert m.isNull();
        assert !m.isTrue();
        assert m.asString().equals("");
        assert m.asInteger() == 0;
        assert Arrays.equals(m.asBytes(), new byte[0]);
        assert m.asAddress().equals(Address.fromBytes(new byte[0]));
        assert m.keys().isEmpty();
        assert m.read("foo").read("bar").isNull();
    }

    @Test
    public void trueBooleanMessage() {
        Message m = new BooleanMessage(true);

        assert !m.isNull();
        assert m.isTrue();
        assert m.asString().equals("true");
        assert m.asInteger() == 1;
        assert Arrays.equals(m.asBytes(), new byte[]{1});
        assert m.asAddress().equals(Address.fromBytes(new byte[0]));
        assert m.keys().isEmpty();
    }

    @Test
    public void falseBooleanMessage() {
        Message m = new BooleanMessage(false);

        assert !m.isNull();
        assert !m.isTrue();
        assert m.asString().equals("");
        assert m.asInteger() == 0;
        assert Arrays.equals(m.asBytes(), new byte[]{0});
        assert m.asAddress().equals(Address.fromBytes(new byte[0]));
        assert m.keys().isEmpty();
    }

    @Test
    public void emptyStringMessage() {
        Message m = new StringMessage("");

        assert !m.isNull();
        assert !m.isTrue();
        assert m.asString().equals("");
        assert m.asInteger() == 0;
        assert Arrays.equals(m.asBytes(), new byte[0]);
        assert m.asAddress().equals(Address.fromBytes(new byte[0]));
        assert m.keys().isEmpty();
    }

    @Test
    public void stringMessage() {
        Message m = new StringMessage("foo");

        assert !m.isNull();
        assert m.isTrue();
        assert m.asString().equals("foo");
        assert m.asInteger() == 1;
        assert Arrays.equals(m.asBytes(), "foo".getBytes());
        assert m.asAddress().equals(Address.fromBytes("foo".getBytes()));
        assert m.keys().isEmpty();
    }

    @Test
    public void integerMessage() {
        Message m = new IntegerMessage(42);

        assert !m.isNull();
        assert m.isTrue();
        assert m.asString().equals("42");
        assert m.asInteger() == 42;
        assert Arrays.equals(m.asBytes(), new byte[]{0, 0, 0, 42});
        assert m.asAddress().equals(Address.fromBytes(new byte[]{0, 0, 0, 42}));
        assert m.keys().isEmpty();
    }

    @Test
    public void zeroIntegerMessage() {
        Message m = new IntegerMessage(0);

        assert !m.isNull();
        assert !m.isTrue();
        assert m.asString().equals("0");
        assert m.asInteger() == 0;
        assert Arrays.equals(m.asBytes(), new byte[]{0, 0, 0, 0});
        assert m.asAddress().equals(Address.fromBytes(new byte[]{0, 0, 0, 0}));
        assert m.keys().isEmpty();
    }

    @Test
    public void binaryMessage() {
        Message m = new BinaryMessage("mu".getBytes());

        assert !m.isNull();
        assert m.isTrue();
        assert m.asString().equals("0x6d75");
        assert m.asInteger() == 0x6d75;
        assert Arrays.equals(m.asBytes(), "mu".getBytes());
        assert m.asAddress().equals(Address.fromBytes("mu".getBytes()));
        assert m.keys().isEmpty();
    }

    @Test
    public void emptyBinaryMessage() {
        Message m = new BinaryMessage(new byte[0]);

        assert !m.isNull();
        assert !m.isTrue();
        assert m.asString().equals("0x");
        assert m.asInteger() == 0;
        assert Arrays.equals(m.asBytes(), new byte[0]);
        assert m.asAddress().equals(Address.fromBytes(new byte[0]));
        assert m.keys().isEmpty();
    }

    @Test
    public void addressMessage() {
        Message m = new AddressMessage(Address.fromString("42de"));

        assert !m.isNull();
        assert m.isTrue();
        assert m.asString().equals("0x42de");
        assert m.asInteger() == 0x42de;
        assert Arrays.equals(m.asBytes(), ByteArray.fromHexString("42de"));
        assert m.asAddress().equals(Address.fromString("42de"));
        assert m.keys().isEmpty();
    }

    @Test
    public void compositeMessage() {
        Message m = new CompositeMessage()
                .put("one", new StringMessage("uno"))
                .put("and", new CompositeMessage()
                        .put("two", new IntegerMessage(2)));

        assert !m.isNull();
        assert m.isTrue();
        assert m.asString().contains("and:{two:2}");
        assert m.asString().contains("one:uno");
        assert m.asInteger() == 1;
        assert Arrays.equals(m.asBytes(), new byte[0]);

        assert m.keys().contains("one");
        assert m.keys().contains("and");
        assert m.read("one").asString().equals("uno");
        assert m.read("and").read("two").asInteger() == 2;
        assert m.read("not").isNull();
    }

    @Test
    public void compositeMessageWithNumericalKeys() {
        Message m = new CompositeMessage()
                .put(1, new StringMessage("one"))
                .put(42, new StringMessage("fourty-two"))
                .put("21", new CompositeMessage(new IntegerMessage(21), new IntegerMessage(22)));

        assert m.keys().equals(new HashSet<String>(Arrays.asList("1", "42", "21")));
        assert m.read(1).asString().equals("one");
        assert m.read("1").asString().equals("one");
        assert m.read(21).read(0).asString().equals("21");
        assert m.read(21).read(1).asString().equals("22");
    }
}
