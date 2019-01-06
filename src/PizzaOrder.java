import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

class PizzaOrder {

    public static void main(String[] args) {

        Product product1 = new Product("пицца с томатом и зеленью");
        Product product2 = new Product("пицца с грибами");
        Product product3 = new Product("мексиканская пицца");

        /**
         * Коллекция списка продуктов
         */
        List<Product> productList = new ArrayList<>();

        productList.add(product1);
        productList.add(product2);
        productList.add(product3);

        /**
         * Оформить заказ
         */
        Order order1 = new Order(productList);

        BlockingQueue<Order>orderC1 = new ArrayBlockingQueue<>(3,true); // клиент
        BlockingQueue<Order>orderC2 = new ArrayBlockingQueue<>(3,true); // офицант
        BlockingQueue<Order>orderC3 = new ArrayBlockingQueue<>(3,true); // повар

        /**
         * Зарегистрировать заказ
         */
        Customer c1 = new Customer(orderC1,order1,orderC3);

        Waiter w1 = new Waiter(orderC1,orderC2);

        Cook cook1 = new Cook(orderC2,orderC3);


        c1.start();
        w1.start();
        cook1.start();


    }

}

class Product {
    private String nameProduct;

    public Product(String name) {
        this.nameProduct = name;
    }

    public void setNameProduct(String product){
        this.nameProduct = product;
    }

    public String getProductName(){
        return this.nameProduct;
    }

    @Override
    public String toString() {
        return "Product{" +
                "nameProduct='" + nameProduct + '\'' +
                '}';
    }

}

class Order {

    private List<Product>listProduct;

    public Order(List<Product> products) {
        this.listProduct = products;
    }


    public void getListProducts() {

        if (!this.listProduct.isEmpty()){

            for (Product product: listProduct){
                System.out.println(product.getProductName());
            }

        }
        else {
            //throw new Exception("Order empty");
            System.out.println("Order List is Empty");
        }

    }


    public int getSizeOrder(){

        return this.listProduct.size();
    }


    @Override
    public String toString() {
        return "Order{" +
                "listProduct=" + listProduct +
                '}';
    }

}

class Customer extends Thread{


    private Order order;
    private BlockingQueue<Order>customerQueue;
    private BlockingQueue<Order>cookQueue;


    public Customer(BlockingQueue<Order>cq, Order order1, BlockingQueue<Order>cookQ){
        this.order = order1;
        this.cookQueue = cookQ;
        this.customerQueue = cq;

    }

    @Override
    public void run() {

            try {

                customerQueue.put(order);

                order = cookQueue.take();

                if (order.getSizeOrder() > 0){
                    System.out.println("Заберите ваш заказ: ");
                    order.getListProducts();
                }
                else {
                    System.out.println("Ваш заказ еще не готов");
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

    }
}

class Waiter extends Thread{

    private Order waiterOrders;

    private BlockingQueue<Order> customerOrder;
    private BlockingQueue<Order>waiterOrder;

    public Waiter(BlockingQueue<Order>cus, BlockingQueue<Order>wt){
        this.customerOrder = cus;
        this.waiterOrder = wt;
    }

    @Override
    public void run() {

        try {
            waiterOrders = customerOrder.take();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            waiterOrder.put(waiterOrders);


            System.out.println("Заказ получен официантом..");


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

class Cook extends Thread{

    private Order cookOrderList;

    private BlockingQueue<Order>waiterOrder;
    private BlockingQueue<Order>cookOrder;


    public Cook(BlockingQueue<Order>waiter, BlockingQueue<Order>cook){
        this.waiterOrder = waiter;
        this.cookOrder = cook;

    }
    @Override
    public void run() {
        try {
            cookOrderList = waiterOrder.take();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Ваш заказ передается повару....");
            sleep(1000);
            cookOrder.put(cookOrderList);
            System.out.println("Ваш заказ отдан повару!");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
