package sax;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import org.xml.sax.*;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.DefaultHandler2;

import com.fasterxml.aalto.sax.*;

public class TestSaxReader
    extends base.BaseTestCase
{
    public void testSimpleReader()
        throws IOException, SAXException
    {
        doTest("<root/>", "<root;root></root;root>");
    }

    public void testComments()
        throws IOException, SAXException
    {
        doTest("<!--c1--><a><!--c2--></a><!--c3-->",
               "<!--c1--><a;a><!--c2--></a;a><!--c3-->");
    }

    public void testPI()
        throws IOException, SAXException
    {
        doTest("<?p?><a><?proc  instr ?></a><?p2  ?>",
               "<?p?><a;a><?proc instr ?></a;a><?p2?>");
    }

    /*
    //////////////////////////////////////////////////////
    // Helper methods:
    //////////////////////////////////////////////////////
    */

    private void doTest(String inputXml, String expXml)
        throws IOException, SAXException
    {
        SAXParserFactoryImpl spf = new SAXParserFactoryImpl();
        SAXParser sp = spf.newSAXParser();
        MyContentHandler h = new MyContentHandler();
        sp.setProperty(SAXProperty.LEXICAL_HANDLER.toExternal(), (DeclHandler) h);
        StringReader str = new StringReader(inputXml);
        sp.parse(new InputSource(str), h);

        expXml = "[[" + expXml + "]]";

        assertEquals(expXml, h.toString());
    }        

    /*
    ////////////////////////////////////////////////
    // Helper class
    ////////////////////////////////////////////////
     */

    final static class MyContentHandler
        extends DefaultHandler2
                implements DeclHandler
    {
        final StringBuffer mText = new StringBuffer();

        public MyContentHandler() { }

        public String toString() { return mText.toString(); }

        public void characters(char[] ch, int start, int length)
        {
            mText.append(ch, start, length);
        }

        public void endDocument()
        {
            mText.append("]]");
        }

        public void endElement(String namespaceURI, String localName, String qName)
        {
            mText.append("</");
            appendName(mText, localName, qName, namespaceURI);
            mText.append(">");
        }

        public void endPrefixMapping(String prefix)
        {
            mText.append("{/");
            mText.append(prefix);
            mText.append("}");
        }

        public void ignorableWhitespace(char[] ch, int start, int length)
        {
            mText.append(ch, start, length);
        }

        public void processingInstruction(String target, String data)
        {
            mText.append("<?");
            mText.append(target);
            if (data != null && data.length() > 0) {
                mText.append(' ');
                mText.append(data);
            }
            mText.append("?>");
        }

        public void setDocumentLocator(Locator locator) { }

        public void skippedEntity(String name)
        {
            mText.append('&');
            mText.append(name);
            mText.append(';');
        }

        public void startDocument()
        {
            mText.append("[[");
        }

        public void startElement(String namespaceURI, String localName, String qName, Attributes attrs)
        {
            mText.append("<");
            appendName(mText, localName, qName, namespaceURI);
            for (int i = 0, len = attrs.getLength(); i < len; ++i) {
                mText.append(' ');
                appendName(mText, attrs.getLocalName(i), attrs.getQName(i),
                           attrs.getURI(i));
                mText.append("='");
                mText.append(attrs.getValue(i));
                mText.append("'");
            }
            mText.append(">");
        }

        public void startPrefixMapping(String prefix, String uri)
        {
            // !!! TBI
        }

        public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName)
        {
            // !!! TBI
        }

        public void warning(SAXParseException e)
        {
            // !!! TBI
        }

        // // // LexicalHandler:

        public void comment(char[] ch, int start, int length)
        {
            mText.append("<!--");
            mText.append(ch, start, length);
            mText.append("-->");
        }

        public void endCDATA()
        {
        }

        public void endDTD()
        {
            mText.append("]>");
        }

        public void endEntity(String name)
        {
        }

        public void startCDATA()
        {
        }

        public void startDTD(String name, String publicId, String systemId)
        {
            mText.append("<!DOCTYPE ");
            mText.append(name);
            if (publicId != null) {
                mText.append(" PUBLIC ");
                mText.append(publicId);
                if (systemId != null) {
                    mText.append(' ');
                    mText.append(systemId);
                }
            } else if (systemId != null) {
                mText.append(" SYSTEM ");
                mText.append(systemId);
            }
            mText.append(" [");
        }

        public void startEntity(String name) 
        {
        }

        // // // Helper methods:

        private void appendName(StringBuffer sb, String ln, String qn, String uri)
        {
            if (uri != null && uri.length() > 0) {
                mText.append('{');
                mText.append(uri);
                mText.append('}');
            }
            mText.append(ln);
            mText.append(';');
            mText.append(qn);
        }
    }
}

