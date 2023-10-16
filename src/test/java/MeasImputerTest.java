import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit.ApplicationTest;

import com.colbertlum.MeasImputer;
import com.colbertlum.SalesConverter;
import com.colbertlum.SalesImputer;
import com.colbertlum.ShopeeSalesConvertApplication;
import com.colbertlum.entity.Meas;

import javafx.stage.Stage;

public class MeasImputerTest extends ApplicationTest {
    
    @Test
    public void fillNameTest(){
        
        ArrayList<Meas> measList = ShopeeSalesConvertApplication.getMeasList();

        MeasImputer measImputer = new MeasImputer();

        measImputer.imputeNameField(measList);
    }

    @Test 
    public void obtainNewChildFromMutliChildShouldSuccess(){
        MeasImputer measImputer = new MeasImputer();
        String newChildSku = measImputer.createNewChildSku("22320010-a");
        assertEquals("22320010-d", newChildSku);
    }

    @Test 
    public void obtainNewChildFromOriginShouldSuccess(){
        MeasImputer measImputer = new MeasImputer();
        String newChildSku = measImputer.createNewChildSku("22320137");
        assertEquals("22320137-b", newChildSku);

        ArrayList<Meas> measList = measImputer.getMeasList();
        assertEquals("22320137-a", measList.get(469).getRelativeId());
    }

    @Override
    public void start(Stage stage) throws Exception {
        SalesImputer salesImputer = new SalesImputer(null, null);
        stage.setScene(salesImputer.getScene());
        stage.show();
    }
}
