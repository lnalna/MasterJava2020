package ru.javaops.masterjava;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.Project;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class MainXml {
    private static final Comparator<User> USER_COMPARATOR = Comparator.comparing(User::getValue);

    public static void main(String[] args) throws IOException, JAXBException {
       // args[0] = "topjava";
        if (args.length != 1) {
            System.out.println("Error: Input Project Name");
            System.exit(1);
        }
        String projectName = args[0];
        URL payloadUrl = Resources.getResource("payload.xml");

        Set<User> users = jaxbParser(projectName, payloadUrl);
        for (User user : users) {
            System.out.println(user.getValue());
        }
    }

    private static Set<User> jaxbParser(String projectName, URL payloadUrl) throws IOException, JAXBException {
        JaxbParser parser = new JaxbParser(ObjectFactory.class);
        parser.setSchema(Schemas.ofClasspath("payload.xsd"));
        Payload payload;
        try (InputStream is = payloadUrl.openStream()) {
            payload = parser.unmarshal(is);
        }

        Project project = payload.getProjects().getProject().stream()
                .filter(p -> p.getName().equals(projectName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid project name '" + projectName + '\''));

        return payload.getUsers().getUser().stream()
                .filter(u -> !Collections.disjoint(project.getGroup(), u.getGroupRefs()))
                .collect(Collectors.toCollection(() -> new TreeSet<>(USER_COMPARATOR)));
    }
}
