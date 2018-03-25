package com.defano.wyldcard.editor;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.TemplateCompletion;

import java.util.ArrayList;
import java.util.Collection;

public class CompletionBuilder {

    private String codePrefix;
    private String templateName;
    private ArrayList<String> templates = new ArrayList<>();
    private String shortDescription;
    private String longDescription;
    private ArrayList<String> examplesCode = new ArrayList<>();
    private ArrayList<String> examplesDescription = new ArrayList<>();
    private ArrayList<String> parameterNames = new ArrayList<>();
    private ArrayList<String> parameterDescriptions = new ArrayList<>();

    public static CompletionBuilder autoComplete(String codePrefix, String named) {
        CompletionBuilder builder = new CompletionBuilder();
        builder.codePrefix = codePrefix;
        builder.templateName = named;
        return builder;
    }

    public CompletionBuilder to(String template) {
        this.templates.add(template);
        return this;
    }

    public CompletionBuilder named(String name) {
        this.templateName = name;
        return this;
    }

    public CompletionBuilder withSummary(String summary) {
        this.shortDescription = summary;
        return this;
    }

    public CompletionBuilder withDescription(String description) {
        this.longDescription = description;
        return this;
    }

    public CompletionBuilder withParmeter(String parameter, String description) {
        this.parameterNames.add(parameter);
        this.parameterDescriptions.add(description);
        return this;
    }

    public CompletionBuilder withExample(String exampleCode) {
        this.examplesCode.add(exampleCode);
        this.examplesDescription.add("");
        return this;
    }

    public CompletionBuilder withExample(String exampleDescription, String exampleCode) {
        this.examplesCode.add(exampleCode);
        this.examplesDescription.add(exampleDescription);
        return this;
    }

    public void buildInto(Collection<Completion> completions, CompletionProvider provider) {
        completions.add(build(provider));
    }

    public TemplateCompletion build(CompletionProvider provider) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(getSummaryMarkdown());
        String summary = HtmlRenderer.builder().build().render(document);

        return new TemplateCompletion(provider, codePrefix, templateName, templates.get(0), null, summary);
    }

    private String getSummaryMarkdown() {
        StringBuilder builder = new StringBuilder();

        if (templateName != null) {
            builder.append("### ").append(templateName).append("\n\n");
        }

        if (shortDescription != null) {
            builder.append(shortDescription).append("\n\n");
        }

        for (String template : templates) {
            template = template.replace("$", "");
            builder.append(makeCodeBlock(0, template));
        }

        builder.append("\n");

        if (!parameterNames.isEmpty()) {
            builder.append("#### Where:").append("\n");
            for (int index = 0; index < parameterNames.size(); index++) {
                builder.append("* ").append("`{").append(parameterNames.get(index)).append("}`: ");
                builder.append(parameterDescriptions.get(index)).append("\n");
            }
        }

        if (longDescription != null) {
            builder.append("\n").append(longDescription).append("\n\n");
        }

        if (!examplesCode.isEmpty()) {

            for (int index = 0; index < examplesDescription.size(); index++) {
                if (examplesDescription.size() > 1) {
                    builder.append("\n**Example ").append(index + 1).append(":** ");
                } else {
                    builder.append("\n**Example:** ");
                }

                if (!examplesDescription.get(index).isEmpty()) {
                    builder.append(examplesDescription.get(index)).append("\n\n");
                } else {
                    builder.append("\n\n");
                }

                builder.append(makeCodeBlock(0, examplesCode.get(index))).append("\n\n");
            }
        }

        return builder.toString();
    }

    /**
     * Converts a string of text into a code block.
     *
     * The Commonmark markdown processor does not appear to properly render triple-backtick code blocks, so this code
     * attempts to synthesize each line as a single-backtick code span. Note that \t characters will be replaced by
     * two non-breaking spaces.
     *
     * @param indent Indent depth to be applied to every line in the block.
     * @param codeBlock The \n-delimited block of code.
     * @return Synthesized Markdown
     */
    private String makeCodeBlock(int indent, String codeBlock) {
        StringBuilder builder = new StringBuilder();

        // Padding before/after ` is important; two adjacent (``) is illegal
        codeBlock = codeBlock.replace("\t", " `&nbsp; &nbsp;` ");

        for (String thisLine : codeBlock.split("\n")) {
            for (int index = 0; index < indent; index++) {
                builder.append(">");
            }

            builder.append("`").append(thisLine).append("`").append("<br>\n");
        }

        return builder.toString();
    }
}
