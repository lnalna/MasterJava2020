package ru.javaops.masterjava;

import com.google.common.base.Splitter;
import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.Project;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.nullToEmpty;

public class MainXml {
    private static final Comparator<User> USER_COMPARATOR = Comparator.comparing(User::getValue).thenComparing(User::getEmail);

    public static void main(String[] args) throws Exception {
        // args[0] = "topjava";
        if (args.length != 1) {
            System.out.println("Error: Input Project Name");
            System.exit(1);
        }
        String projectName = args[0];
        URL payloadUrl = Resources.getResource("payload.xml");

        Set<User> users = jaxbParser(projectName, payloadUrl);
        for (User user : users) {
            System.out.println("name=" + user.getValue() + " email=" + user.getEmail());
        }

        System.out.println("\n\n\n");
        users = staxParser(projectName, payloadUrl);
        for (User user : users) {
            System.out.println("name=" + user.getValue() + " email=" + user.getEmail());
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

    private static Set<User> staxParser(String projectName, URL payloadUrl) throws Exception {
        try (InputStream is = payloadUrl.openStream()) {
            StaxStreamProcessor processor = new StaxStreamProcessor(is);
            final Set<String> groupNames = new HashSet<>();

            // Projects loop
            projects:
            while (processor.doUntil(XMLEvent.START_ELEMENT, "Project")) {
                if (projectName.equals(processor.getAttribute("name"))) {
                    // Groups loop
                    String element;
                    while ((element = processor.doUntilAny(XMLEvent.START_ELEMENT, "Project", "Group", "Users")) != null) {
                        if (!element.equals("Group")) {
                            break projects;
                        }
                        groupNames.add(processor.getAttribute("name"));
                    }
                }
            }
            if (groupNames.isEmpty()) {
                throw new IllegalArgumentException("Invalid " + projectName + " or no groups");
            }

            // Users loop
            Set<User> users = new TreeSet<>(USER_COMPARATOR);

            while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                String groupRefs = processor.getAttribute("groupRefs");
                if (!Collections.disjoint(groupNames, Splitter.on(' ').splitToList(nullToEmpty(groupRefs)))) {
                    User user = new User();
                    user.setEmail(processor.getAttribute("email"));
                    user.setValue(processor.getText());
                    users.add(user);
                }
            }
            return users;
        }
    }
}
