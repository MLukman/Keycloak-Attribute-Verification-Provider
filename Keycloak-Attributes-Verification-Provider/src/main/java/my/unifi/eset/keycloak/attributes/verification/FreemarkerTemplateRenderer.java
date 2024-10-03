package my.unifi.eset.keycloak.attributes.verification;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import my.unifi.eset.keycloak.attributes.verification.jpa.UAVerificationEntity;
import org.keycloak.authentication.RequiredActionContext;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class FreemarkerTemplateRenderer {

    private final Configuration freemarkerConfig;

    // Constructor to initialize the Freemarker configuration
    public FreemarkerTemplateRenderer() {
        // Set up the Freemarker configuration
        freemarkerConfig = new Configuration(new Version("2.3.31"));
        freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/templates"); // Templates directory
        freemarkerConfig.setDefaultEncoding("UTF-8");
    }

    public String renderForm(UAVerificationEntity uav, RequiredActionContext context) {
        try {
            // Load the Freemarker template
            Template template = getTemplate("requiredActionForm.ftl");

            // Create the data model to pass to the template
            Map<String, Object> model = new HashMap<>();
            model.put("uav", uav);
            model.put("context", context);

            // Process the template with the provided model
            StringWriter writer = new StringWriter();
            template.process(model, writer);

            return writer.toString();
        } catch (IOException | TemplateException e) {
            throw new RuntimeException("Error while rendering Freemarker template");
        }
    }

    // Method to get the Freemarker template by name
    private Template getTemplate(String templateName) throws IOException {
        return freemarkerConfig.getTemplate(templateName);
    }
}
