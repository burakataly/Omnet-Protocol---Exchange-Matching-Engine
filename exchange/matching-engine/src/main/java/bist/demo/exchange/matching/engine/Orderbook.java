package bist.demo.exchange.matching.engine;

import bist.demo.exchange.common.Constants;

import java.util.LinkedList;
import java.util.ListIterator;

public class Orderbook {
    private final String name;
    private final LinkedList<OrderNode> buyOrders;
    private final LinkedList<OrderNode> sellOrders;
    private final LinkedList<OrderNode> matches;

    public Orderbook(String name) {
        this.name = name;
        buyOrders = new LinkedList<>();
        sellOrders = new LinkedList<>();
        matches = new LinkedList<>();
    }

    public void handleNewOrder(byte side, int quantity, int price) {
        OrderNode newOrder = new OrderNode(quantity, price);
        if(side == Constants.SIDE_BUY){
            matchTheOrder(sellOrders, newOrder);
            if(newOrder.quantity > 0) addInOrder(buyOrders, newOrder, -1);
        }
        else if(side == Constants.SIDE_SELL){
            matchTheOrder(buyOrders, newOrder);
            if(newOrder.quantity > 0) addInOrder(sellOrders, newOrder, 1);
        }
        else{
            throw new IllegalArgumentException("Invalid side. Must be 'B' or 'S'.");
        }
    }

    private void matchTheOrder(LinkedList<OrderNode> list, OrderNode newOrder){
        ListIterator<OrderNode> iterator = list.listIterator();
        int quantity = newOrder.quantity;
        while (iterator.hasNext() && newOrder.quantity > 0) {
            OrderNode order = iterator.next();
            if(order.price == newOrder.price){
                if(order.quantity == newOrder.quantity){
                    iterator.remove();
                }
                else if(order.quantity > newOrder.quantity){
                    order.quantity -= newOrder.quantity;
                }
                else{
                    newOrder.quantity -= order.quantity;
                    iterator.remove();
                }
            }
        }
        if(quantity != newOrder.quantity){
            matches.addFirst(new OrderNode(quantity - newOrder.quantity, newOrder.price));
        }
    }

    public void print() {
        System.out.printf("Orderbook: %s\n", name);
        System.out.println("Matches");
        printList(matches, "Match");
        System.out.println("Buy Orders");
        printList(buyOrders, "Buy");
        System.out.println("Sell Orders");
        printList(sellOrders, "Sell");
    }

    private void printList(LinkedList<OrderNode> list, String listName){
        System.out.println("----------------------------");
        for(OrderNode order : list){
            System.out.printf("%s %d@%d\n", listName, order.quantity, order.price);
        }
        System.out.println("----------------------------");
    }

    private void addInOrder(LinkedList<OrderNode> list, OrderNode newNode, int comparator) {
        System.out.println("Adding in order");
        if(list.isEmpty()){
            list.add(newNode);
            System.out.println("Added in order " + newNode.price);
            return;
        }
        ListIterator<OrderNode> iterator = list.listIterator();
        while (iterator.hasNext()) {
            if ((iterator.next().price - newNode.price)  * comparator > 0) {
                iterator.previous();
                iterator.add(newNode);
                System.out.println("Added in order " + newNode.price);
                return;
            }
        }
    }

    private static class OrderNode {
        private int quantity;
        private final int price;

        public OrderNode(int quantity, int price) {
            this.quantity = quantity;
            this.price = price;
        }
    }
}


