package org.example;


import java.util.Comparator;

public class ReferencesTreeNodeLevelComparator implements Comparator<ReferencesTreeNode>
{

    @Override
    public int compare(ReferencesTreeNode o1, ReferencesTreeNode o2)
    {
        return o1.getLevel()- o2.getLevel();
    }
}
