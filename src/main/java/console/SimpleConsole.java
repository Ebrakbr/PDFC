package console;

import java.io.File;
import java.io.PrintWriter;

import com.inet.pdfc.PDFComparer;
import com.inet.pdfc.error.PdfcException;
import com.inet.pdfc.presenter.ConsolePresenter;
import com.inet.pdfc.results.ResultModel;

import util.SampleUtil;

/**
 * A sample for logger output.
 *
 * Expected 2 arguments, the path of the pdf files
 */
public class SimpleConsole {

    /**
     * A sample for logger output.
     * @param args Expected 2 arguments, the path of the pdf files
     */
    public static void main( String[] args ) {
        SampleUtil.filterServerPlugins();
        File[] files = getFileOfArguments( args );
        ConsolePresenter consolePresenter = new ConsolePresenter();
        
        // The presenter will write to the current log file by default. We redirect the output to the system console for this sample. 
        consolePresenter.setLogWriter( new PrintWriter( System.out ) );
        
		PDFComparer pdfComparer = new PDFComparer().addPresenter( consolePresenter );
        try ( ResultModel model = pdfComparer.compare( files[1], files[0] ) ){
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
    public static File[] getFileOfArguments(final String[] args){
        if (args == null || args.length != 2) {
            throw new IllegalArgumentException( "Usage: SimpleConsole <PDF-File1> <PDF-File2>" );
        }
        return new File[]{ SampleUtil.checkAndGetFile( args[0] ), SampleUtil.checkAndGetFile( args[1] )};
    }
}
