import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.colbertlum.MeasImputer;
import com.colbertlum.ShopeeSalesConvertApplication;
import com.colbertlum.entity.Meas;
import com.colbertlum.entity.UOM;

public class MeasImputerTest {
    
    @Test
    public void fillNameTest(){
        
        ArrayList<Meas> measList = ShopeeSalesConvertApplication.getMeasList();

        MeasImputer.imputeNameField(measList);
    }
}
