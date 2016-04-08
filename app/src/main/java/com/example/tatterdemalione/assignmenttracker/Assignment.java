package com.example.tatterdemalione.assignmenttracker;

/**
 * Created by Tatterdemalione on 2016-03-30.
 */
public class Assignment
{
    private String  name, course, url, dueDate;
    private int id;
    private long priority;

    public Assignment(int id, String name, String course, String url, String dueDate)
    {
        this.name = name;
        this.course = course;
        this.url = url;
        this.dueDate = dueDate;
        this.id = id;
    }

    /* GETTER Methods */
    public String getAssignmentName()
    {
        return this.name;
    }
    public String getCourseName()
    {
        return this.course;
    }
    public String getUrl()
    {
        return this.url;
    }
    public String getDueDate()
    {
        return this.dueDate;
    }
    public int getId()
    {
        return this.id;
    }
    public long getPriority() { return this.priority; }

    public void setPriority(long priority)
    {
        this.priority = priority;
    }
}
