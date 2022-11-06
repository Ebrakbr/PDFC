package differences;

import com.inet.pdfc.PDFComparer;
import com.inet.pdfc.error.PdfcException;
import com.inet.pdfc.results.ResultModel;
import util.SampleUtil;

import java.io.File;

/**
 *  A sample to show the difference/changes beetween 2 pdf files.
 *
 *  Expected 2 arguments, the path of the pdf files
 *
 */
public class NumOfDifferences {

    /**
     * A sample to show the difference/changes beetween 2 pdf files.
     * @param args 2 arguments, the path of the pdf files
     */
    public static void main( String[] args ) {
        SampleUtil.filterServerPlugins();
        File[] files = getFileOfArguments( args );
        PDFComparer pdfComparer = new PDFComparer();

        try( ResultModel result = pdfComparer.compare( files[0], files[1] ) ) {
            int differences = result.getDifferencesCount( false );
            System.out.println( "differences = " + differences );
        } catch( PdfcException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Get 2 Files back, that was checked
     *
     * @param args the arguments
     * @return 2 Files
     */
    public static File[] getFileOfArguments(final String[] args){
        if (args == null || args.length != 2) {
            throw new IllegalArgumentException( "Usage: NumOfDifferences <PDF-File1> <PDF-File2>" );
        }
        return new File[]{ SampleUtil.checkAndGetFile( args[0] ), SampleUtil.checkAndGetFile( args[1] )};
    }
}
