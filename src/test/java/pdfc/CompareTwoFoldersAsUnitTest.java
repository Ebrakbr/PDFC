/*
  i-net software provides programming examples for illustration only, without warranty
  either expressed or implied, including, but not limited to, the implied warranties
  of merchantability and/or fitness for a particular purpose. This programming example
  assumes that you are familiar with the programming language being demonstrated and
  the tools used to create and debug procedures. i-net software support professionals
  can help explain the functionality of a particular procedure, but they will not modify
  these examples to provide added functionality or construct procedures to meet your
  specific needs.
  © i-net software 2011
*/
package pdfc;

import com.inet.config.Configuration;
import com.inet.config.ConfigurationManager;
import com.inet.logging.LogManager;
import com.inet.logging.Logger;
import com.inet.pdfc.PDFComparer;
import com.inet.pdfc.config.DefaultProfile;
import com.inet.pdfc.config.IProfile;
import com.inet.pdfc.error.PdfcException;
import com.inet.pdfc.generator.InvalidLicenseException;
import com.inet.pdfc.plugin.persistence.ProfilePersistence;
import com.inet.pdfc.plugin.persistence.ProfilePersistenceManager;
import com.inet.pdfc.results.ResultModel;
import com.inet.plugin.ServerPluginManager;
import org.junit.Assert;
import org.junit.BeforeClass;
//import org.junit.Test;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import java.util.List;


/**
 * This sample demonstrates how you can use JUnit to compare two folders
 * containing PDF files with pairwise identical names.
 *
 * This sample runs as a parameterized JUnit test for each PDF file.
 *
 * <b>NOTE:<b> This sample implements a sequential comparison of the files.
 * To improve the overall performance the comparisons can be parallelized.
 * Parallelization is implemented in the comparison back end but requires
 * such feature in the reference JUnit version as well.
 */
//@RunWith(Parameterized.class)
public class CompareTwoFoldersAsUnitTest {

    // INPUT PARAMETERS (by environment variables of the VM)

    // The root folder of the PDF files that should be compared.
    // It must be set with the -DcurrentFolder=... java option
    private static final String SOURCE      = System.getProperty( "currentFolder" );

    // The root folder of the reference PDF files.
    // It must be set with the -DreferenceFolder=... java option
    private static final String REFERENCE   = System.getProperty( "referenceFolder" );

    // An optional name of the profile to be used. This requires that the profile is
    // accessible to the user account with runs this class. If the profile name
    // cannot be resolved, the default 'continuous document' profile will be used
    private static final String PROFILE     = System.getProperty( "pdfcProfile" );

    // RUNTIME PARAMETERS per test case

    // the configuration containing various settings for i-net PDFC
    private static IProfile profile;

    // the PDF file, that should be compared with a reference file
    private File currentPdfFile = new File("D:\\My PDFC\\step2\\code\\test\\src\\main\\resources\\Example1.pdf") ;

    // the reference file to compare with
    private File referencePdfFile = new File("D:\\My PDFC\\step2\\code\\test\\src\\main\\resources\\Example2.pdf") ;


    /**
     * The constructor sets the current PDF file that should be compared with the specified reference PDF file.
     //* @param referencePdf the reference PDF file
     // * @param currentPdf the PDF file that should be compared
     */
   /* public CompareTwoFoldersAsUnitTest(File referencePdf, File currentPdf) {
       // currentPdfFile = currentPdf;
       // referencePdfFile = referencePdf;
    }*/
    public CompareTwoFoldersAsUnitTest() {


        // currentPdfFile = currentPdf;
        // referencePdfFile = referencePdf;
    }

    /**
     * Initialize the test by enabling console output to the logger and setting the log level to display some informations.
     * These changes to the logger will be reseted.
     * In addition the create PNG image settings of the PDFC configuration will be disabled.
     *
     */
    @BeforeClass
    public static void init() {

        // Logger log = LogManager.getLogger(LogExample.class.getName());
        // Get the profiles manger to check for available profiles
        profile = new DefaultProfile();
        if( PROFILE != null ) {
            try {
                // Check the PUBLIC profiles for whether the profile name can be resolved
                ProfilePersistenceManager profileManager = ServerPluginManager.getInstance().getSingleInstance( ProfilePersistenceManager.class );
                ProfilePersistence profileReference = profileManager.getUserProfile(null, PROFILE);
                if( profileReference != null ) {
                    profile = profileReference.getProfile();
                }
            } catch (IOException e) {
                LogManager.getApplicationLogger().error(e);
            }
        }


    }

    /**
     * Creates a collection that contains a mapping for all PDF files in the source folder with their
     * corresponding reference PDF files.
     * This test will be executed for each entry in this collection.
     * @return  a list with all source PDF files an their corresponding reference files.
     */
    @SuppressWarnings("unused")
    @Parameters
    public static Collection<Object[]> data() {
        // Call one of the PDFC public API entry points to start the plugin framework
        List<Object[]> list = new java.util.ArrayList<Object[]>();
        ConfigurationManager configManager = ConfigurationManager.getInstance();
        Configuration config = configManager.get( 1, "i-net PDFC" );
        if( config != null ) {
            configManager.setCurrent( config );
        }
        new PDFComparer();



        // the source directory must be set
        assertTrue( "Reference directory not defined", REFERENCE != null );

        File refDir = new File( REFERENCE );
        assertTrue( "Reference directory " + REFERENCE + " is not a directory ", refDir.isDirectory() );

        assertTrue( "Source directory not defined", SOURCE != null );

        File sourceDir = new File( SOURCE );
        assertTrue( "Source directory " + SOURCE + " is not a directory ", sourceDir.isDirectory() );

        // add all founded PDF files in the source directory to the list
        LogManager.getApplicationLogger().status("Scanning '" + REFERENCE + "' and '" + SOURCE + "' for files to compare");
        list.addAll( preparePaths( refDir, sourceDir ) );


        assertTrue( "There are no PDF files in the source directory at " + SOURCE, list.size() != 0 );

        return list;
    }

    /**
     * Makes recursive search of PDF files in current directory and in it's sub directories.
     * @param sourceDir  the current directory to look for PDF files
     * @return  list of all found PDF files
     */
    private static List<Object[]> preparePaths( File refDir, File sourceDir ) {
        List<Object[]> paramList = new ArrayList<Object[]>();
        // get all files using the filter
        File[] pdfFiles = sourceDir.listFiles( f -> f.isFile() && f.getName().endsWith( ".pdf" ) );
        for( File refFile : pdfFiles ) {
            // check for file to compare to
            File sourceFile = new File( refDir, refFile.getName() );
            if( sourceFile.exists() ) {
                paramList.add( new File[]{ refFile, sourceFile } );
            }
        }

        // parse the source sub directories
        File[] subDirs = sourceDir.listFiles( File::isDirectory );
        for( File refSubDir : subDirs ) {
            File sourceSubDir = new File( refDir, refSubDir.getName() );
            if( sourceSubDir.isDirectory() ) {
                paramList.addAll( preparePaths( refSubDir, sourceSubDir ) );
            }
        }
        return paramList;
    }

    /**
     * Compares the current PDF file with it's reference file and checks if there are any differences.
     * @throws IOException if the canonical path of the current PDF file can not be obtained
     * @throws PdfcException in case of errors while reading the documents
     * @throws InvalidLicenseException the current license is invalid
     */
    @Test
    public void compare() throws IOException, InvalidLicenseException, PdfcException, RuntimeException {


        // compare the current PDF file with it's reference
        PDFComparer comparer = new PDFComparer();
        comparer.setProfile( profile );
        // PRESENTERS, e.g. to export a PDF file result, can be appended here

        try( ResultModel result = comparer.compare( referencePdfFile, currentPdfFile ) ){
            // NOTE: ResultModel is a closable to free the cached resources as soon as possible by closing the model instance
            int differenceCount = result.getDifferencesCount( false );
            Assertions.assertEquals(10, differenceCount, "Differences found in " + currentPdfFile.getCanonicalPath());


        }
    }
}
