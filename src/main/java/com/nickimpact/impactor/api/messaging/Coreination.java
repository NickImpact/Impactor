package com.nickimpact.impactor.api.messaging;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

/**
 * This class represents a set of chat messages fit to a special style of formatting.
 * For a title, we take into account the size of each character along with the line itself,
 * so we can properly center the text we are given. As for the info, we will indent each line
 * by 2 spaces. If no border icon is passed, we will create the border via a provided default.
 *
 * @author NickImpact
 */
public class Coreination
{
	private final Text border;

	private final Text title;

	private final Text info;

	public Coreination(Builder builder)
	{
		this.border = builder.border;
		this.title = builder.title;
		this.info = builder.info;
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public Text output()
	{
		return Text.of(
				border, Text.NEW_LINE,
				title,
				info,
				border
		);
	}

	public static class Builder
	{
		private Text border;

		private Text title;

		private Text info;

		public Builder border(Text border)
		{
			Text b = border;
			for(int i = 0; i < 34; i++)
			{
				b = Text.of(b, border);
			}
			this.border = Text.of(b);
			return this;
		}

		public Builder title(Text title)
		{
			this.title = Text.of(MessageUtils.center(title), Text.NEW_LINE, Text.NEW_LINE);
			return this;
		}

		public Builder info(Text info, boolean indent)
		{
			this.info = Text.of(indent ? MessageUtils.indent(info) : info, Text.NEW_LINE, Text.NEW_LINE);
			return this;
		}

		public Builder info(List<Text> info, boolean indent)
		{
			if(info.isEmpty())
			{
				this.info = Text.EMPTY;
				return this;
			}

			Text result = Text.EMPTY;
			for(Text t : info)
			{
				result = Text.of(result, indent ? MessageUtils.indent(t) : t, Text.NEW_LINE);
			}

			this.info = Text.of(result, Text.NEW_LINE);
			return this;
		}

		public Coreination build()
		{
			if(border == null)
			{
				Builder builder = border(Text.of(TextColors.WHITE, "\u2580"));
				return new Coreination(builder);
			}
			return new Coreination(this);
		}
	}
}
