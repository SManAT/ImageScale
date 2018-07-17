package xml;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Ladet Einstellungen
 *
 * @author Mag. Stefan Hagmann
 */
public class configHandler implements ContentHandler {

  private String currentValue;

  public int maxsize;
  public String imagePath;
  public String backupPath;
  public String thumbPath;
  public int thumbsize;

  public configHandler() {

  }

  // Aktuelle Zeichen die gelesen werden, werden in eine Zwischenvariable
  // gespeichert
  @Override
  public void characters(char[] ch, int start, int length)
          throws SAXException {
    currentValue = new String(ch, start, length);
  }

  // Methode wird aufgerufen wenn der Parser zu einem Start-Tag kommt
  @Override
  public void startElement(String uri, String localName, String qName,
          Attributes atts) throws SAXException {
    if (localName.equals("config")) {
    }
  }

  // Methode wird aufgerufen wenn der Parser zu einem End-Tag kommt
  @Override
  public void endElement(String uri, String localName, String qName)
          throws SAXException {
    // Name setzen
    if (localName.equals("thumb")) {
      thumbsize = Integer.parseInt(currentValue.trim());
    }
    if (localName.equals("max")) {
      maxsize = Integer.parseInt(currentValue.trim());
    }
    if (localName.equals("image-dir")) {
     imagePath = currentValue.trim();
    }
    if (localName.equals("backup-dir")) {
      backupPath = currentValue.trim();
    }
    if (localName.equals("thumb-dir")) {
      thumbPath = currentValue.trim();
    }
  }

  @Override
  public void endDocument() throws SAXException {
  }

  @Override
  public void endPrefixMapping(String prefix) throws SAXException {
  }

  @Override
  public void ignorableWhitespace(char[] ch, int start, int length)
          throws SAXException {
  }

  @Override
  public void processingInstruction(String target, String data)
          throws SAXException {
  }

  @Override
  public void setDocumentLocator(Locator locator) {
  }

  @Override
  public void skippedEntity(String name) throws SAXException {
  }

  @Override
  public void startDocument() throws SAXException {
  }

  @Override
  public void startPrefixMapping(String prefix, String uri)
          throws SAXException {
  }
}
