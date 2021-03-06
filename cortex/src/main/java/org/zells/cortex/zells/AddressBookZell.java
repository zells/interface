package org.zells.cortex.zells;

import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.messages.AddressMessage;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.StringMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AddressBookZell implements Zell {

    private Map<String, Address> addresses = new HashMap<String, Address>();
    private Dish dish;
    private Set<Address> observers = new HashSet<Address>();

    public AddressBookZell(Dish dish) {
        this.dish = dish;
    }

    @Override
    public void receive(Message message) {
        if (message.read(0).equals(new StringMessage("entries"))) {

            if (!message.read("at").isNull() && message.keys().contains("put")) {
                String name = message.read("at").asString().replace(" ", "");
                Address address = message.read("put").asAddress();
                put(name, address);
            } else if (!message.read("remove").isNull()) {
                String name = message.read("remove").asString();
                remove(name);
            } else if (!message.read("tell").isNull()) {
                CompositeMessage book = new CompositeMessage();
                for (String name : addresses.keySet()) {
                    book.put(name, new AddressMessage(addresses.get(name)));
                }
                dish.send(message.read("tell").asAddress(), book);
            }

        } else if (message.read(0).equals(new StringMessage("observers"))) {

            if (!message.read("add").isNull()) {
                observers.add(message.read("add").asAddress());
            }
        }
    }

    private void notifyObservers(Message change) {
        for (Address observer : observers) {
            dish.send(observer, new CompositeMessage(new StringMessage("observer"))
                    .put("stateChanged", change));
        }
    }

    public void put(String name, Address address) {
        boolean replaced = addresses.containsKey(name);
        addresses.put(name, address);
        notifyObservers(new CompositeMessage()
                .put(replaced ? "replaced" : "added", new CompositeMessage()
                        .put(name, new AddressMessage(address))));
    }

    private void remove(String name) {
        Address removed = addresses.remove(name);
        if (removed != null) {
            notifyObservers(new CompositeMessage()
                    .put("removed", new CompositeMessage()
                            .put(name, new AddressMessage(removed))));
        }
    }

    public Address get(String name) {
        if (!has(name)) {
            throw new RuntimeException("Not in address book: " + name);
        }
        return addresses.get(name);
    }

    public boolean has(String name) {
        return addresses.containsKey(name);
    }

    public Map<String, Address> getAddresses() {
        return new HashMap<String, Address>(addresses);
    }

    public boolean contains(Address address) {
        return addresses.containsValue(address);
    }

    public String nameOf(Address address) {
        for (String name : addresses.keySet()) {
            if (addresses.get(name).equals(address)) {
                return name;
            }
        }
        throw new RuntimeException("Not in book: " + address);
    }
}
