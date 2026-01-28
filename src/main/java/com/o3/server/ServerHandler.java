package com.o3.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ServerHandler implements HttpHandler {
	ArrayList<String> database = new ArrayList<String>();

	@Override
	public void handle(HttpExchange t) throws IOException {

		String method = t.getRequestMethod();

		if ("GET".equals(method)) {
			String response = handleGETRequest(t);
			handleGETResponse(t, response);
		} else if ("POST".equals(method)) {
			handlePOSTRequest(t);
			handlePOSTResponse(t);
		} else {
			handleRubbishResponse(t);
		}
	}

	private String handleGETRequest(HttpExchange t){

		StringBuilder responseHTMLBuilder = new StringBuilder();
		responseHTMLBuilder.append("<html>")
			.append("<head>")
			.append("<meta charset=\"UTF-8\"")
			.append("</head>")
			.append("<body>")
			.append("<div>");

		if (!database.isEmpty()){
			for (String message : database){
				responseHTMLBuilder.append("<div style=\"" +
						"border: 1px solid black; " +
						"margin: 5px; " +
						"padding: 0 40px; " +
						"border-radius: 30px; " +
						"width: fit-content; " +
						"/*font-family: 'Comic Sans MS', 'Comic Sans', cursive; " +
						"color: #fe019a*/\">")
					.append("<h4>")
					.append(message)
					.append("</h4>")
					.append("</div>");
			}
		} else {
			responseHTMLBuilder.append("<div style=\"" +
				"border: 1px solid black; " +
				"margin: 5px; " +
				"padding: 0 40px; " +
				"border-radius: 30px; " +
				"width: fit-content; " +
				"/*font-family: 'Comic Sans MS', 'Comic Sans', cursive; " +
				"color: #fe019a*/\">")
				.append("<h4>")
				.append("No messages")
				.append("</h4>")
				.append("</div>");
		}


		responseHTMLBuilder.append("</div>")
			.append("</body>")
			.append("</html>");

		return responseHTMLBuilder.toString();
	}

	private void handleGETResponse(HttpExchange t, String responseString) throws IOException{
		byte [] bytes = responseString.getBytes(StandardCharsets.UTF_8);
		t.sendResponseHeaders(200, bytes.length);
		OutputStream os = t.getResponseBody();
		os.write(responseString.getBytes());
		os.flush();
		os.close();
	}

	private void handlePOSTRequest(HttpExchange t){
		String messageToAdd = new BufferedReader(new InputStreamReader(t.getRequestBody(),StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
		database.add(messageToAdd);
	}

	private void handlePOSTResponse(HttpExchange t) throws IOException{
		t.sendResponseHeaders(200, -1);
		OutputStream os = t.getResponseBody();
		os.flush();
		os.close();
	}

	private void handleRubbishResponse(HttpExchange t) throws IOException{
		String responseString = "Not supported";
		byte [] bytes = responseString.getBytes(StandardCharsets.UTF_8);
		t.sendResponseHeaders(400, bytes.length);
		OutputStream os = t.getResponseBody();
		os.write(responseString.getBytes());
		os.flush();
		os.close();
	}
}
