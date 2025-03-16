package com.example.dine_in_order_api.service.serviceImpl;

import com.example.dine_in_order_api.dto.responce.BillResponse;
import com.example.dine_in_order_api.enums.OrderStatus;
import com.example.dine_in_order_api.enums.TableStatus;
import com.example.dine_in_order_api.exception.NoBillFoundException;
import com.example.dine_in_order_api.mapper.BillMapper;
import com.example.dine_in_order_api.model.Bill;
import com.example.dine_in_order_api.model.Order;
import com.example.dine_in_order_api.model.RestaurantTable;
import com.example.dine_in_order_api.repository.BillRepository;
import com.example.dine_in_order_api.repository.OrderRepository;
import com.example.dine_in_order_api.repository.TableRepository;
import com.example.dine_in_order_api.service.BillService;
import com.example.dine_in_order_api.utility.BillGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final TableRepository tableRepository;
    private final OrderRepository orderRepository;
    private  final BillMapper billMapper;
    private final BillGenerator billGenerator;


    @Override
    public BillResponse createBill(long tableId) {

        RestaurantTable restaurantTable = tableRepository.findById(tableId)
                .orElseThrow(() -> new NoSuchElementException("Table not found !!"));

        List<Order> orderList = orderRepository.findByRestaurantTable(restaurantTable,OrderStatus.PAID);
        double totalAmount = orderList.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
        Bill bill = null;
        if(!orderList.isEmpty()) {
            bill = new Bill();
            bill.setOrders(orderList);
            bill.setTotalPayableAmount(totalAmount);
            billRepository.save(bill);
        }
        else{
            throw new NoSuchElementException(" No CartItem Selected !! ");
        }
        orderList.forEach(order -> order.setOrderStatus(OrderStatus.PAID));
        restaurantTable.setStatus(TableStatus.AVAILABLE);
        tableRepository.save(restaurantTable);
        orderRepository.saveAll(orderList);

        return billMapper.mapToBillResponse(bill);
    }

    @Override
    public BillResponse findById(long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new NoBillFoundException("No bill found with "+ billId +" id"));
        return billMapper.mapToBillResponse(bill);
    }

    @Override
    public byte[] findBillById(long billId) throws IOException {
        Bill billDetails = billRepository.findById(billId)
                .orElseThrow(() -> new NoBillFoundException("No bill found with "+ billId +" id"));

        Map<String,Object> bill = mapOfBillDerails(billDetails);

        System.out.println(bill);

        byte[] pdfBytes = billGenerator.generatePdf("billUI", bill);

        return pdfBytes;
    }

    private static Map<String,Object> mapOfBillDerails(Bill billDetails) {
        Map<String,Object> bill = new HashMap<>();

        bill.put("id", billDetails.getBillId());

        bill.put("restaurantName", billDetails.getOrders().getFirst()
                .getRestaurantTable()
                .getRestaurent().getName());

        bill.put("tableNo", billDetails.getOrders().getFirst()
                .getRestaurantTable().getTableNumber());

        bill.put("orders", // orders in one bill
                billDetails.getOrders().stream().map(order -> {
                          return Map.of("id", order.getOrderId(),
                                  "foodItems",order.getCartItems().stream().map(foodItem -> { // cartitems in one order
                                              return Map.of("name",foodItem.getFoodItem().getName(),
                                              "price",foodItem.getTotalPrice(),
                                              "quantity", foodItem.getQuantity()
                                              );
                                          }).toList(),
                                  "totalAmount",order.getTotalAmount()
                          );
                        }
                ).toList()
        );
        bill.put("totalAmount", billDetails.getTotalPayableAmount());

        /*
         * map of bill -> 1) bill id ,
         *                2) restaurantName ,
         *                3) table No
         *                4) list of orders -> (where inside contain
         *                                  1) orderId and
         *                                  2) List of cartItems -> ( in that detail of all cart item
         *                                                     1) name
         *                                                     2) price
         *                                                     3) quantity
         *                                                         ) -- one order can have multiple cartitems
         *                                 3) total amount of order
         *                                  ) -- one bill can have multiple orders
         *                5) bill total amount
         * */

        return bill;
    }
}

