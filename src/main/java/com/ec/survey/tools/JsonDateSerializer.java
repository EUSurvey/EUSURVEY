package com.ec.survey.tools;

import java.io.IOException;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;


@Component
public class JsonDateSerializer extends JsonSerializer<Date>
{
    @Override
    public void serialize(Date date, JsonGenerator gen, SerializerProvider provider)
            throws IOException
    {
        String formattedDate = Tools.formatDate(date, "MM/dd/yyyy HH:mm"); // ISO 8601
        gen.writeString(formattedDate);
    }
}
