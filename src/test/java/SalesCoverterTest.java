import org.junit.Test;

import com.colbertlum.SalesConverter;
import com.colbertlum.ShopeeSalesConvertApplication;

public class SalesCoverterTest {
    
    @Test
    public void convertMoveOut(){
        ShopeeSalesConvertApplication shopeeSalesConvertApplication = new ShopeeSalesConvertApplication();
        SalesConverter salesConverter = new SalesConverter(shopeeSalesConvertApplication.getMoveOuts(), ShopeeSalesConvertApplication.getMeasList());
        salesConverter.process();

    }
}
