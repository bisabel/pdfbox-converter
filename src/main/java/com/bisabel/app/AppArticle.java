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
 * Hello world!
 *
 */
public class AppArticle
{

    public static String fetchTextByRegion(PDDocument document, int pageNumber) throws IOException {
          //Rectangle2D region = new Rectangle2D.Double(x,y,width,height);
          //Rectangle2D region = new Rectangle2D.Double(80, 90, 690, 948);
          //Rectangle2D region = new Rectangle2D.Double(0, 40, 600, 570);//FUNdamentals of climbing: movement.pdf
          //Rectangle2D region = new Rectangle2D.Double(0, 40, 600, 570);
          Rectangle2D region = new Rectangle2D.Double(0, 0, 550, 650);//speed climbing
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
     	    System.err.println("specify a url and a output file as argument");
     	    System.exit(1);
   	    }

        String filename = args[0];
        try (PDDocument pddocument = PDDocument.load(new File(filename)) ) {
          System.out.println( "Hello World!!!" );
          CustomPDFTextStripper pdfStripper = new CustomPDFTextStripper();

          int FIRSTPAGE = 13;
          int LASTPAGE = pddocument.getNumberOfPages();

          //System.out.println(
            //fetchTextByRegion(pddocument, pddocument.getNumberOfPages()-1)
            //fetchTextByRegion(pddocument, 49)
          //);

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
          //System.out.println(text);

          //Closing the PDFdocument
          //pddocument.close();


          //PDDocument doc = PDDocument.load(out);
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
          //System.out.println("list l: "+list.size()+ " - " + pdfStripper.getCurrentPageNo());
          //System.out.println("start: " + pdfStripper.getArticleStart());
          System.out.println("first getPageWidth: "+list.get(0).get(0).getPageWidth());
          System.out.println("first getPageHeight: "+list.get(0).get(0).getPageHeight());

          /*
          for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).size());
            for (int j = 0; j < list.get(i).size(); j++){
              //System.out.print(list.get(i).get(j).size());
              //System.out.print(list.get(i).get(j).getUnicode());
            }
            System.out.println();

            //System.out.println(list.get(i));
          }
          */

          /*
          //String text = pdfStripper.getText(document);
          //doble o triple new lines
          //some become from previous replace
          text = text.replaceAll("  ", " ");
          text = text.replaceAll("\n\\s?\n", "\n");


          //remove specific footer/header books
          text = text.replaceAll("T R A I N I N G T I P S F O R R O C K C L I M B E R S ","");
          text = text.replaceAll("R O P E S - A G U I D E F O R C L I M B E R S A N D M O U N T A I N E E R S","");
          text = text.replaceAll("[0-9]{1,2} Climbing Outside","");
          //specific from file speed climbing!
          text = text.replaceAll("([a-z][0-9])\\s\n([a-z])", "$1 $2");

          //doesnt work
          text = text.replaceAll("Figure\\s*[0-9]+\\s*\n","\n");
          text = text.replaceAll("\n\\s?[0-9]+\\s?\n","\n");

          //doble o triple new lines
          //some become from previous replace
          text = text.replaceAll("  ", " ");
          text = text.replaceAll("\n\\s\n", "\n");

          //remove new line that split sentence
          text = text.replaceAll("([a-zA-Z]+[,\"’);]*)\\s?\n([a-zA-Z])", "$1 $2");
          text = text.replaceAll("([a-z])\\-\\s?\n([a-z])", "$1$2");
          text = text.replaceAll("([0-9])\\-\\s?\n([a-z])", "$1$2");
          text = text.replaceAll("([a-z])\\s\n([a-z])", "$1 $2");
          text = text.replaceAll("([a-z])\n([a-z])", "$1 $2");
          //not sure about this
          text = text.replaceAll("(\\s[0-9])\\s\n([a-z])", "$1 $2");
          text = text.replaceAll("([a-z])\\s\n([0-9])", "$1 $2");
          //BMC case new lines split with simbol – or ()
          text = text.replaceAll("([a-z])\\s–\\s\n([a-zA-Z])", "$1 $2");
          text = text.replaceAll("([a-z])\\s?\n([,\"’‘)(]*[a-zA-Z])", "$1 $2");
          text = text.replaceAll("([a-z])\\s(–)\n([a-z])", "$1 $2 $3");


          try (PrintWriter out =
               new PrintWriter("/home/borja/Proyectos/MScBigData/dissertation/resource/doc_sources/output_from_pdf.txt") )
          {
            out.println(text);
            out.close();
          } catch (java.io.FileNotFoundException fnf){
            System.out.println( "ERROR "+fnf );
          }
          //System.out.println(text);

          //Closing the document
          document.close();
          */
        } catch (IOException ioe){
          System.out.println( "ERROR "+ioe );
        }
    }
}
