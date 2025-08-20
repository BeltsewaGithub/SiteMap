package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;

import lombok.Getter;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

@Getter
public class SiteMapCreator extends RecursiveTask<ReferencesTreeNode>
{
    private static final Logger logger = Logger.getLogger("SiteMapCreator");
    private LinkSearcher linkSearcher;
    private static FileWriter fileWriter;
    private String mapFilePath;
    private static final int MAX_RECOURSION_DEPTH = 3;
    private static ArrayList<ReferencesTreeNode> siteMap;
    private static ConcurrentHashMap referensesProcessed = new ConcurrentHashMap();
    private String url;
    private int level = 0;
    public SiteMapCreator(String url, String mapFilePath, int level)
    {
        PropertyConfigurator.configure("E:/Программирование/java_basics/Multithreading/SiteMap/src/log4j.properties");
        this.mapFilePath = mapFilePath;
        this.linkSearcher = new LinkSearcher(url);
        this.url = url;
        this.level = level;
        siteMap = new ArrayList<>();
        try
        {
            fileWriter = new FileWriter(mapFilePath, true);
        }
        catch (IOException e)
        {
            logger.info("SiteMapCreator: " + e.getMessage());
        }
    }

    /*
    Если ссылка содержится в карте сайта и есть все её потомки,
    то вхождения в рекурсию не будет, ссылка просто записывается в потомков корня
     */
    @Override
    protected ReferencesTreeNode compute()
    {
        ReferencesTreeNode node = new ReferencesTreeNode(url, level);
        List<SiteMapCreator> taskList = new ArrayList<>();
        //уникальные ссылки
        HashSet<String> references = node.getNode();

        //Перебор ссылок на странице url
        for(String ref : references)
        {
            //с массивом отработанных ссылок одновременно может работать только один поток
            synchronized (referensesProcessed)
            {
                //если ссылка уже была обработана
                boolean isNotExist = !referensesProcessed.keySet().contains(ref);
                referensesProcessed.put(ref, level);
                //Если ссылки нет в массиве бывших в рассмотрении ссылок
                if (isNotExist)
                {
                    String root = linkSearcher.getRootRefernce();
                    /*
                    если ссылка не ведёт на корень сайта
                    И ссылка содержит корень (т.е. не ведёт на сторонние сайты)
                    И ссылка не ведёт на корень текущей страницы
                    И текущий уровень не превышает максимальную глубину рекурсии
                     */
                    if (!ref.equals(root) && ref.contains(root) && !ref.equals(this.url) && this.level < MAX_RECOURSION_DEPTH)
                    {
                        logger.info("SiteMapCreator: Новое вхождение в рекурсию: " + ref + "; lvl - " + level);
                        SiteMapCreator task = new SiteMapCreator(ref, mapFilePath, level + 1);
                        task.fork();
                        taskList.add(task);
                    }
                }
                else
                {
                    logger.info("SiteMapCreator: Ссылка существует в карте - " + ref);
                }
            }
        } //конец перебора ссылок на странице

        for(SiteMapCreator task : taskList)
        {
            var treeNode = task.join();
            siteMap.add(treeNode);
            try
            {
                fileWriter.write(treeNode.toString());
            }
            catch (IOException e)
            {
                logger.info("SiteMapCreator: " + e.getMessage());
            }
            logger.info("SiteMapCreator: Добавлен новый node - \n" + treeNode.toString());
        }
        return node;
    }
    public ArrayList<ReferencesTreeNode> getSiteMap()
    {
        return this.siteMap;
    }
    private ReferencesTreeNode getNode(String rootUrl)
    {
        return this.getSiteMap()
                .stream().filter(
                        ref -> ref.getRoot().equals(rootUrl)
                )
                .sorted(new ReferencesTreeNodeLevelComparator())
                .findFirst()
                .get();
    }

}