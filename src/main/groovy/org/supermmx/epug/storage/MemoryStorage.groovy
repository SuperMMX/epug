package org.supermmx.epug.storage

class MemoryStorage extends AbstractStorage {
    Map<String, byte[]> resources = [:]

    void addResource(String srcPath, String destPath) {
        byte[] content = new File(srcPath).getBytes()
        resources[(destPath)] = content
    }

    byte[] getResource(String destPath) {
        return resources[(destPath)]
    }
}
