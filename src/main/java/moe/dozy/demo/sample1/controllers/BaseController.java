package moe.dozy.demo.sample1.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ControllerAdvice
public class BaseController {
    @ModelAttribute("webpack_css")
    public List<String> getWebpackCssManifest() {
        List<String> result = new ArrayList<String>();
        Resource resource = new ClassPathResource("webpack-css.meta.json");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(resource.getFile());
            Iterator<JsonNode> elements = rootNode.elements();
            while (elements.hasNext()) {
                String url = elements.next().asText();
                if (url.endsWith(".css")) {
                    result.add(url);
                }
            }
        } catch(IOException e) {
            // Did you run mvn generate-resources?
        }
        return result;
    }

    @ModelAttribute("webpack_js")
    public List<String> getWebpackJsManifest() {
        List<String> result = new ArrayList<String>();
        Resource resource = new ClassPathResource("webpack-js.meta.json");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(resource.getFile());
            Iterator<JsonNode> elements = rootNode.elements();
            while (elements.hasNext()) {
                String url = elements.next().asText();
                if (url.endsWith(".js")) {
                    result.add(url);
                }
            }
        } catch(IOException e) {
            // Did you run mvn generate-resources?
        }
        return result;
    }
}
