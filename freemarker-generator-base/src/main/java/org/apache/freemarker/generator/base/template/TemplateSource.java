package org.apache.freemarker.generator.base.template;

import org.apache.freemarker.generator.base.util.Validate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Information how to load a template. The template is either
 * provided as source code or as template path resolved
 * by FreeMarker's template loader.
 */
public class TemplateSource {

    public enum Origin {
        PATH,
        CODE
    }

    /** Name of template for diagnostics */
    private final String name;

    /** Origin of template, e.g. loaded via FreeMarker's template loader */
    private final Origin origin;

    /** Code of the template */
    private final String code;

    /** Template path */
    private final String path;

    /** Template encoding */
    private final Charset encoding;

    private TemplateSource(String name, String code) {
        this.name = name;
        this.origin = Origin.CODE;
        this.code = code;
        this.path = null;
        this.encoding = null;
    }

    private TemplateSource(String name, String path, Charset encoding) {
        this.name = name;
        this.origin = Origin.PATH;
        this.code = null;
        this.path = path;
        this.encoding = encoding;
    }

    public static TemplateSource fromPath(String path) {
        Validate.notEmpty(path, "Template path is empty");
        return new TemplateSource(path, path, StandardCharsets.UTF_8);
    }

    public static TemplateSource fromPath(String path, Charset encoding) {
        Validate.notEmpty(path, "Template path is empty");
        Validate.notNull(encoding, "Template encoding is null");
        return new TemplateSource(path, path, encoding);
    }

    public static TemplateSource fromCode(String name, String code) {
        Validate.notEmpty(name, "Template name is empty");
        Validate.notEmpty(code, "Template code is empty");
        return new TemplateSource(name, code);
    }

    public Origin getOrigin() {
        return origin;
    }

    public String getCode() {
        return code;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "TemplateSource{" +
                "name='" + name + '\'' +
                ", origin=" + origin +
                ", encoding=" + encoding +
                '}';
    }
}
