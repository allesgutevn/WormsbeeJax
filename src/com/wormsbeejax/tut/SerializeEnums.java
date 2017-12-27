package com.wormsbeejax.tut;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class SerializeEnums
{
	@JsonFormat(shape = JsonFormat.Shape.OBJECT)
	@JsonSerialize(using = DistanceSerializer.class)
	public static enum Distance {
	    KILOMETER("km", 1000), 
	    MILE("miles", 1609.34),
	    METER("meters", 1), 
	    INCH("inches", 0.0254),
	    CENTIMETER("cm", 0.01), 
	    MILLIMETER("mm", 0.001);
	 
	    public String unit;
	    public double meters;
	 
	    private Distance(String unit, double meters) {
	        this.unit = unit;
	        this.meters = meters;
	    }
	    // standard getters and setters
	}
	
	public static class DistanceSerializer extends StdSerializer<Distance>
	{
		private static final long serialVersionUID = 1L;

		public DistanceSerializer()
		{
			super(Distance.class);
		}

		public DistanceSerializer(Class<Distance> t)
		{
			super(t);
		}
	    @Override
	    public void serialize(Distance distance, JsonGenerator generator, SerializerProvider provider)
	    		throws IOException, JsonProcessingException {
	    	
	        generator.writeStartObject();
	        generator.writeFieldName("name");
	        generator.writeString(distance.name());
	        generator.writeFieldName("unit");
	        generator.writeString(distance.unit);
	        generator.writeFieldName("meters");
	        generator.writeNumber(distance.meters);
	        generator.writeEndObject();
	    }

	}
	
	public static void main(String[] args) throws IOException
	{
		System.out.println(new ObjectMapper().writeValueAsString(Distance.KILOMETER));
	}
	
}
