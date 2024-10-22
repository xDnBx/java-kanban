package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.interfaces.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String requestMethod = httpExchange.getRequestMethod();
            if (requestMethod.equals("GET")) {
                String response = gson.toJson(taskManager.getHistory());
                sendText(httpExchange, response, 200);
            } else {
                sendText(httpExchange, "Ждем GET, а получили - " + requestMethod, 405);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendText(httpExchange, "Internal Server Error", 500);
        }
    }
}