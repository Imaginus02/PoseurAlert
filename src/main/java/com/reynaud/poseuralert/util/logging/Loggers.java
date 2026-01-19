package com.reynaud.poseuralert.util.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Loggers {
    private Loggers() {}

    public static Logger technical() { return LoggerFactory.getLogger("technical"); }
    public static Logger business() { return LoggerFactory.getLogger("business"); }
    public static Logger access() { return LoggerFactory.getLogger("access"); }
    public static Logger diagnostic() { return LoggerFactory.getLogger("diagnostic"); }

    public static Logger app(Class<?> cls) { return LoggerFactory.getLogger(cls); }
}
