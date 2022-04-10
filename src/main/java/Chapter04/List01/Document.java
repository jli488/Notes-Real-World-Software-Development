package Chapter04.List01;

import java.util.Map;

public class Document {
    private final Map<String, String> attributes;

    // package scoped constructor
    // only code in the Document Management System should be able to create documents
    Document(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getAttribute(final String attributeName) {
        return attributes.get(attributeName);
    }
}
