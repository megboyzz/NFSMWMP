/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class ByteBufferIOStream {
    protected static final int SEGMENT_SIZE = 4096;
    protected int m_availableSegment = 0;
    protected LinkedList<byte[]> m_buffer = new LinkedList<>();
    protected boolean m_closed = false;
    protected ByteBufferInputStream m_input = new ByteBufferInputStream();
    protected ByteBufferOutputStream m_output = new ByteBufferOutputStream();
    protected int m_readPosition = 0;
    protected int m_writePosition = 0;

    public ByteBufferIOStream() {
        this(1);
    }

    public ByteBufferIOStream(int n2) {
        int n3 = n2;
        if (n2 <= 0) {
            n3 = 1;
        }
        n3 = (n3 - 1) / 4096;
        n2 = 0;
        while (n2 < n3 + 1) {
            this.m_buffer.add(new byte[4096]);
            ++n2;
        }
    }

    public void appendSegmentToBuffer(byte[] byArray, int n2) throws IOException {
        if (this.m_writePosition == 0 && byArray.length == 4096) {
            ListIterator<byte[]> listIterator = this.m_buffer.listIterator();
            for (int i2 = 0; i2 < this.m_availableSegment; ++i2) {
                listIterator.next();
            }
            listIterator.add(byArray);
            if (n2 != 4096) {
                this.m_writePosition = n2;
                return;
            }
            ++this.m_availableSegment;
            return;
        }
        this.getOutputStream().write(byArray, 0, n2);
    }

    public int available() throws IOException {
        return this.m_input.available();
    }

    public void clear() {
        this.m_closed = false;
        this.m_availableSegment = 0;
        this.m_writePosition = 0;
        this.m_readPosition = 0;
    }

    protected void closeIOStream() {
        this.m_closed = true;
    }

    public InputStream getInputStream() {
        return this.m_input;
    }

    public OutputStream getOutputStream() {
        return this.m_output;
    }

    public byte[] growBufferBySegment() throws IOException {
        if (this.m_writePosition != 0) {
            throw new IOException("Bad location to grow buffer");
        }
        ListIterator<byte[]> listIterator = this.m_buffer.listIterator();
        int n2 = 0;
        while (true) {
            if (n2 >= this.m_availableSegment) {
                byte[] byArray = new byte[4096];
                listIterator.add(byArray);
                ++this.m_availableSegment;
                return byArray;
            }
            listIterator.next();
            ++n2;
        }
    }

    public byte[] prepareSegment() {
        if (this.m_availableSegment + 1 >= this.m_buffer.size()) {
            return new byte[4096];
        }
        if (this.m_buffer.size() != 0) return this.m_buffer.removeLast();
        return null;
    }

    protected class ByteBufferInputStream
    extends InputStream {
        protected ByteBufferInputStream() {
        }

        @Override
        public int available() throws IOException {
            if (!ByteBufferIOStream.this.m_closed) return ByteBufferIOStream.this.m_availableSegment * 4096 + ByteBufferIOStream.this.m_writePosition - ByteBufferIOStream.this.m_readPosition;
            throw new IOException("ByteBufferIOStream is closed");
        }

        @Override
        public void close() throws IOException {
            ByteBufferIOStream.this.closeIOStream();
        }

        @Override
        public boolean markSupported() {
            return false;
        }

        @Override
        public int read() throws IOException {
            if (this.available() <= 0) {
                throw new IOException("Nothing to read in ByteBufferIOStream");
            }
            byte by2 = ByteBufferIOStream.this.m_buffer.getFirst()[ByteBufferIOStream.this.m_readPosition];
            ByteBufferIOStream byteBufferIOStream = ByteBufferIOStream.this;
            ++byteBufferIOStream.m_readPosition;
            if (ByteBufferIOStream.this.m_readPosition < 4096) return by2;
            ByteBufferIOStream.this.m_buffer.add(ByteBufferIOStream.this.m_buffer.poll());
            ByteBufferIOStream.this.m_readPosition = 0;
            byteBufferIOStream = ByteBufferIOStream.this;
            --byteBufferIOStream.m_availableSegment;
            return by2;
        }

        @Override
        public int read(byte[] byArray) throws IOException {
            return this.read(byArray, 0, byArray.length);
        }

        @Override
        public int read(byte[] object, int n2, int n3) throws IOException {
            if (n2 < 0) throw new IndexOutOfBoundsException("The reading range of out of buffer boundary.");
            if (n3 < 0) throw new IndexOutOfBoundsException("The reading range of out of buffer boundary.");
            if (n2 + n3 > ((byte[])object).length) {
                throw new IndexOutOfBoundsException("The reading range of out of buffer boundary.");
            }
            int n4 = this.available();
            if (n4 <= 0) {
                return -1;
            }
            int n5 = n3;
            if (n3 > n4) {
                n5 = n4;
            }
            if (n5 < (n3 = 4096 - ByteBufferIOStream.this.m_readPosition)) {
                System.arraycopy(ByteBufferIOStream.this.m_buffer.getFirst(), ByteBufferIOStream.this.m_readPosition, object, n2, n5);
                ByteBufferIOStream byteBufferIOStream = ByteBufferIOStream.this;
                byteBufferIOStream.m_readPosition += n5;
                return n5;
            }
            System.arraycopy(ByteBufferIOStream.this.m_buffer.getFirst(), ByteBufferIOStream.this.m_readPosition, object, n2, n3);
            ByteBufferIOStream.this.m_buffer.add(ByteBufferIOStream.this.m_buffer.poll());
            n4 = n5 - n3;
            n2 += n3;
            int n6 = n4 / 4096;
            n3 = 0;
            while (true) {
                if (n3 >= n6) {
                    System.arraycopy(ByteBufferIOStream.this.m_buffer.getFirst(), 0, object, n2, n4);
                    ByteBufferIOStream.this.m_readPosition = n4;
                    ByteBufferIOStream byteBufferIOStream = ByteBufferIOStream.this;
                    byteBufferIOStream.m_availableSegment -= n6 + 1;
                    return n5;
                }
                System.arraycopy(ByteBufferIOStream.this.m_buffer.getFirst(), 0, object, n2, 4096);
                ByteBufferIOStream.this.m_buffer.add(ByteBufferIOStream.this.m_buffer.poll());
                n4 -= 4096;
                n2 += 4096;
                ++n3;
            }
        }

        @Override
        public long skip(long l2) throws IOException {
            int n2;
            int n3 = this.available();
            if (n3 <= 0) {
                return 0L;
            }
            long l3 = l2;
            if (l2 > (long)n3) {
                l3 = n3;
            }
            if ((n3 = (int)l3) < (n2 = 4096 - ByteBufferIOStream.this.m_readPosition)) {
                ByteBufferIOStream byteBufferIOStream = ByteBufferIOStream.this;
                byteBufferIOStream.m_readPosition += n3;
                return l3;
            }
            ByteBufferIOStream.this.m_buffer.add(ByteBufferIOStream.this.m_buffer.poll());
            n2 = n3 - n2;
            int n4 = n2 / 4096;
            n3 = 0;
            while (true) {
                if (n3 >= n4) {
                    ByteBufferIOStream.this.m_readPosition = n2;
                    ByteBufferIOStream byteBufferIOStream = ByteBufferIOStream.this;
                    byteBufferIOStream.m_availableSegment -= n4 + 1;
                    return l3;
                }
                ByteBufferIOStream.this.m_buffer.add(ByteBufferIOStream.this.m_buffer.poll());
                n2 -= 4096;
                ++n3;
            }
        }
    }

    protected class ByteBufferOutputStream
    extends OutputStream {
        protected ByteBufferOutputStream() {
        }

        @Override
        public void close() throws IOException {
            ByteBufferIOStream.this.closeIOStream();
        }

        @Override
        public void write(int n2) throws IOException {
            if (ByteBufferIOStream.this.m_closed) {
                throw new IOException("ByteBufferIOStream is closed");
            }
            ByteBufferIOStream.this.m_buffer.getFirst()[ByteBufferIOStream.this.m_writePosition] = (byte)n2;
            ByteBufferIOStream byteBufferIOStream = ByteBufferIOStream.this;
            ++byteBufferIOStream.m_writePosition;
            if (ByteBufferIOStream.this.m_writePosition != 4096) return;
            ByteBufferIOStream.this.m_writePosition = 0;
            byteBufferIOStream = ByteBufferIOStream.this;
            ++byteBufferIOStream.m_availableSegment;
        }

        @Override
        public void write(byte[] byArray) throws IOException {
            this.write(byArray, 0, byArray.length);
        }

        @Override
        public void write(byte[] object, int n2, int n3) throws IOException {
            int n4;
            if (n2 < 0) throw new IndexOutOfBoundsException("The writing range is out of buffer boundary.");
            if (n3 < 0) throw new IndexOutOfBoundsException("The writing range is out of buffer boundary.");
            if (n2 + n3 > ((byte[])object).length) {
                throw new IndexOutOfBoundsException("The writing range is out of buffer boundary.");
            }
            if (ByteBufferIOStream.this.m_closed) {
                throw new IOException("ByteBufferIOStream is closed");
            }
            int n5 = 4096 - ByteBufferIOStream.this.m_writePosition;
            Iterator iterator = ByteBufferIOStream.this.m_buffer.iterator();
            for (n4 = 0; n4 < ByteBufferIOStream.this.m_availableSegment; ++n4) {
                iterator.next();
            }
            if (n3 < n5) {
                System.arraycopy(object, n2, iterator.next(), ByteBufferIOStream.this.m_writePosition, n3);
                ByteBufferIOStream byteBufferIOStream = ByteBufferIOStream.this;
                byteBufferIOStream.m_writePosition += n3;
                return;
            }
            System.arraycopy(object, n2, iterator.next(), ByteBufferIOStream.this.m_writePosition, n5);
            n4 = n2 + n5;
            Object object2 = ByteBufferIOStream.this;
            ++((ByteBufferIOStream)object2).m_availableSegment;
            ByteBufferIOStream.this.m_writePosition = 0;
            n2 = n3 -= n5;
            n3 = n4;
            while (n2 > 0) {
                if (iterator.hasNext()) {
                    object2 = (byte[])iterator.next();
                } else {
                    object2 = new byte[4096];
                    ByteBufferIOStream.this.m_buffer.add((byte[])object2);
                }
                if (n2 < 4096) {
                    System.arraycopy(object, n3, object2, 0, n2);
                    ByteBufferIOStream.this.m_writePosition = n2;
                    n2 = 0;
                    continue;
                }
                System.arraycopy(object, n3, object2, 0, 4096);
                n2 -= 4096;
                n3 += 4096;
                object2 = ByteBufferIOStream.this;
                ++((ByteBufferIOStream)object2).m_availableSegment;
            }
        }
    }
}

