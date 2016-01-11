import org.junit.Test;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 * Created by peter on 11/01/16.
 */
public class WekaTest {

    @Test
    public void converstion() {
        Attribute emg_0 = new Attribute("emg_0", 0);
        Attribute emg_1 = new Attribute("emg_1", 1);
        Instance row = new Instance(2);
        row.setValue(emg_0, 10);
        row.setValue(emg_1, 15);
        FastVector attInfo = new FastVector();
        attInfo.addElement(emg_0);
        attInfo.addElement(emg_1);
        new Instances("emg-data", attInfo, 1);
    }


}
