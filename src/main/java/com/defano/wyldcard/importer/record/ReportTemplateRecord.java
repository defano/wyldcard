package com.defano.wyldcard.importer.record;

@SuppressWarnings("unused")
public class ReportTemplateRecord {

    private final int templateId;
    private final String templateName;

    public ReportTemplateRecord(int templateId, String templateName) {
        this.templateId = templateId;
        this.templateName = templateName;
    }

    public int getTemplateId() {
        return templateId;
    }

    public String getTemplateName() {
        return templateName;
    }
}
