package com.yandex.kanban.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathConstant {
    public static final String RESOURCE = "resources" + File.separator;
    public static final String FILE_NAME = "dataBase.csv";
    public static final String HEADER = "id,type,name,description,status,other";
    public static final Path PATH = Paths.get(RESOURCE, FILE_NAME);
}
