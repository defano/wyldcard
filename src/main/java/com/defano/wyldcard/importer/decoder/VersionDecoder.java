package com.defano.wyldcard.importer.decoder;

@SuppressWarnings("unused")
public interface VersionDecoder {

    default int getMajorVersion(int versionCode) {
        return (versionCode & 0xff000000) >> 24;
    }

    default int getMinorVersion(int versionCode) {
        return (versionCode & 0x00ff0000) >> 16;
    }

    default String getReleaseState(int versionCode) {
        int state = (versionCode & 0x0000ff00) >> 8;

        if (state == 0x80) {
            return "final";
        } else if (state == 0x60) {
            return "beta";
        } else if (state == 40) {
            return "alpha";
        } else if (state == 20) {
            return "development";
        } else {
            return "";  // Bad value...?
        }
    }

    default int getBuildNumber(int versionCode) {
        return (versionCode & 0x000000ff);
    }

    default String getVersionString(int versionCode) {
        return getMajorVersion(versionCode) + "." + getMinorVersion(versionCode) + " r" + getBuildNumber(versionCode) +
                " " + getReleaseState(versionCode);
    }

}
