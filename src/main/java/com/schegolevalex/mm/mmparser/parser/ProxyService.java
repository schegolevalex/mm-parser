package com.schegolevalex.mm.mmparser.parser;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.core.io.ClassPathResource;
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
        if (!extensions.isEmpty()) {
            int extNumber = new Random().nextInt(extensions.size());
            options.addExtensions(extensions.get(extNumber));
            log.info("Выбран прокси-сервер №{} ({}:{}@{}:{})",
                    extNumber + 1,
                    proxies.get(extNumber).getUsername(),
                    proxies.get(extNumber).getPassword(),
                    proxies.get(extNumber).getHost(),
                    proxies.get(extNumber).getPort());
        } else {
            log.info("Прокси-серверов нет");
        }
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
        ClassPathResource proxyResource = new ClassPathResource("proxy/proxy_list");
        if (!proxyResource.exists()) {
            log.error("Файл с прокси-серверами не найден");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(proxyResource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.isEmpty()) continue;
                String[] strings = line.split("@");
                proxies.add(Proxy.builder()
                        .username(strings[0].split(":")[0])
                        .password(strings[0].split(":")[1])
                        .host(strings[1].split(":")[0])
                        .port(strings[1].split(":")[1])
                        .build());
            }
        } catch (IOException e) {
            log.error("Не удалось прочитать файл с прокси-серверами", e);
        }
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

    @PreDestroy
    private void deleteExtensions() {
        extensions.forEach(file -> {
            if (file.exists()) {
                if (file.delete())
                    log.info("Файл удален: {}", file.getAbsolutePath());
                else
                    log.error("Не удалось удалить файл: {}", file.getAbsolutePath());
            } else
                log.info("Файл не существует: {}", file.getAbsolutePath());
        });
    }
}
