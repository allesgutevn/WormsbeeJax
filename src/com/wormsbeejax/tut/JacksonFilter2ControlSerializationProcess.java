package com.wormsbeejax.tut;


import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class JacksonFilter2ControlSerializationProcess
{

	public static void main (String[] args) throws JsonParseException, IOException
	{
		JacksonFilter2ControlSerializationProcess j2c = new JacksonFilter2ControlSerializationProcess();
		j2c.givenTypeHasFilterThatIgnoresNegativeInt_whenDtoIsSerialized_thenCorrect();
	}
	
	public final void givenTypeHasFilterThatIgnoresNegativeInt_whenDtoIsSerialized_thenCorrect()
		throws JsonParseException, IOException
	{
		final PropertyFilter theFilter = new SimpleBeanPropertyFilter(){
			@Override
			public final void serializeAsField(
				final Object pojo, final JsonGenerator jgen, final SerializerProvider provider,
				final PropertyWriter writer) throws Exception
			{
				if (include(writer))
				{
					if (!writer.getName().equals("intValue"))
					{
						writer.serializeAsField(pojo, jgen, provider);
						return;
					}

					final int intValue = ((MyDtoWithFilter) pojo).intValue;
					if (intValue >= 0)
					{
						writer.serializeAsField(pojo, jgen, provider);
					}
				}
				else if (!jgen.canOmitFields())
				{ // since 2.3
					writer.serializeAsOmittedField(pojo, jgen, provider);
				}
			}

//			@Override
//			protected final boolean include(final BeanPropertyWriter writer)
//			{
//				return true;
//			}
//
//			@Override
//			protected final boolean include(final PropertyWriter writer)
//			{
//				return true;
//			}
		};
		final FilterProvider filters = new SimpleFilterProvider().addFilter("myFilter", theFilter);

		final MyDtoWithFilter dtoObject = new MyDtoWithFilter("Thanh", -2, true);
		dtoObject.intValue = -1;
		
		final MyDto dto = new MyDto();
		dto.test = dtoObject;

		final ObjectMapper mapper = new ObjectMapper();
		final String dtoAsString = mapper.writer(filters).writeValueAsString(dto);

		// assertThat(dtoAsString, not(containsString("intValue")));
		// assertThat(dtoAsString, containsString("booleanValue"));
		// assertThat(dtoAsString, containsString("stringValue"));
		System.out.println(dtoAsString);
	}

	@JsonFilter("myFilter")
	private class MyDtoWithFilter
	{
		public String stringValue;
		public int intValue;
		public boolean booleanValue;

		public MyDtoWithFilter()
		{
			super();
		}

		public MyDtoWithFilter(final String stringValue, final int intValue, final boolean booleanValue)
		{
			super();

			this.stringValue = stringValue;
			this.intValue = intValue;
			this.booleanValue = booleanValue;
		}
	}
	private class MyDto
	{
		public MyDtoWithFilter test;
	}
}
