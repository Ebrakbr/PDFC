package configuration;

import java.io.File;
import java.util.InvalidPropertiesFormatException;

import com.inet.pdfc.PDFComparer;
import com.inet.pdfc.config.IProfile;
import com.inet.pdfc.config.XMLProfile;
import com.inet.pdfc.error.PdfcException;
import com.inet.pdfc.presenter.ReportPresenter;
import com.inet.pdfc.results.ResultModel;

import util.SampleUtil;

/**
 * A sample to show, how use a PDFC-Config XML-File.
 * Expects 3 arguments - the first 2 arguments for the paths of the PDF files to be compared and the last one
 * for the XML config file.
 */
public class UseXMLConfiguration {

    /**
     * Start the sample, that show, how use a PDFC-Config XML-File.
     *
     * @param args Expected 3 arguments, the first 2 arguments for the path of the PDF files and the last one
     *             for the XML config file.
     */
    public static void main( String[] args ) {
        SampleUtil.filterServerPlugins();
        File[] files = getFileOfArguments( args );
        IProfile profile = null;
        try {
            profile = new XMLProfile( files[2] );
        } catch( InvalidPropertiesFormatException e ) {
            System.out.println( "The file = " + files[2] + " is not a correct XML-Configuration File" );
            e.printStackTrace();
        }

        PDFComparer pdfComparer = new PDFComparer()
        		.setProfile( profile )
        		.addPresenter( new ReportPresenter( false, true, "pdf", files[0].getParentFile(), true ) );
        try ( ResultModel result = pdfComparer.compare( files[0], files[1] ) ){
            SampleUtil.showPresenterError( pdfComparer );
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
        if( args == null || args.length != 3 ) {
            throw new IllegalArgumentException(
                            "Usage: UseXMLConfiguration <PDF-File1> <PDF-File2> <XML-Configuration-File>" );
        }
        return new File[] { SampleUtil.checkAndGetFile( args[0] ), SampleUtil.checkAndGetFile( args[1] ), SampleUtil.checkAndGetFile( args[2] ) };
    }
}
