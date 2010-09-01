/* Woodstox Lite ("wool") XML processor
 *
 * Copyright (c) 2006- Tatu Saloranta, tatu.saloranta@iki.fi
 *
 * Licensed under the License specified in the file LICENSE which is
 * included with the source code.
 * You may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fasterxml.aalto.out;

import java.io.*;

import javax.xml.stream.*;

/**
 * This is the generic implementation of {@link XmlWriter}, used if
 * the destination is byte-based {@link java.io.OutputStream}, and
 * encoding is ISO-8859-1 (aka Latin1)
 */
public final class Latin1XmlWriter
    extends SingleByteXmlWriter
{
    /**
     * Decode tables will have enough entries for the full 8-bit range
     */
    final static int LAST_VALID_CHAR = 0xFF;

    /*
    ////////////////////////////////////////////////
    // Life-cycle
    ////////////////////////////////////////////////
     */

    public Latin1XmlWriter(WriterConfig cfg, OutputStream out)
    {
        super(cfg, out, OutputCharTypes.getLatin1CharTypes());
    }

    public int getHighestEncodable()
    {
        return 0xFF;
    }

    public void writeRaw(char[] cbuf, int offset, int len)
        throws IOException, XMLStreamException
    {
        if (_out == null || len == 0) {
            return;
        }
        if (mSurrogate != 0) {
            outputSurrogates(mSurrogate, cbuf[offset]);
            ++offset;
            --len;
        }

        len += offset; // now marks the end

        // !!! TODO: combine input+output length checks into just one

        while (offset < len) {
            char ch = cbuf[offset++];
            if (ch > LAST_VALID_CHAR) {
                reportFailedEscaping("raw content", ch);
            }
            if (_outputPtr >= _outputBufferLen) {
                flushBuffer();
            }
            _outputBuffer[_outputPtr++] = (byte)ch;
        }
    }

    /* Note: identical to that of Ascii writer implementation
     */

    protected WName doConstructName(String localName)
        throws XMLStreamException
    {
        return new ByteWName(localName, getAscii(localName));
    }

    protected WName doConstructName(String prefix, String localName)
        throws XMLStreamException
    {
        int plen = prefix.length();
        byte[] pname = new byte[plen + 1 + localName.length()];
        getAscii(prefix, pname, 0);
        pname[plen] = BYTE_COLON;
        getAscii(localName, pname, plen+1);
        return new ByteWName(prefix, localName, pname);
    }
}
