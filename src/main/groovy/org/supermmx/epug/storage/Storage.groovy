package org.supermmx.epug.storage

interface Storage {
    void addResource(String srcPath, String destPath)
    byte[] getResource(String destPath)
}
