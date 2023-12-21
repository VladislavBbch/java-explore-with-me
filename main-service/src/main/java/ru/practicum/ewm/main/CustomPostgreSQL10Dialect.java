package ru.practicum.ewm.main;

import org.hibernate.dialect.PostgreSQL10Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;

public class CustomPostgreSQL10Dialect extends PostgreSQL10Dialect {
    public CustomPostgreSQL10Dialect() {
        super();
        registerFunction("wilson", new StandardSQLFunction("wilson"));
    }
}
