package org.example;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.xml.transform.Source;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ForkJoinPool;

public class Main {
    private static final Logger logger = Logger.getLogger("Main");
    public static void main(String[] args)
    {
//        LinkSearcher l = new LinkSearcher("https://skillbox.ru");
//        for(var a : l.findSubReferences())
//        {
//            LinkSearcher l1 = new LinkSearcher(a);
//            System.out.println(a + " lvl/" + l.getFoldersNumber(a));
//
//            l1.findSubReferences().stream().forEach(b ->
//                    {
//                        if(b.equals(a)) System.out.println("ССЫЛКА ВЕДЁТ НА КОРЕНЬ СТРАНИЦЫ");
//                        if(l.getFoldersNumber(b) <= l.getFoldersNumber(a)) System.out.println("ссылка на странице ниже или равна по укровню корня");
//                        System.out.println("\t"+b + " lvl/"+l.getFoldersNumber(b));
//                    });
//
//        }
        foo();
    }
    public static void foo()
    {
        PropertyConfigurator.configure("E:/Программирование/java_basics/Multithreading/SiteMap/src/log4j.properties");
        String path = "E:/Программирование/java_basics/Multithreading/SiteMap/mapTest.txt";
        String url = "https://skillbox.ru";//http://wordpresssite

        SiteMapCreator siteMapCreator = new SiteMapCreator(url, path, 0);
        FileWriter fileWriter = null;
        try
        {
            fileWriter = new FileWriter(path, true);
            fileWriter.write(siteMapCreator.getLinkSearcher().getRootRefernce()+"\n");
            fileWriter.flush();
        }
        catch (IOException e)
        {
            logger.info(e.getMessage());
        }
        logger.info("NEW MAP CREATED");
        new ForkJoinPool().invoke(siteMapCreator);
    }
}