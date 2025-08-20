package org.example;

import lombok.Getter;
import org.apache.log4j.PropertyConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashSet;

import org.apache.log4j.Logger;

@Getter
public class LinkSearcher
{
    private static final Logger logger = Logger.getLogger("LinkSearcher");
    private String url;
    private static String ROOT_PAGE_URL;
    private static int counter = 0;
    public LinkSearcher(String url)
    {
        if(counter < 1)
        {
            ROOT_PAGE_URL = url;
            counter++;
        }
        this.url = url;
        PropertyConfigurator.configure("E:/Программирование/java_basics/Multithreading/SiteMap/src/log4j.properties");
    }
    public HashSet<String> findSubReferences() throws
            InterruptedException
    {
        HashSet<String> referencesList = new HashSet<>();
        Document doc = null;
        Thread.sleep(150);//чтобы не заблокировал сервер
        try {
            doc = Jsoup.connect(url).userAgent("Mozilla").get();
            Elements references = doc.select("a");
            //если на странице есть ссылки
            if(references.size() > 0)
            {
                //поиск на корневой странице(url) всех ссылок
                for (var ref : references)
                {
                    ref.getElementsByAttribute("href").stream().forEach(t ->
                    {
                        String reference = t.absUrl("href"); //).absUrl
                        if(reference != "") referencesList.add(reference);
                    });
                }
            }
        } catch (Exception e) {
            logger.info("LinkSearcher: " + e.getMessage());;
        }
        return referencesList;
    }

    public String getRootRefernce()
    {
        return ROOT_PAGE_URL;
    }
    public String toString()
    {
        String s = "";
        try
        {
            for(var ref : this.findSubReferences())
            {
                s += ref + "\n";
            }
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        return s;
    }

}
