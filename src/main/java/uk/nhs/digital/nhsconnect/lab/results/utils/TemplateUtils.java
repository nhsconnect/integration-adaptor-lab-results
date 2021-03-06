package uk.nhs.digital.nhsconnect.lab.results.utils;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.MustacheNotFoundException;

import java.io.IOException;
import java.io.StringWriter;

public final class TemplateUtils {

    private TemplateUtils() { }

    private static final String TEMPLATES_DIRECTORY = "templates";

    public static Mustache loadTemplate(String templateName) {
        MustacheFactory mf = new DefaultMustacheFactory(TEMPLATES_DIRECTORY);
        return mf.compile(templateName);
    }

    public static String fillTemplate(Mustache template, Object content) {
        StringWriter writer = new StringWriter();
        String data = "";

        try {
            template.execute(writer, content).flush();
            data += writer.toString();
        } catch (IOException e) {
            throw new MustacheNotFoundException("Could not fill template " + template.getName(), e);
        }

        return data;
    }
}
