package gluu.scim2.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.BeforeSuite;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

/**
 * Created by jgomer on 2017-06-09.
 * Base class for tests (as former BaseScimTest) but reads contents for properties from files instead of a having all JSON
 * content written in a single .properties file.
 */
public class BaseTest {

    private static final String FILE_PREFIX="file:";
    private static final Charset DEFAULT_CHARSET=Charset.forName("UTF-8");
    private static final String NEW_LINE=System.getProperty("line.separator");

    private Logger logger = LogManager.getLogger(getClass());

    private String decodeFileValue(String value){

        String decoded = value;
        if (value.startsWith(FILE_PREFIX)) {
            value = value.substring(FILE_PREFIX.length());    //remove the prefix
            try (BufferedReader bfr = Files.newBufferedReader(Paths.get(value), DEFAULT_CHARSET)) {     //create reader
                //appends every line after another
                decoded = bfr.lines().reduce("", (partial, next) -> partial + NEW_LINE + next);
                if (decoded.length()==0)
                    logger.warn("Key '{}' is empty", value);
            }
            catch (IOException e){
                logger.error(e.getMessage(), e);
                decoded=null;
            }
            //No need to close bfr: try-with-resources statements does it for me
        }
        return decoded;

    }

    @BeforeSuite
    public void initTestSuite(ITestContext context) throws Exception {

        logger.info("Invoked initTestSuite of '{}'", context.getCurrentXmlTest().getName());

        //Properties with the file: preffix will point to real .json files stored under src/test/resources folder
        String properties = context.getCurrentXmlTest().getParameter("file");
        Properties prop = new Properties();
        prop.load(Files.newBufferedReader(Paths.get(properties), DEFAULT_CHARSET));     //do not bother much about IO issues here. Ensure your files are accessible!

        Map<String, String> parameters = new Hashtable<>();
        //do not bother about empty keys... but
        //If a value is found null, this will throw a NPE since we are using a Hashtable
        prop.forEach((Object key, Object value) -> parameters.put(key.toString(), decodeFileValue(value.toString())));
        // Override test parameters
        context.getSuite().getXmlSuite().setParameters(parameters);
    }

}
