import java.lang.ref.SoftReference;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.junit.Test;

import com.colbertlum.OrderService;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.Order;

public class OrderServiceTest {
    
    @Test
    public void shippingOrderToCompletedTest(){
        ArrayList<Order> orders = new ArrayList<Order>();
        Order order = new Order();
        order.setId("sses");
        order.setStatus(OrderService.STATUS_SHIPPING);
        orders.add(order);
        order.setMoveOutList(new ArrayList<SoftReference<MoveOut>>());

        new MoveOut()
    }

    // test newShipping order will represent correcly at repository's shipping orders
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
