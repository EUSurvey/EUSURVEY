package com.ec.survey.tools;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.List;

import static java.nio.file.FileVisitResult.*;

public class FileFinder extends SimpleFileVisitor<Path> {

    private final PathMatcher matcher;
    private int numMatches = 0;
    private List<Path> paths;
    private boolean firstDir = true;

    public FileFinder(String pattern, List<Path> paths) {
    	this.paths = paths;
    	
        matcher = FileSystems.getDefault()
                .getPathMatcher("glob:" + pattern);
    }

    // Compares the glob pattern against
    // the file or directory name.
    void find(Path file) {
        Path name = file.getFileName();
        if (name != null && matcher.matches(name)) {
            numMatches++;
            paths.add(file);
        }
    }

    public List<Path> getResult()
    {
    	return paths;
    }

    // Invoke the pattern matching
    // method on each file.
    @Override
    public FileVisitResult visitFile(Path file,
            BasicFileAttributes attrs) {
        find(file);
        
        if (numMatches >= 50) return TERMINATE;
        
        return CONTINUE;
    }

    // Invoke the pattern matching
    // method on each directory.
    @Override
    public FileVisitResult preVisitDirectory(Path dir,
            BasicFileAttributes attrs) {
        find(dir);
        
        if (firstDir)
        {
        	firstDir = false;
        	return CONTINUE;
        }
        
        return FileVisitResult.SKIP_SUBTREE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file,
            IOException exc) {
        return CONTINUE;
    }
}

