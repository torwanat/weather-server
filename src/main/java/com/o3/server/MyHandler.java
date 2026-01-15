package com.o3.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class MyHandler implements HttpHandler {
	String [] database = {"Message 1", "Message 2", "Message 3", "ąęćżźł"};

	@Override
	public void handle(HttpExchange t) throws IOException {

		String method = t.getRequestMethod();

		if ("GET".equals(method)) {
			String response = handleGETRequest(t);
			handleGETResponse(t, response);
		} else if ("POST".equals(method)) {
			System.out.println("POST");
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

		for (String message : database){
			responseHTMLBuilder.append("<div style=\"" +
					"border: 1px solid black; " +
					"margin: 5px; " +
					"padding: 0 40px; " +
					"border-radius: 30px; " +
					"width: fit-content\">")
				.append("<h4>")
				.append(message)
				.append("</h4>")
				.append("</div>");
		}

		responseHTMLBuilder.append("</div>")
			.append("</body>")
			.append("</html>");

		return responseHTMLBuilder.toString();
	}

	private void handlePOSTRequest(HttpExchange t){

	}

	private void handleGETResponse(HttpExchange t, String responseString) throws IOException{
		byte [] bytes = responseString.getBytes(StandardCharsets.UTF_8);
		t.sendResponseHeaders(200, bytes.length);
		OutputStream os = t.getResponseBody();
		os.write(responseString.getBytes());
		os.close();
	}

	private void handleRubbishResponse(HttpExchange t) throws IOException{
		String responseString = "Not supported";
		byte [] bytes = responseString.getBytes(StandardCharsets.UTF_8);
		t.sendResponseHeaders(400, bytes.length);
		OutputStream os = t.getResponseBody();
		os.write(responseString.getBytes());
		os.close();
	}
}
