package com.bisabel.app;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.regex.Pattern;
import java.net.URL;

import java.io.FileWriter;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.PrintWriter;

public class extractFromURL {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";

    public static void main(String[] args)  throws Exception{
      if (args.length != 2) {
        System.err.println("specify a url and a output file as argument");
        System.exit(1);
      }

      String url = args[0];
      //String url = "https://www.alpinesavvy.com/blog/belayers-responsibilities-to-the-climber";

      //Change depending of the encoding of the website
      Document _doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
      //Document _doc = Jsoup.parse(new URL(url).openStream(), "ISO-8859-1", url);

      Element _body = _doc.body();
      Element _content = _body.select(".entry-content").first();//first node with text in alpinesavvy.com
      Elements _paragraphs = _content.getElementsByTag("p");
      for(Element text : _paragraphs) {
        System.err.println(""+text.text());
      }

      try (PrintWriter out =
           new PrintWriter(args[1]) )
      {
        for(Element text : _paragraphs) {
          out.println(text.text());
          out.println();
          System.err.println(""+text.text());
        }
        out.close();
      } catch (java.io.FileNotFoundException fnf){
        System.out.println( "ERROR "+fnf );
      }

      System.err.println("ALL DONE");
    }
}
