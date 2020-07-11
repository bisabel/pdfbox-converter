package com.bisabel.app;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.PrintWriter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )  throws PrinterException
    {
        if (args.length != 1) {
     	    System.err.println("specify a file url as argument");
     	    System.exit(1);
   	    }

        String filename = args[0];
        try (PDDocument document = PDDocument.load(new File(filename)) ) {
          System.out.println( "Hello World!!!" );
          PDFTextStripper pdfStripper = new PDFTextStripper();

          //Retrieving text from PDF document
          String text = pdfStripper.getText(document);
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
        } catch (IOException ioe){
          System.out.println( "ERROR "+ioe );
        }
    }
}
