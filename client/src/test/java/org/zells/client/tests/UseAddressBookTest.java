package org.zells.client.tests;

import org.junit.Ignore;
import org.junit.Test;

public class UseAddressBookTest {

    @Test
    @Ignore
    public void defaultAlias() {
//        user.hear("client hello");
//        assert dish.sent.get(0).getKey().equals(Address.fromString("fade"));
    }

    @Test
    @Ignore
    public void addAlias() {
//        user.hear("client alias use:foo for:0xdada");
//        user.hear("foo bar");
//
//        assert user.told.contains("Set alias [foo] for [0xdada]");
//        assert dish.sent.get(1).getKey().equals(Address.fromString("dada"));
    }

    @Test
    @Ignore
    public void ignoreSpaces() {
//        user.hear("client alias use:\"foo bar\" for:0xdada");
//
//        assert user.told.contains("Set alias [foobar] for [0xdada]");
    }

    @Test
    @Ignore
    public void overwriteAlias() {
//        user.hear("client alias use:foo for:dada");
//        user.hear("client alias use:foo for:fade");
//        user.hear("foo bar");
//
//        assert dish.sent.get(2).getKey().equals(Address.fromString("fade"));
    }

    @Test
    @Ignore
    public void removeAlias() {
//        user.hear("client alias use:da for:dada");
//        user.hear("client alias remove:da");
//        user.hear("da hello");
//
//        assert user.told.contains("Removed alias [da]");
//        assert dish.sent.get(2).getKey().equals(Address.fromString("da"));
    }

    @Test
    @Ignore
    public void removeNonExistingAlias() {
//        user.hear("client alias remove:da");
//        assert dish.logged.get(0).getMessage().equals("No such alias: da");
    }

    @Test
    @Ignore
    public void listAliases() {
//        user.hear("client alias use:foo for:dada");
//        user.hear("client alias use:bar for:fade");
//        user.hear("client alias list");
//
//        assert user.told.get(2).contains("Aliases:");
//        assert user.told.get(2).contains("bar: 0xfade");
//        assert user.told.get(2).contains("foo: 0xdada");
//        assert user.told.get(2).contains("client: 0xfade");
    }

    @Test
    @Ignore
    public void useAliasInMessage() {
//        user.hear("client alias use:foo for:dada");
//        user.hear("fade from:@foo");
//
//        assert dish.lastMessage.read("from").equals(BinaryMessage.fromString("0xdada"));
    }
}