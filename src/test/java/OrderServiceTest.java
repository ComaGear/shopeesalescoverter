import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.colbertlum.OrderRepository;
import com.colbertlum.OrderService;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.Order;

public class OrderServiceTest {
    
    
    private OrderRepository orderRepository = new OrderRepository();
    private OrderService orderService = new OrderService(orderRepository);

    @Test
    public void shippingOrderToCompletedTest(){
        ArrayList<Order> orders = new ArrayList<Order>();
        Order order = new Order();
        order.setId("sses");
        order.setStatus(OrderService.STATUS_SHIPPING);
        orders.add(order);
        order.setMoveOutList(new ArrayList<SoftReference<MoveOut>>());

        new MoveOut();
    }

    // test newShipping order will represent correcly at repository's shipping orders
    @Test
    public void newShippingOrderShouldExistedAtRepository(){
        ArrayList<Order> arrayList = new ArrayList<Order>();

        Order order = new Order();
        order.setId("www123");
        order.setStatus(OrderService.STATUS_SHIPPING);
        
        ArrayList<SoftReference<MoveOut>> softMoveOuts = new ArrayList<SoftReference<MoveOut>>();
        MoveOut moveOut = new MoveOut().setSku("123").setPrice(1).setPrice(1);
        softMoveOuts.add(new SoftReference<MoveOut>(moveOut));
        moveOut.setOrder(order);
        order.setMoveOutList(softMoveOuts);

        arrayList.add(order);

        ArrayList<MoveOut> moveOuts = new ArrayList<MoveOut>();
        moveOuts.add(moveOut);
        orderService.process(moveOuts);

        assertTrue(orderRepository.getShippingOrders().contains(order));
        
    }
    // test newShipping and repository on shipping order will report a temporary movement record

    // test newCompleted order will represent correcly at repository's completed orders
    // test newCompleted order will removed from repository's shipping orders
    // test newCompleted order will report a movement record at completed order stage

    // test newReturnFailedDelivery order will represent correcly at repository's ReturnAfterShipping orders
    // test newReturnFailedDelivery order will removed from repository's shipping orders

    // test newReturnOnceCompleted order will represent correcly at repository's ReturnAfterCompleted orders
    // test newReturnOnceCompleted order will removed from repository's shipping orders
    // test newReturnOnceCompleted order will removed from repository's completed orders
    // test newReturnOnceCompleted order will report a movement record at completed order stage

    // test newReturnFalledDelivery order will represent at repository's inReturn movements
    // test newReturnOnceCompleted order will represent at repository's inReturn movements
    // test all inReturn movement's received stage reporting Credit Note movement.
    // test all inReturn movement's particularly received stage reporting Credit Note movement for reason both return and damaged.
    // test all inReturn movement's damaged stage reporting Credit Note movement for reason damaged.
    // test all inReturn movement's lost stage reporting Credit Note movement for reason damaged.
    // test all inReturn movement's none stage will not reporting.

}
