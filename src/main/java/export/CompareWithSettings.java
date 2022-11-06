package export;

import com.inet.pdfc.PDFComparer;
import com.inet.pdfc.config.DefaultSetting;
import com.inet.pdfc.config.Settings;
import com.inet.pdfc.error.PdfcException;
import com.inet.pdfc.generator.model.DiffGroup;
import com.inet.pdfc.presenter.DifferencesPDFPresenter;
import util.SampleUtil;

import java.awt.*;
import java.io.File;

/**
 * A sample for exporting the results of the comparison of 2 PDF Files to a PDF.
 * This sample modified the output.
 * Expects 2 arguments - the paths of the PDF files to be compared
 *
 */
public class CompareWithSettings {
    /**
     * Start the sample, that show how exporting the result of a comparison of 2 PDF Files to a PDF,
     * showing how to set the export path for this comparison report.
     *
     * @param args Expects 2 arguments: the paths of the 2 PDF files that will be compared,
     */
    public static void main( String[] args ) {
        SampleUtil.filterServerPlugins();
        File[] files = getFileOfArguments( args );

        //Used the current i-net PDFC configuration. If no configuration has been previously set then the default configuration will be used.
        DifferencesPDFPresenter differencesPDFPresenter = new DifferencesPDFPresenter( files[0].getParentFile() );
        DefaultSetting defaultSetting = new DefaultSetting();
        //Show Header/Footer
        defaultSetting.setEnabled( true, Settings.EXPORT.HEADER, Settings.EXPORT.FOOTER );
        //Don't add comments for the output
        defaultSetting.setEnabled( false, Settings.EXPORT.COMMENTS );

        //Set the hightlight color for Add/Remove differences to red
        defaultSetting.setColor( new Color( 255,0,0,40 ), DiffGroup.GroupType.AddedOrRemoved );
        //Invisible Diffs with the type modified. (Example the text color was changed)
        defaultSetting.setEnabled( false, DiffGroup.GroupType.Modified );

        try {
            PDFComparer pdfComparer = new PDFComparer().addPresenter( differencesPDFPresenter ).setSettings( defaultSetting );
            pdfComparer.compare( files[0], files[1] ).close();
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
        if( args == null || args.length != 2 ) {
            throw new IllegalArgumentException( "Usage: CompareWithSettings <PDF-File1> <PDF-File2>" );
        }
        return new File[] { SampleUtil.checkAndGetFile( args[0] ), SampleUtil.checkAndGetFile( args[1] ) };
    }
}
