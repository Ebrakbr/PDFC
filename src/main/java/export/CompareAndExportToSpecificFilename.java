package export;

import com.inet.pdfc.PDFComparer;
import com.inet.pdfc.error.PdfcException;
import com.inet.pdfc.presenter.DifferencesPDFPresenter;
import util.SampleUtil;

import java.io.*;

/**
 * A Sample for export to pdf file the comparing between 2 PDF Files,
 * for the case to change the export path.
 *
 * Expected 3 arguments, the path of the 2 pdf files that will be compared.
 * At least arguments the path for the export file. If no export file exist,
 * it will be create a new file.
 *
 * Similar to ReportingToSpecificFilename
 */
public class CompareAndExportToSpecificFilename {

    public static void main( String[] args ) {
        SampleUtil.filterServerPlugins();
        File[] files = getFileOfArguments( args );
        File exportFile = checkAndCreateFile( args[2] );

        //Used the current i-net PDFC configuration. If no configuration has been previously set then the default configuration will be used.
        DifferencesPDFPresenter differencesPDFPresenter = new DifferencesPDFPresenter( exportFile, false );
        PDFComparer pdfComparer = new PDFComparer().addPresenter( new DifferencesPDFPresenter( files[0].getParentFile() ) ).addPresenter( differencesPDFPresenter );
        try {
            pdfComparer.compare( files[1], files[0] );
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
        if (args == null || args.length != 3  ) {
            throw new IllegalArgumentException( "Usage: CompareAndExportToSpecificFilename <PDF-File1> <PDF-File2> <PDF-File-Output>" );
        }
        return new File[]{ SampleUtil.checkAndGetFile( args[0] ), SampleUtil.checkAndGetFile( args[1] )};
    }

    /**
     * Returns a File object based on a string path
     * The file must not be null, must exist and must not be a directory
     *
     * @param file path to the file
     * @return The File object
     */
    public static File checkAndCreateFile( final String file){
        final File fileObject = new File( file );

        try {
            fileObject.createNewFile();
        } catch( IOException e ) {
            e.printStackTrace();
            throw new IllegalArgumentException( "the export file can not will create" );
        }

        if( fileObject.isDirectory()){
            throw new IllegalArgumentException( "The file is a folder and not a pdf file.\n parameter = " + file );
        }

        return  fileObject;
    }
}
