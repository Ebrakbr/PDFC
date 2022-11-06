package print;

import java.io.File;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import com.inet.pdfc.PDFComparer;
import com.inet.pdfc.error.PdfcException;
import com.inet.pdfc.presenter.DifferencesPrintPresenter;
import com.inet.pdfc.results.ResultModel;

import util.SampleUtil;

/**
 * A simple Sample for print the comparing between 2 PDF Files
 *
 * Expected 2 arguments, the path of the pdf files
 *
 */
public class SimpleCompareAndPrint {

    /**
     * A simple Sample for print the comparing between 2 PDF Files
     * @param args Expected 2 arguments, the path of the pdf files
     */
    public static void main( String[] args ) {
        SampleUtil.filterServerPlugins();
        File[] files = getFileOfArguments( args );

        //Used the current i-net PDFC configuration. If no configuration has been previously set then the default configuration will be used.

        //set up Printer service
        PrintService printService = PrintServiceLookup.lookupDefaultPrintService(); //use the default printservice, for testing purpose it makes sense to use a virtual printer!
        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();

        PDFComparer pdfComparer = new PDFComparer().addPresenter( new DifferencesPrintPresenter( printService, attributes ) );
        try ( ResultModel result = pdfComparer.compare( files[1], files[0] ) ) {
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
            throw new IllegalArgumentException( "Usage: SimpleCompareAndPrint <PDF-File1> <PDF-File2>" );
        }
        return new File[]{ SampleUtil.checkAndGetFile( args[0] ), SampleUtil.checkAndGetFile( args[1] )};
    }
}
