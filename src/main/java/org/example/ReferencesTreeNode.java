package org.example;

import lombok.Getter;
import lombok.Setter;

import javax.sound.sampled.LineEvent;
import java.util.HashSet;

@Getter
@Setter
public class ReferencesTreeNode implements Comparable<ReferencesTreeNode>
{
    private int level;
    private HashSet<String> node;
    private String root;
    public ReferencesTreeNode(String rootUrl, int level)
    {
        this.root = rootUrl;
        try
        {
            this.node = new LinkSearcher(rootUrl).findSubReferences();
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        this.level = level;
    }

    public String toString()
    {
        String s = "\t".repeat(level) + root + "\n";
        for (var a : node)
        {
            s += "\t".repeat(level+1) + a + "\n";
        }
        return s + "\n";
    }
    @Override
    public int compareTo(ReferencesTreeNode o)
    {
        int res = this.getRoot().compareTo(o.getRoot());
        return res;
    }
}
