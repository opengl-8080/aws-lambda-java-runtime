package lambda;

import java.util.UUID;

public class Function {
    private static final String ID = UUID.randomUUID().toString();

    String invoke(String request) {
        System.out.println("[" + ID + "] request = " + request);
        return "Java Custom Runtime!";
    }
}
