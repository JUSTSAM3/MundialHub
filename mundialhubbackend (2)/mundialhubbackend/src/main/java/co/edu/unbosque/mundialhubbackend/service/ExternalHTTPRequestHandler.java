//package co.edu.unbosque.mundialhubbackend.service;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.URLEncoder;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.nio.charset.StandardCharsets;
//import java.time.Duration;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonParser;
//
//import co.edu.unbosque.parcialback.dto.JokeApiDTO;
//import co.edu.unbosque.parcialback.dto.PurgomalumDTO;
//
//public class ExternalHTTPRequestHandler {
//
//	public static void main(String[] args) {
//
////		System.out.println(doGetAndParse(
////				"https://v2.jokeapi.dev/joke/Programming,Miscellaneous,Dark,Pun,Spooky,Christmas?blacklistFlags=nsfw,religious,political,racist,sexist,explicit&type=single"));
//
//		JokeApiDTO joke = doGetJokeDTO("Misc", "twopart", "nsfw");
////		JokeApiDTO joke = doGetJokeDTO("Misc", "single", "nsfw");
//
//		System.out.println(joke.toString());
//
//		String content = " ";
//
//		if (joke.getJoke() != null) {
//			content = joke.getJoke();
//		} else {
//			content = joke.getSetup() + "......" + joke.getDelivery();
//		}
//
//		PurgomalumDTO pu = doGetPurgomalumDTO(content);
//
//		System.out.println(pu.toString());
//
//	}
//
//	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
//			.connectTimeout(Duration.ofSeconds(10)).build();
//
//	public static String doPost(String url, String json) {
//		HttpRequest solicitud = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(json))
//				.uri(URI.create(url)).header("Content-Type", "application/json").build();
//
//		HttpResponse<String> respuesta = null;
//		try {
//			respuesta = HTTP_CLIENT.send(solicitud, HttpResponse.BodyHandlers.ofString());
//		} catch (IOException e) {
//			System.out.println(e);
//		} catch (InterruptedException e) {
//			System.out.println(e);
//		}
//		return respuesta.statusCode() + "\n" + respuesta.body();
//	}
//
//	public static JokeApiDTO doGetJokeDTO(String category, String type, String flags) {
//
//		String url = "";
//
//		if (type.equals("single")) {
//
//			url = "https://v2.jokeapi.dev/joke/" + category + "?blacklistFlags=" + flags + "&type=single";
//		} else {
//			url = "https://v2.jokeapi.dev/joke/" + category + "?blacklistFlags=" + flags;
//
//		}
//
//		HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
//		HttpResponse<String> response = null;
//		try {
//			response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		String json = response.body();
//		return new GsonBuilder().create().fromJson(json, JokeApiDTO.class);
//	}
//
//	public static PurgomalumDTO doGetPurgomalumDTO(String text) {
//
//		String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
//
//		String url = "https://www.purgomalum.com/service/json?text=" + encodedText + "&add=input&fill_char=_";
//
//		System.out.println(doGetAndParse(url));
//
//		HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
//		HttpResponse<String> response = null;
//		try {
//			response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		String json = response.body();
//		return new GsonBuilder().create().fromJson(json, PurgomalumDTO.class);
//	}
//
//	public static String doGetAndParse(String url) {
//
//		HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url))
//				.header("Content-Type", "aplication/json").build();
//
//		HttpResponse<String> response = null;
//		try {
//			response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		System.out.println("status code -> " + response.statusCode());
//		String uglyJson = response.body();
//
//		return prettyPrintUsingGson(uglyJson);
//
//	}
//
//	public static String prettyPrintUsingGson(String uglyJson) {
//
//		@SuppressWarnings("deprecation")
//		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
//		JsonElement jsonElement = JsonParser.parseString(uglyJson);
//		String prettyJsonString = gson.toJson(jsonElement);
//		return prettyJsonString;
//	}
//
//}