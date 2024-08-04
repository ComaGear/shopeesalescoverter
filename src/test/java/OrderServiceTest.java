import java.lang.ref.SoftReference;
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
}
