package example;

import com.inet.pdfc.PDFComparer;
import com.inet.pdfc.config.*;
import com.inet.pdfc.error.PdfcException;
import com.inet.pdfc.presenter.DifferencesPDFPresenter;
import com.inet.pdfc.results.ResultModel;

import util.SampleUtil;

import java.io.File;
import java.net.URISyntaxException;
import java.util.InvalidPropertiesFormatException;

/**
 * Example for OCR
 *
 * How do work with a profile file
 * Work with an other parser (multiple image file)
 * Work with OCR
 *
 * Information about the additionally required tesseract program
 * can be found at https://docs.inetsoftware.de/pdfc/help/Tesseractinstalled
 */
public class ExampleOCR {

    private PDFComparer pdfComparer;

    /**
     * Easy example with own profile and setting usage
     * @param args a output directory
     * @throws URISyntaxException 
     */
    public static void main( String[] args ) throws URISyntaxException {
        SampleUtil.filterServerPlugins();
        ExampleOCR exampleOCR = new ExampleOCR();
        exampleOCR.startCompare( getFileOfArguments( args ) );
    }

    private IProfile comparisonSettings;

    /**
     * Initialize the sample variables
     */
    public ExampleOCR() {
        try {
            pdfComparer = new PDFComparer();
            comparisonSettings = new XMLProfile( new File( getClass().getResource( "/Profile_Custom_OCR.xml" ).toURI()) );
        } catch( InvalidPropertiesFormatException e ) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
    }

    /**
     * Get 2 Files back, that was checked
     * @param args the arguments
     * @return 2 Files
     */
    public static File getFileOfArguments( final String[] args ) {
        if( args == null || args.length != 1 ) {
            throw new IllegalArgumentException( "Usage: ExampleOCR <PDF-Directory-Output>" );
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

    /**
     * Start the comparison with the "i-net_PDFC_-_Command_Line_Access document"
     * @param fileDirectory output directory
     * @throws URISyntaxException 
     */
    public void startCompare( final File fileDirectory ) throws URISyntaxException {
        //Used the current i-net PDFC configuration. If no configuration has been previously set then the default configuration will be used.
        DifferencesPDFPresenter differencesPDFPresenter = new DifferencesPDFPresenter( fileDirectory );
        if( comparisonSettings != null ) {
            pdfComparer.setProfile( comparisonSettings );
        }
        pdfComparer.addPresenter( differencesPDFPresenter );

        File file1 = new File( getClass().getResource( "/Optical-Character-Recognition.pdf" ).toURI() );
        File file2 = new File( getClass().getResource( "/Optical-Character-Recognition.zip" ).toURI() );
        
        try ( ResultModel result = pdfComparer.compare( file1, file2 ) ) {
            //Necessary if Tesseract has not been specified in the PATH.
            //or if you want to change the folders of the language files.
            //Remove the following comments and edit the path for Tesseract and/or Tesseract language files
            //Configuration current = ConfigurationManager.getInstance().getCurrent();
            //current.put( "tesseract.path", "C:\\Program Files\\Tesseract-OCR\\tesseract.exe" );
            //current.put( "tesseract.path.language", "C:\\Program Files\\Tesseract-OCR\\tessdata" );

            SampleUtil.showPresenterError( pdfComparer );
        } catch( PdfcException e ) {
            e.printStackTrace();
        }
    }
}
