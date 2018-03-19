package com.nickimpact.impactor.api.messaging;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.FormattingCodeTextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

/**
 * (Some note will appear here)
 *
 * @author NickImpact (Nick DeGruccio)
 */
public class MessageUtils
{
	private final static int CENTER_PX = 154;

	private final static FormattingCodeTextSerializer serializer = TextSerializers.FORMATTING_CODE;

	public static Text center(Text message){
		if(message == null || message.equals(Text.EMPTY))
			return Text.EMPTY;

		String text = serializer.serialize(message);

		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for(char c : text.toCharArray())
		{
			if(c == serializer.getCharacter())
			{
				previousCode = true;
			}
			else if(previousCode)
			{
				previousCode = false;
				isBold = c == 'l' || c == 'L';
			}
			else
			{
				DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
				messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
				messagePxSize++;
			}
		}

		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = CENTER_PX - halvedMessageSize;
		int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		StringBuilder sb = new StringBuilder();
		while(compensated < toCompensate)
		{
			sb.append(" ");
			compensated += spaceLength;
		}

		return serializer.deserialize(sb.toString() + text);
	}

	public static Text indent(Text text)
	{
		if(text == null || text.equals(Text.EMPTY))
			return Text.EMPTY;

		String[] lines = serializer.serialize(text).split("\n");
		StringBuilder result = new StringBuilder();

		for(String line : lines)
		{
			StringBuilder output = new StringBuilder();
			String[] message = line.split(" ");

			int maxPerLine = 275;
			int current = 0;

			boolean code = false;
			boolean isBold = false;

			for (String word : message)
			{
				for (char c : word.toCharArray())
				{
					if (c == serializer.getCharacter())
						code = true;
					else if (code)
					{
						code = false;
						isBold = c == 'l' || c == 'L';
					} else if (c == '\n')
					{
						current = 0;
					} else
					{
						DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
						current += isBold ? dFI.getBoldLength() : dFI.getLength();
						current++;
					}
				}

				if (current < maxPerLine)
				{
					if (output.length() == 0)
					{
						output.append("  ").append(word);
					} else
					{
						output.append(' ').append(word);
					}
				} else
				{
					current -= maxPerLine;
					output.append('\n').append(word);
				}
			}
			result.append(output).append('\n');
		}

		return serializer.deserialize(result.toString());
	}
}
