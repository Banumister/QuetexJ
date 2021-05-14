/*
 * License : The MIT License
 * Copyright(c) 2019 Olyutorskii
 */

package io.github.olyutorskii.quetexj;

import java.awt.EventQueue;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * Log handler for Swing text component model(Document).
 *
 * <p>Logging is supported from both EDT(Event-Dispatch-Thread) and non-EDT.
 */
public class SwingLogHandler extends Handler {

    private final Document document;
    private final Queue<String> msgQueue;
    private final LogTransferTask transferTask;


    /**
     * Constructor.
     *
     * <p>PlainDocument is prepared.
     */
    public SwingLogHandler() {
        this(new PlainDocument());
        return;
    }

    /**
     * Constructor.
     *
     * <p>Do not access document via non-EDT.
     *
     * @param document Document model of Swing text component.
     */
    public SwingLogHandler(Document document) {
        super();

        Objects.requireNonNull(document);
        this.document = document;

        this.msgQueue = new ConcurrentLinkedQueue<>();
        this.transferTask =
                new LogTransferTask(this.msgQueue);

        Formatter formatter = new SimpleFormatter();
        setFormatter(formatter);

        return;
    }


    /**
     * Return associated document.
     *
     * <p>Do not access document via non-EDT.
     *
     * @return document
     */
    public Document getDocument() {
        return this.document;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This is thread-safe.
     *
     * @param logRec {@inheritDoc}
     */
    @Override
    public synchronized void publish(LogRecord logRec) {
        if (logRec == null) return;

        if (!isLoggable(logRec)) {
            return;
        }

        Formatter formatter = getFormatter();
        String message = formatter.format(logRec);

        publish(message);

        return;
    }

    /**
     * Publish a log message.
     *
     * <p>Document model will be updated later.
     *
     * @param message log message
     */
    private void publish(String message) {
        boolean offered = this.msgQueue.offer(message);
        assert offered;

        if (EventQueue.isDispatchThread()) {
            this.transferTask.transferQueueToDoc();
        } else {
            EventQueue.invokeLater(this.transferTask);
        }

        return;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() {
        return;
    }

    /**
     * {@inheritDoc}
     *
     * @throws SecurityException {@inheritDoc}
     */
    @Override
    public void close() throws SecurityException {
        setLevel(Level.OFF);
        flush();
        return;
    }


    /**
     * Transfer log from Queue to Document.
     *
     * <p>EDT only supported.
     */
    private class LogTransferTask implements Runnable {

        private final Queue<String> queue;
        private final StringBuilder msgBuf;


        /**
         * Constructor.
         *
         * @param queue log message queue
         */
        LogTransferTask(Queue<String> queue) {
            super();

            this.queue = queue;
            this.msgBuf = new StringBuilder();

            return;
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            transferQueueToDoc();
            return;
        }

        /**
         * Transfer message from Queue to Document.
         */
        void transferQueueToDoc() {
            int queueSize = this.queue.size();
            if (queueSize == 1) {   // common case
                String msg = this.queue.poll();
                appendToDocument(msg);
                return;
            } else if (queueSize <= 0) {
                return;
            }

            this.msgBuf.setLength(0);

            while (!this.queue.isEmpty()) {
                String msg = this.queue.poll();
                if (msg == null) break;
                this.msgBuf.append(msg);
            }

            appendToDocument(this.msgBuf);

            return;
        }

        /**
         * Append text to last pos of Document.
         *
         * <p>DocumentEvent will happen from Document.
         *
         * @param logMessage text
         */
        private void appendToDocument(CharSequence logMessage) {
            if (logMessage == null) return;
            if (logMessage.length() <= 0) return;

            Document doc = getDocument();
            String str = logMessage.toString();
            int insertPt = doc.getLength();

            try {
                doc.insertString(insertPt, str, null);
            } catch (BadLocationException e) {
                assert false;
            }

            return;
        }

    }

}
