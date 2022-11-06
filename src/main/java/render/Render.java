package render;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.inet.pdfc.PDFComparer;
import com.inet.pdfc.error.PdfcException;
import com.inet.pdfc.generator.model.DiffGroup;
import com.inet.pdfc.generator.model.Modification;
import com.inet.pdfc.model.Document;
import com.inet.pdfc.model.EnumerationProgress;
import com.inet.pdfc.model.PagedElement;
import com.inet.pdfc.plugin.DocumentReader;
import com.inet.pdfc.results.ResultModel;
import com.inet.pdfc.results.ResultPage;

import util.SampleUtil;

/**
 * A sample to show the PDF render function with a simple function for displayer markers where differences are detected.
 * Expected 2 arguments, the path of the PDF files
 */
public class Render {

    private JFrame frame;

    /**
     * Start the sample, to show the PDF render function with a simple function for displayer markers
     * where differences are detected.
     *
     * @param args Expected 2 arguments, the path of the PDF files
     */
    public static void main( String[] args ) {
        SampleUtil.filterServerPlugins();
        File[] files = getFileOfArguments( args );
        new Render( files ).show();
    }

    /**
     * For initialization.
     *
     * @param files Expected 2 PDF files
     */
    public Render( final File[] files ) {
        PDFComparer pdfComparer = new PDFComparer();
        try ( ResultModel compare = pdfComparer.compare( files[0], files[1] ) ){
            ResultPage page = compare.getPage( 0, true );

            frame = new JFrame();
            frame.setTitle( "PDF Difference" );
            frame.setSize( page.getWidth(), page.getHeight() );
            frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            frame.add( new PDFViewer( compare ) );
        } catch( PdfcException e ) {
            e.printStackTrace();
        }
    }

    /**
     * For show the gui frame
     */
    public void show() {
        frame.setVisible( true );
    }

    /**
     * To show one PDF file.
     * Every click goes to the next page,
     * if no next page exists, it returns to page 1
     * Every change is marked with a blue transparent color
     * over the line.
     */
    public class PDFViewer extends JComponent {

        private int currentPageIndex = 0;
        private       ResultModel compare;
        private final int         maxPageNumber;

        /**
         * To show one PDF file.
         * Every click goes to the next page,
         * if no next page exists, it returns to page 1
         * Every change is marked with a blue transparent color
         * over the line.
         *
         * @param compare A comparer between 2 PDF files
         * @throws PdfcException
         */
        public PDFViewer( final ResultModel compare ) throws PdfcException {
            this.compare = compare;

            try ( Document document = DocumentReader.getInstance().readDocument( compare.getComparisonParameters().getFirstFile() ) ){
				EnumerationProgress pages = document.getPages( null, 0 );
	            int index = 0;
	            while( pages.hasMoreElements() ){
	                ++index;
	                pages.nextElement();
	            }
	            maxPageNumber = index;
            }

            addMouseListener( new MouseAdapter() {
                /**
                 * Every click goes to the next page,
                 * if no next page exists, it returns to page 1
                 * @param e mouse event
                 */
                @Override
                public void mouseClicked( MouseEvent e ) {
                    ++currentPageIndex;
                    if( maxPageNumber <= currentPageIndex ) {
                        currentPageIndex = 0;
                    }
                    repaint();
                }
            } );
        }

        @Override
        public void paint( Graphics g ) {
            super.paint( g );
            Graphics2D g2d = (Graphics2D)g;

            ResultPage page = compare.getPage( currentPageIndex, true );
            //draw the PDF file, alternative: page.renderPage(1.0, g2d  );
            BufferedImage pageImage = page.getPageImage( 1.0 );
            g2d.drawImage( pageImage, 0, 0, null );

            //for highlight the differences lines
            g2d.setColor( new Color( 0, 40, 255, 40 ) ); //transparent blue
            List<DiffGroup> differences = compare.getDifferences( false );
            for( DiffGroup difference : differences ) {
                if( hasChangesForThisPage( difference.getModifications() ) ) {
                    Rectangle bounds = difference.getBounds( true );
                    g2d.fillRect( bounds.x, bounds.y - currentPageIndex * page.getHeight(), page.getWidth(),
                                  bounds.height );
                }
            }
        }

        /**
         * Check the modifications found for the current page.
         *
         * @param modifications a list with modifications
         * @return true the modification is for the current page, false if it is no modification for the current page
         */
        private boolean hasChangesForThisPage( final List<Modification> modifications ) {
            if( modifications == null ) {
                return false;
            }

            for( Modification modification : modifications ) {
                List<PagedElement> affectedElements = modification.getAffectedElements( true );
                for( PagedElement affectedElement : affectedElements ) {
                    int pageIndex = affectedElement.getPageIndex();
                    if( pageIndex == currentPageIndex ) {
                        return true;
                    }
                }
                affectedElements = modification.getAffectedElements( false );
                for( PagedElement affectedElement : affectedElements ) {
                    int pageIndex = affectedElement.getPageIndex();
                    if( pageIndex == currentPageIndex ) {
                        return true;
                    }
                }
            }
            return false;
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
            throw new IllegalArgumentException( "Usage: Render <PDF-File1> <PDF-File2>" );
        }
        return new File[] { SampleUtil.checkAndGetFile( args[0] ), SampleUtil.checkAndGetFile( args[1] ) };
    }
}
