package com.defano.wyldcard.stackreader.decoder;

/**
 * A mixin interface providing utilities for converting a HyperCard version code into human-readable components.
 */
@SuppressWarnings("unused")
public interface VersionDecoder {

    /**
     * Gets the major version part of the version code (i.e., '2' from '2.4').
     *
     * @param versionCode The HyperCard version code
     * @return The major version.
     */
    default int getMajorVersion(int versionCode) {
        return (versionCode & 0xff000000) >> 24;
    }

    /**
     * Gets the minor version part of the version code (i.e., '4' from '2.4').
     *
     * @param versionCode The HyperCard version code.
     * @return The minor version.
     */
    default int getMinorVersion(int versionCode) {
        return (versionCode & 0x00ff0000) >> 16;
    }

    /**
     * Gets the release state of the version code.
     *
     * @param versionCode The HyperCard version code.
     * @return The release state, one of 'final', 'beta', 'alpha', 'development' or 'unknown'.
     */
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
            return "unknown";  // Bad value...?
        }
    }

    /**
     * Gets the build number component of the version code.
     *
     * @param versionCode The HyperCard version code.
     * @return The build number component of the version code.
     */
    default int getBuildNumber(int versionCode) {
        return (versionCode & 0x000000ff);
    }

    /**
     * Provides a human readable string representation of a HyperCard version code.
     *
     * @param versionCode The HyperCard version code.
     * @return A human readable string representation of the version.
     */
    default String getVersionString(int versionCode) {
        return getMajorVersion(versionCode) + "." + getMinorVersion(versionCode) + " build " + getBuildNumber(versionCode) +
                " (" + getReleaseState(versionCode) + ")";
    }

}
