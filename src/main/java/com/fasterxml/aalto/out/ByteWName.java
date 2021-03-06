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

/**
 * This class is used to represent all names that are to be serialized
 * to byte streams, independent of specific encoding used (they will
 * be stored on per-encoding symbol tables however).
 */
public final class ByteWName
    extends WName
{
    final byte[] _bytes;

    protected ByteWName(String ln, byte[] bytes)
    {
        super(ln);
        _bytes = bytes;
    }

    protected ByteWName(String prefix, String ln, byte[] bytes)
    {
        super(prefix, ln);
        _bytes = bytes;
    }

    public final int serializedLength()
    {
        return _bytes.length;
    }

    public int appendBytes(byte[] buffer, int offset)
    {
        int len = _bytes.length;
        System.arraycopy(_bytes, 0, buffer, offset, len);
        return len;
    }

    public void writeBytes(OutputStream out) throws IOException
    {
        out.write(_bytes);
    }

    public int appendChars(char[] buffer, int offset)
    {
        throw new RuntimeException("Internal error: appendChars() should never be called");
    }

    public void writeChars(Writer w) throws IOException
    {
        throw new RuntimeException("Internal error: writeChars() should never be called");
    }

}

