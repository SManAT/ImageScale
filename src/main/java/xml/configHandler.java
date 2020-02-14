package xml;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Ladet Einstellungen
 *
 * @author Mag. Stefan Hagmann
 */
public class configHandler extends DefaultHandler {
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
  public void characters(char ch[], int start, int length) throws SAXException {
    currentValue = new String(ch, start, length);
  }

  // Methode wird aufgerufen wenn der Parser zu einem Start-Tag kommt
  @Override 
  public void startElement(String uri, String localName,String qName, 
                Attributes attributes) throws SAXException {
  }

  // Methode wird aufgerufen wenn der Parser zu einem End-Tag kommt
  @Override
  public void endElement(String uri, String localName,
		String qName) throws SAXException {
    // Name setzen
    if (qName.equals("thumb")) {
      thumbsize = Integer.parseInt(currentValue.trim());
    }
    if (qName.equals("max")) {
      maxsize = Integer.parseInt(currentValue.trim());
    }
    if (qName.equals("image-dir")) {
     imagePath = currentValue.trim();
    }
    if (qName.equals("backup-dir")) {
      backupPath = currentValue.trim();
    }
    if (qName.equals("thumb-dir")) {
      thumbPath = currentValue.trim();
    }
  }
}