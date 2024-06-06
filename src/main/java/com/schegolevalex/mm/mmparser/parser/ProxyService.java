package com.schegolevalex.mm.mmparser.parser;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProxyService {
    private final List<Proxy> proxies = new ArrayList<>();
    private List<File> extensions = new ArrayList<>();

    public void setProxy(ChromeOptions options) {
        int extNumber = new Random().nextInt(extensions.size());
        log.info("Выбран прокси-сервер №{}", extNumber + 1);
        options.addExtensions(extensions.get(extNumber));
    }

    @PostConstruct
    private void createPlugins() {
        populateProxies();

        AtomicInteger num = new AtomicInteger();

        extensions = proxies.stream().map(proxy -> {
                    String manifest_json = """
                            {
                              "version": "1.0.0",
                              "manifest_version": 2,
                              "name": "Chrome Proxy",
                              "permissions": [
                                "proxy",
                                "tabs",
                                "unlimitedStorage",
                                "storage",
                                "<all_urls>",
                                "webRequest",
                                "webRequestBlocking"
                              ],
                              "background": {
                                "scripts": ["background.js"]
                              },
                              "minimum_chrome_version":"22.0.0"
                            }""";

                    String background_js = String.format("""
                                    var config = {
                                      mode: "fixed_servers",
                                      rules: {
                                        singleProxy: {
                                          scheme: "http",
                                          host: "%s",
                                          port: parseInt(%s)
                                        },
                                        bypassList: ["localhost"]
                                      }
                                    };

                                    chrome.proxy.settings.set({value: config, scope: "regular"}, function() {});

                                    function callbackFn(details) {
                                      return {
                                        authCredentials: {
                                          username: "%s",
                                          password: "%s"
                                        }
                                      };
                                    }

                                    chrome.webRequest.onAuthRequired.addListener(
                                    callbackFn,
                                    {urls: ["<all_urls>"]},
                                    ['blocking']
                                    );""",
                            proxy.getHost(),
                            proxy.getPort(),
                            proxy.getUsername(),
                            proxy.getPassword());

                    FileOutputStream fos;
                    try {
                        fos = new FileOutputStream("proxy_auth_plugin_" + num + ".zip");
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    ZipOutputStream zipOS = new ZipOutputStream(fos);

                    createFile("manifest.json", manifest_json);
                    createFile("background.js", background_js);

                    File file = new File("proxy_auth_plugin_" + num.getAndIncrement() + ".zip");
                    try {
                        writeToZipFile("manifest.json", zipOS);
                        writeToZipFile("background.js", zipOS);
                        zipOS.close();
                        fos.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return file;
                })
                .toList();
    }

    private void populateProxies() {
//        proxies.add(Proxy.builder()
//                .username("vAGrAD")
//                .password("zyAUGsyJATFA")
//                .host("37.139.34.51")
//                .port("10789")
//                .build());
//        proxies.add(Proxy.builder()
//                .username("heZAgY")
//                .password("As4Heu6kAgNe")
//                .host("37.139.34.51")
//                .port("11346")
//                .build());
//        proxies.add(Proxy.builder()
//                .username("kAB3Se")
//                .password("AtEKakUz9yhY")
//                .host("37.139.34.51")
//                .port("11777")
//                .build());
        proxies.add(Proxy.builder()
                .username("BAy5Ty")
                .password("Ep3megaBaT6Z")
                .host("37.139.34.51")
                .port("11780")
                .build());
    }

    private static void writeToZipFile(String path, ZipOutputStream zipStream) throws IOException {
        System.out.println("Writing file : '" + path + "' to zip file");
        File aFile = new File(path);
        FileInputStream fis = new FileInputStream(aFile);
        ZipEntry zipEntry = new ZipEntry(path);
        zipStream.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipStream.write(bytes, 0, length);
        }
        zipStream.closeEntry();
        fis.close();
    }

    private static void createFile(String filename, String text) {
        try (PrintWriter out = new PrintWriter(filename)) {
            out.println(text);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
