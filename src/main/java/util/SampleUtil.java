package util;

import com.inet.pdfc.PDFComparer;
import com.inet.pdfc.error.PresenterExceptionData;
import com.inet.pdfc.generator.model.DiffGroup;
import com.inet.pdfc.generator.model.Modification;
import com.inet.pdfc.results.ResultModel;
import com.inet.plugin.PluginFilter;
import com.inet.plugin.ServerPluginDescription;
import com.inet.plugin.ServerPluginManager;

import java.io.File;
import java.util.List;

/**
 * Utils for the sample classes
 */
public class SampleUtil {

    public static void filterServerPlugins(){
        ServerPluginManager.getInstance().setPluginFilter( new PluginFilter() {
            @Override
            public boolean accept( ServerPluginDescription plugin ) {
                return !plugin.getId().matches( "(?i)remotegui|webserver|authentication.*|.*setupwizard.*|theme|processbridge.*|embeddedwebsites|errorhandler|pdfcserver|command.*|configuration" );
            }
        });
    }

    /**
     * Returns a File object based on a string path
     * The file must not be null, must exist and must not be a directory
     *
     * @param file path to the file
     * @return The File object
     */
    public static File checkAndGetFile( final String file ) {
        if( file == null ) {
            throw new IllegalArgumentException( "The parameter is empty.\n parameter = " + file );
        }
        final File fileObject = new File( file );

        if( !fileObject.exists() ) {
            throw new IllegalArgumentException( "The file didn't exist.\n parameter = " + file );
        }
        if( fileObject.isDirectory() ) {
            throw new IllegalArgumentException( "The file is a folder and not a PDF file.\n parameter = " + file );
        }
        return fileObject;
    }

    /**
     * Show all modifications
     *
     * @param result the result of the comparision of the 2 PDF files
     */
    public static void showModifications( final ResultModel result ) {
        List<DiffGroup> differences = result.getDifferences( false );

        for( DiffGroup difference : differences ) {
            List<Modification> modifications = difference.getModifications();
            if( modifications != null ) {
                for( Modification modification : modifications ) {
                    System.out.println( "modification = " + modification );
                    modification.getModificationType();
                }
            }
        }
    }

    /**
     * Write the error for the presenter in the console
     * @param comparer the comparer after the comparison
     */
    public static void showPresenterError(final PDFComparer comparer){
        for( PresenterExceptionData presenterException: comparer.getPresenterExceptions() ) {
            System.err.println( presenterException );
        }
    }
}
