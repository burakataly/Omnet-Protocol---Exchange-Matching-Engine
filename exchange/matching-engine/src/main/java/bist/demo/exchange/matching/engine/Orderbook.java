package bist.demo.exchange.matching.engine;

import bist.demo.exchange.common.Constants;

import java.util.LinkedList;
import java.util.ListIterator;

public class Orderbook {
    private final String name;
    private final LinkedList<OrderNode> buyOrderList;
    private final LinkedList<OrderNode> sellOrderList;
    private final LinkedList<OrderNode> matchList;

    public Orderbook(String name) {
        this.name = name;
        buyOrderList = new LinkedList<>();
        sellOrderList = new LinkedList<>();
        matchList = new LinkedList<>();
    }

    public void handleNewOrder(byte side, int quantity, int price) {
        OrderNode newOrder = new OrderNode(quantity, price);
        if(side == Constants.SIDE_BUY){
            matchTheOrder(sellOrderList, newOrder);
            if(newOrder.quantity > 0) addInOrder(buyOrderList, newOrder, -1);
        }
        else if(side == Constants.SIDE_SELL){
            matchTheOrder(buyOrderList, newOrder);
            if(newOrder.quantity > 0) addInOrder(sellOrderList, newOrder, 1);
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
                    newOrder.quantity = 0;
                    iterator.remove();
                }
                else if(order.quantity > newOrder.quantity){
                    order.quantity -= newOrder.quantity;
                    newOrder.quantity = 0;
                }
                else{
                    newOrder.quantity -= order.quantity;
                    iterator.remove();
                }
            }
        }
        if(quantity != newOrder.quantity){
            matchList.addFirst(new OrderNode(quantity - newOrder.quantity, newOrder.price));
        }
    }

    public void print() {
        System.out.printf("Orderbook: %s\n", name);
        System.out.println("Matches");
        printList(matchList, "Match");
        System.out.println("Buy Orders");
        printList(buyOrderList, "Buy");
        System.out.println("Sell Orders");
        printList(sellOrderList, "Sell");
    }

    private void printList(LinkedList<OrderNode> list, String listName){
        System.out.println("----------------------------");
        for(OrderNode order : list){
            System.out.printf("%s %d@%d\n", listName, order.quantity, order.price);
        }
        System.out.println("----------------------------");
    }

    private void addInOrder(LinkedList<OrderNode> list, OrderNode newNode, int comparator) {
        ListIterator<OrderNode> iterator = list.listIterator();
        while (iterator.hasNext()) {
            if ((iterator.next().price - newNode.price)  * comparator > 0) {
                iterator.previous();
                iterator.add(newNode);
                return;
            }
        }
        list.add(newNode);
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


