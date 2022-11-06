package configuration;

import com.inet.pdfc.PDFComparer;
import com.inet.pdfc.config.*;
import com.inet.pdfc.error.PdfcException;
import com.inet.pdfc.results.ResultModel;

import util.SampleUtil;

import java.io.File;

/**
 * A simple sample for using regular expressions for filtering which texts are to be compared.
 * Expects 2 arguments - the paths of the PDF files
 */
public class UseRegex {

    /**
     * Start the sample, that show how using regular expressions for filtering which texts are to be compared.
     *
     * @param args Expected 2 arguments, the path of the PDF files
     */
    public static void main( String[] args ) {
        SampleUtil.filterServerPlugins();
        File[] files = getFileOfArguments( args );
        PDFComparer pdfComparer = new PDFComparer();

        System.out.println( "\nFiltered " );
        IProfile profile = new DefaultProfile();

        profile.putValue( PDFCProperty.FILTER_PATTERNS, ""
                        //for removing all numbers that are not in a text
                        + "\\s\\d+$|regexp|active\n"
                        + "^\\d+\\s|regexp|active\n"
                        + "\\s\\d+\\s|regexp|active\n"
                        + "^\\d+$|regexp|active\n"
                        //filtered date in format YYYY mm dd and dd mm YYYY
                        + "((19|20)\\d\\d([- /.])(0[1-9]|1[012])([- /.])(0[1-9]|[12][0-9]|3[01]))|regexp|active\n"
                        + "((0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d)|regexp|active\n"
                        //filtered length unit
                        + "\\s(mm|cm|dm|m|km)|regexp|active\n"
        );
        profile.putValue( PDFCProperty.FILTERS, "REGEXP" );

        try ( ResultModel result = pdfComparer.setProfile( profile ).compare( files[0], files[1] ) ){
            SampleUtil.showModifications( result );
        } catch( PdfcException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Get 2 files that are to be checked for comparisons
     *
     * @param args the arguments
     * @return 2 files to compare
     */
    public static File[] getFileOfArguments( final String[] args ) {
        if( args == null || args.length != 2 ) {
            throw new IllegalArgumentException( "Usage: UseRegex <PDF-File1> <PDF-File2>" );
        }
        return new File[] { SampleUtil.checkAndGetFile( args[0] ), SampleUtil.checkAndGetFile( args[1] ) };
    }
}
