import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import com.colbertlum.StockImputer;

public class StockImputerTest {
    
    @Test
    public void readingUpdateRuleCSV(){
        StockImputer stockImputer = new StockImputer(null, null);
        Double updateRuleMeasure = 0d;
        try {
            updateRuleMeasure = stockImputer.getUpdateRuleMeasure("default");
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        assertEquals(0.4d, updateRuleMeasure);
    }
}
