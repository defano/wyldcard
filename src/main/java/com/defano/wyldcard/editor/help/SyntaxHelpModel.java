package com.defano.wyldcard.editor.help;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SyntaxHelpModel {

    private String title;
    private String summary;
    private String description;
    private String codePrefix;
    private List<String> templates = new ArrayList<>();
    private List<ParameterModel> parameters = new ArrayList<>();
    private List<ExampleModel> examples = new ArrayList<>();

    private SyntaxHelpModel() {}

    public static Collection<SyntaxHelpModel> fromJson(String resource) throws IOException {
        Gson gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .setPrettyPrinting()
                .setLenient()
                .create();

        URL url = Resources.getResource(resource);
        String text = Resources.toString(url, Charsets.UTF_8);

        Type collectionType = new TypeToken<Collection<SyntaxHelpModel>>(){}.getType();
        return gson.fromJson(text, collectionType);
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getCodePrefix() {
        return codePrefix;
    }

    public List<String> getTemplates() {
        return templates;
    }

    public List<ParameterModel> getParameters() {
        return parameters;
    }

    public List<ExampleModel> getExamples() {
        return examples;
    }

    public String getDescription() {
        return description;
    }
}
