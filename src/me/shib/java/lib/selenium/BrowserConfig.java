package me.shib.java.lib.selenium;

import me.shib.java.lib.utils.FileDownloader;
import me.shib.java.lib.utils.FileUtils;

import java.io.File;

public class BrowserConfig {

    public enum Type {
        FIREFOX, CHROME
    }

    public enum OSType {
        WINDOWS ("win32", ".exe"),
        MACOS ("mac64", ""),
        LINUX ("linux64", "");
        private String chromeDriverZipName;
        private String chromeDriverBinaryName;
        OSType(String chromeDriverZipName, String chromeDriverBinaryName) {
            this.chromeDriverZipName = "chromedriver_" + chromeDriverZipName + ".zip";
            this.chromeDriverBinaryName = "chromedriver" + chromeDriverBinaryName;
        }
    }

    private static BrowserConfig.OSType getOSType() {
        String os = System.getProperty("os.name").toLowerCase();
        if(os.contains("win")) {
            return BrowserConfig.OSType.WINDOWS;
        }
        else if(os.contains("mac")) {
            return BrowserConfig.OSType.MACOS;
        }
        else if(os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return BrowserConfig.OSType.LINUX;
        }
        else {
            return null;
        }
    }

    private static final String chromeDriverVersion = "2.41";
    private static final String chromeDriverRootURL = "https://chromedriver.storage.googleapis.com/" + chromeDriverVersion + "/";
    private static final String seleniumDriversLocalPath = "target" + File.separator + "selenium" + File.separator;

    private static final OSType osType = getOSType();

    private static boolean chromeDriverAvailable = false;
    private static boolean firefoxDriverAvailable = false;

    public static synchronized boolean setupChromeDriver() {
        if ((osType != null) && (!chromeDriverAvailable)) {
            String chromeDriverDownloadURL = chromeDriverRootURL + osType.chromeDriverZipName;
            File chromeDriverZip = new File(seleniumDriversLocalPath + osType.chromeDriverZipName);
            File chromeDriverBinary = new File(seleniumDriversLocalPath + osType.chromeDriverBinaryName);

            if (!chromeDriverBinary.exists()) {
                FileDownloader fileDownloader = new FileDownloader(chromeDriverDownloadURL, chromeDriverZip);
                fileDownloader.start();
                try {
                    fileDownloader.join();
                    if (fileDownloader.getDownloadProgress().getStatus() != FileDownloader.DownloadStatus.COMPLETED) {
                        return false;
                    }
                } catch (InterruptedException e) {
                    return false;
                }
                if (!FileUtils.unZip(chromeDriverZip, new File(seleniumDriversLocalPath))) {
                    return false;
                }
            }
            System.setProperty("webdriver.chrome.driver", chromeDriverBinary.getPath());
            if(osType != OSType.WINDOWS) {
                chromeDriverAvailable = chromeDriverBinary.setExecutable(true);
            }
        }
        return chromeDriverAvailable;
    }

}
