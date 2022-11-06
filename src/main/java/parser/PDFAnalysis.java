package parser;

import com.inet.pdfc.config.FilePdfSource;
import com.inet.pdfc.error.PdfcException;
import com.inet.pdfc.model.*;
import com.inet.pdfc.plugin.DocumentReader;
import util.SampleUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * A sample to show the internal PDF data structure
 * Expected 1 argument, the path of the PDF file
 */
public class PDFAnalysis {

    /**
     * Start the sample, to show the internal PDF data structure
     *
     * @param args Expected argument, the path of the PDF file
     */
    public static void main( String[] args ) {
        SampleUtil.filterServerPlugins();
        File file = getFileOfArguments( args );

        try ( Document document = DocumentReader.getInstance().readDocument( new FilePdfSource( file ) ) ){
            int index = 0;
            EnumerationProgress pages = document.getPages( null, index );
            while( pages.hasMoreElements() ){
                System.out.println( "\npage number = " + index++ );
                Page page = pages.nextElement();

                List<DrawableElement> list = page.getElementList().getList();
                for( DrawableElement drawableElement : list ) {
                    System.out.println( "Type = " + drawableElement.getType() + "\t\t" + drawableElement + "\t" + drawableElement.getClass());
                    switch( drawableElement.getType() ){
                        case Text:
                            TextElement textElement = (TextElement)drawableElement;
                            System.out.println( "textElement = " + textElement );
                            break;
                        case Shape:
                            ShapeElement shapeElement = (ShapeElement)drawableElement;
                            System.out.println( "shapeElement = " + shapeElement );
                            break;
                        case Image:
                            ImageElement imageElement = (ImageElement)drawableElement;
                            System.out.println( "imageElement = " + imageElement );
                            break;
                        default:
                            // There are more types like annotations or controls, add them if required	
                    }
                }
            }
            System.out.println( "number of pages = " + index );
        } catch( PdfcException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Get the PDF file to analyze
     *
     * @param args the arguments
     * @return the PDF file
     */
    public static File getFileOfArguments( final String[] args ) {
        if( args == null || args.length != 1 ) {
            throw new IllegalArgumentException( "Usage: PDFAnalysis <PDF-File>\nYour arguments: " + (args == null ? "no arguments" : Arrays.toString(args)) );
        }
        return SampleUtil.checkAndGetFile( args[0] );
    }
}
