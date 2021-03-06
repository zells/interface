package org.zells.cortex.synapses.communicator;

import org.zells.cortex.zells.AddressBookZell;
import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.Messenger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Communicator {
    private final ArrayList<Message> receivedMessages = new ArrayList<Message>();
    private final Address target;
    private final Dish dish;
    private final AddressBookZell book;

    public Communicator(Address target, Dish dish, AddressBookZell book) {
        this.target = target;
        this.dish = dish;
        this.book = book;
    }

    public void send(String input, Listener listener) throws Exception {
        Map<String, Address> aliases = prepareAliases(listener);

        InputParser parser = new InputParser(input, receivedMessages, aliases);
        Address receiver = resolveAddress(aliases, parser.getReceiver());
        listener.onParsed(parser.getReceiver(), parser.getMessage());

        waitFor(dish.send(receiver, parser.getMessage()), listener);
    }

    private void waitFor(Messenger messenger, final Listener listener) {
        messenger.when(new Messenger.Delivered() {
            @Override
            public void then() {
                listener.onSuccess();
            }
        });
        messenger.when(new Messenger.Failed() {
            @Override
            public void then(Exception e) {
                listener.onFailure(e);
            }
        });
    }

    private Map<String, Address> prepareAliases(final Listener listener) {
        Map<String, Address> addresses = new HashMap<String, Address>(book.getAddresses()) {
            public Address get(Object key) {
                if (key.equals("+")) {
                    ReceiverZell receiver = new ReceiverZell(dish) {
                        @Override
                        protected void received(Message message) {
                            receiveResponse(message, listener);
                        }
                    };
                    Address receiverAddress = dish.add(receiver);
                    receiver.setAddress(receiverAddress);
                    return receiverAddress;
                }
                return super.get(key);
            }

            @Override
            public boolean containsKey(Object key) {
                return key.equals("+") || super.containsKey(key);
            }
        };

        if (target != null) {
            addresses.put(".", target);
        }

        return addresses;
    }

    synchronized private void receiveResponse(Message message, Listener listener) {
        receivedMessages.add(message);
        listener.onResponse(receivedMessages.size() - 1, message);
    }

    private Address resolveAddress(Map<String, Address> aliases, String receiver) {
        if (receiver.startsWith("0x")) {
            return Address.fromString(receiver);
        } else if (aliases.containsKey(receiver)) {
            return aliases.get(receiver);
        } else {
            return Address.fromString(receiver);
        }
    }

    public abstract static class Listener {

        protected void onParsed(String receiver, Message message) {
        }

        protected void onSuccess() {
        }

        protected void onFailure(Exception e) {
        }

        protected void onResponse(int sequence, Message message) {
        }
    }
}
