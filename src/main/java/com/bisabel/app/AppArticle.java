package com.bisabel.app;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.io.StringWriter;
import java.awt.geom.Rectangle2D;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;

class CustomPDFTextStripper extends PDFTextStripperByArea{
    //Vector<Vector<List<TextPosition>>> data = new Vector<Vector<List<TextPosition>>>();
    public CustomPDFTextStripper() throws IOException {
        super();
    }

    public List<List<TextPosition>> getCharactersByArticle(){
       // data.add(charactersByArticle);
        return charactersByArticle;
    }

    public int getCurrentPageNo(){
      return super.getCurrentPageNo();
    }

    String prevBaseFont = "";
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
      StringBuilder builder = new StringBuilder();

      for (TextPosition position : textPositions)
      {
          String baseFont = position.getFont().getName() +  " | "	+position.getFont().getSubType() +" ("+position.getFontSizeInPt()+")";
          if (baseFont != null && !baseFont.equals(prevBaseFont))
          {
              //builder.append('[').append(baseFont).append(']');
              prevBaseFont = baseFont;
          }
          builder.append(position.getUnicode());
      }

      writeString(builder.toString());
    }

}



/**
 * Read a PDF File and extract the text into a txt file.
 *
 */
public class AppArticle
{

    /*
    * Extract the text of a given PDF page.
    * Needs to specify a rectangle area on the page to avoid marginsd, footer, heading...
    * Every PDF needs different rectangle definition
    */
    public static String fetchTextByRegion(PDDocument document, int pageNumber) throws IOException {
      //Rectangle2D region = new Rectangle2D.Double(x,y,width,height);
      //Rectangle2D region = new Rectangle2D.Double(80, 90, 690, 948);
      //Rectangle2D region = new Rectangle2D.Double(0, 40, 600, 570);
      //Rectangle2D region = new Rectangle2D.Double(0, 40, 600, 570);
      Rectangle2D region = new Rectangle2D.Double(0, 0, 550, 650);
      String regionName = "region";
      PDFTextStripperByArea stripper;
      PDPage page = document.getPage(pageNumber);
      stripper = new CustomPDFTextStripper();

      stripper.addRegion(regionName, region);
      stripper.extractRegions(page);
      String text = stripper.getTextForRegion(regionName);
      return cleanText(text);
    }

    public static String cleanText(String text){
      //some become from previous replace
      text = text.replaceAll("  ", " ");
      text = text.replaceAll("\n\\s\n", "\n");

      //remove new line that split sentence
      text = text.replaceAll("([a-zA-Z]+[,\"’);]*)\\s?\n([a-zA-Z])", "$1 $2");
      text = text.replaceAll("([a-z])\\-\\s?\n([a-z])", "$1$2");
      text = text.replaceAll("([0-9])\\-\\s?\n([a-z])", "$1$2");
      text = text.replaceAll("([a-z])\\s\n([a-z])", "$1 $2");
      text = text.replaceAll("([a-z])\n([a-z])", "$1 $2");
      return text;
    }

    public static void main( String[] args )  throws PrinterException
    {
        if (args.length != 2) {
     	    System.err.println("specify PDF File path and a File for a output as arguments");
     	    System.exit(1);
   	    }

        String filename = args[0];
        try (PDDocument pddocument = PDDocument.load(new File(filename)) ) {
          System.out.println("PDF Loaded");
          CustomPDFTextStripper pdfStripper = new CustomPDFTextStripper();

          //change this valuees to extract a specific range of pages of the PDF
          //for default,it will extracted all pages.
          int FIRSTPAGE = 1;
          int LASTPAGE = pddocument.getNumberOfPages();

          try (PrintWriter out =
               new PrintWriter(args[1]) )
          {
            for (int npage = FIRSTPAGE; npage < LASTPAGE; npage++) {
              out.println(fetchTextByRegion(pddocument, npage));
            }
            out.close();
          } catch (java.io.FileNotFoundException fnf){
            System.out.println( "ERROR "+fnf );
          }

          PDFTextStripper textStripper = new PDFTextStripper();
          StringWriter textWriter = new StringWriter();
          pdfStripper.writeText(pddocument, textWriter);
          //set the start-end page

          pdfStripper.setStartPage(14);
          pdfStripper.setEndPage(14);

          System.out.println(pdfStripper.getText(pddocument));
          pddocument.close();

          //Retrieving text from PDF document
          List<List<TextPosition>>	list = pdfStripper.getCharactersByArticle();
          System.out.println("first getPageWidth: "+list.get(0).get(0).getPageWidth());
          System.out.println("first getPageHeight: "+list.get(0).get(0).getPageHeight());

        } catch (IOException ioe){
          System.out.println( "ERROR "+ioe );
        }
    }
}
