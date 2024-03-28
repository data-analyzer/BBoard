package com.se.board.common.gson;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class LocalDateTypeAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
	// private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d::MMM::uuuu HH::mm::ss");
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	// SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN);
	// 2024-03-22T11:01:18.932Z

	@Override
	public JsonElement serialize(final LocalDate date, final Type typeOfSrc, JsonSerializationContext context) {

		return new JsonPrimitive(date.format(formatter));
	}

	@Override
	public LocalDate deserialize(final JsonElement json, final Type typeOfSrc, JsonDeserializationContext context) throws JsonParseException {

		return LocalDate.parse(json.getAsString(), formatter);
	}
}
