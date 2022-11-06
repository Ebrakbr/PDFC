package example;

import com.inet.pdfc.PDFComparer;
import com.inet.pdfc.config.*;
import com.inet.pdfc.error.PdfcException;
import com.inet.pdfc.plugin.configurations.DefaultFilterNames;
import com.inet.pdfc.presenter.DifferencesPDFPresenter;
import com.inet.pdfc.presenter.ReportPresenter;
import com.inet.pdfc.results.ResultModel;

import util.SampleUtil;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Easy example with own profile and setting usage
 */
public class ExampleSDK {

    /**
     * Easy example with own profile and setting usage
     * @param args a output directory
     * @throws URISyntaxException 
     */
    public static void main( String[] args ) throws URISyntaxException {
        SampleUtil.filterServerPlugins();
        ExampleSDK exampleSDK = new ExampleSDK();
        exampleSDK.startCompare( getFileOfArguments( args ) );
    }

    private IProfile comparisonSettings;
    private Settings visibilitySettings;

    /**
     * Initialize the sample variables
     */
    public ExampleSDK(){
        comparisonSettings = new DefaultProfile(  );
        comparisonSettings.putValue( PDFCProperty.CONTINUOUS_COMPARE, "CONTINUOUS" );
        comparisonSettings.putValue( PDFCProperty.COMPARE_TYPES, CompareType.TEXT.name() );
        comparisonSettings.putValue( PDFCProperty.FILTERS, DefaultFilterNames.INVISIBLEELEMENTS+","
                        +DefaultFilterNames.HEADERFOOTER
                        +",TEXTTRANSFORM,SOLVEFALSEREPLACE" );
        //fine tuning for the result
        comparisonSettings.putValue( "FIXED_HEADER_SIZE", "0" );
        comparisonSettings.putValue( "FIXED_FOOTER_SIZE", "30" );

        visibilitySettings = new DefaultSetting();
        //Remove the comments
        visibilitySettings.setEnabled( false, Settings.EXPORT.COMMENTS );
    }

    /**
     * Start the comparison with the "i-net_PDFC_-_Command_Line_Access document"
     * @param fileDirectory output directory
     * @throws URISyntaxException 
     */
    public void startCompare(final File fileDirectory) throws URISyntaxException{
        //Used the current i-net PDFC configuration. If no configuration has been previously set then the default configuration will be used.
        DifferencesPDFPresenter differencesPDFPresenter = new DifferencesPDFPresenter( fileDirectory );
        ReportPresenter reportPresenter = new ReportPresenter(true, true, "pdf", fileDirectory, true );
        PDFComparer pdfComparer = new PDFComparer().setSettings( visibilitySettings )
                                                   .setProfile( comparisonSettings )
                                                   .addPresenter( differencesPDFPresenter )
                                                   .addPresenter( reportPresenter );
        
        File file1 = new File(getClass().getResource( "/i-net_PDFC_-_Command_Line_Access_4-3.pdf" ).toURI());
        File file2 = new File(getClass().getResource( "/i-net_PDFC_-_Command_Line_Access_5-0.pdf" ).toURI());
        
        try ( ResultModel result = pdfComparer.compare( file1, file2 ) ) {
            SampleUtil.showPresenterError( pdfComparer );
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
    public static File getFileOfArguments(final String[] args){
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException( "Usage: ExampleSDK <PDF-Directory-Output>" );
        }
        if( args[0] == null ) {
            throw new IllegalArgumentException( "The parameter is empty.\n parameter = " + args[0] );
        }
        final File fileObject = new File( args[0] );

        if( !fileObject.exists() ) {
            throw new IllegalArgumentException( "The file didn't exist.\n parameter = " + args[0] );
        }
        if( !fileObject.isDirectory() ) {
            throw new IllegalArgumentException( "The file is not a folder.\n parameter = " + fileObject );
        }
        return fileObject;
    }
}
