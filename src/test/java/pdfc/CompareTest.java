package pdfc;

import com.inet.logging.LogManager;
import com.inet.logging.Logger;
import com.inet.pdfc.PDFComparer;
import com.inet.pdfc.config.XMLProfile;
import com.inet.pdfc.error.PdfcException;
import com.inet.pdfc.generator.message.InfoData;
import com.inet.pdfc.results.ResultModel;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;


/**
 * A sample for writing JUnit test cases using PDFC
 */
public class CompareTest {

    private PDFComparer pdfComparer;

    @Before
    public void before() {


        pdfComparer = new PDFComparer();
    }

    @Test
    public void testDifferences() throws PdfcException, IOException {
        System.setProperty("log4j2.properties","D:\\My PDFC\\step2\\code\\test\\src\\main\\resources\\log4j2.xml");
        Logger log = LogManager.getLogger(LogExample.class.getName());
        String file1="D:\\My PDFC\\step2\\code\\test\\src\\main\\resources\\m1.pdf";
        String file2="D:\\My PDFC\\step2\\code\\test\\src\\main\\resources\\m2.pdf";
        String xmlfile="D:\\My PDFC\\step2\\code\\test\\src\\main\\resources\\Continuous_document_profil.xml";
        File example1 = new File( file1 );




        File example2 = new File(file2);
        File profil = new File( xmlfile );




        ResultModel result = new PDFComparer().setProfile( new XMLProfile(profil) ).compare( example1, example2 );
        InfoData comparisonParameters = result.getComparisonParameters();

        Assertions.assertEquals(11, result.getDifferencesCount( false ));
        Assertions.assertEquals(11, result.getDifferencesCount( true ));
        Assertions.assertEquals(3, comparisonParameters.getFirstPageCount());
        Assertions.assertEquals(3, comparisonParameters.getSecondPageCount());
        result.getDifferences(true).get(0);
    }
}
