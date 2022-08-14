package com.ec.survey.tools;

import org.hibernate.dialect.MySQL8Dialect;

import java.sql.Types;

public class CustomMySQL8Dialect extends MySQL8Dialect {
    public CustomMySQL8Dialect(){
        super();
        //Fix for : No Dialect mapping for JDBC type: 0
        registerColumnType(Types.NULL, "null");
        registerHibernateType(Types.NULL, "null");
    }
}
