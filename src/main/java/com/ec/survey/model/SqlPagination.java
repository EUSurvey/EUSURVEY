package com.ec.survey.model;

public class SqlPagination {
    private int currentPage;
    private int rowsPerPage;
    
    public SqlPagination(int currentPage, int rowsPerPage)
    {
    	this.currentPage = currentPage;
    	this.rowsPerPage = rowsPerPage;
    }
    
    public int getFirstResult() {
        return (currentPage - 1) * rowsPerPage;
    }
    
    public int getMaxResult() {
        return rowsPerPage;
    }
}
