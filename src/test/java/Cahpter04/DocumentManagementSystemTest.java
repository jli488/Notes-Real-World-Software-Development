package Cahpter04;

import Chapter04.List01.Attributes;
import Chapter04.List01.Document;
import Chapter04.List01.DocumentManagementSystem;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DocumentManagementSystemTest {
    private static final String RESOURCES =
            "src" + File.separator + "test" + File.separator + "resources" + File.separator;
    private static final String LETTER = RESOURCES + "patient.letter";

    private DocumentManagementSystem dms = new DocumentManagementSystem();

    @Test
    public void shouldImportFile() throws Exception {
        dms.importFile(LETTER);
        final Document document = onlyDocument();
        assertAttributeEquals(document, Attributes.PATH, LETTER);
    }

    private void assertAttributeEquals(final Document document,
                                       final String attributeName,
                                       final String expectedValue) {
        assertEquals(
                "Document has the wrong value for " + attributeName,
                expectedValue,
                document.getAttribute(attributeName)
        );
    }

    private Document onlyDocument() {
        final List<Document> documents = dms.contents();
        assertEquals(1, documents.size());
        return documents.get(0);
    }
}
