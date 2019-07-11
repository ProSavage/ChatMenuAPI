package me.tom.sparse.spigot.chat.menu.element;

import me.tom.sparse.spigot.chat.menu.CMListener;
import me.tom.sparse.spigot.chat.menu.ChatMenuAPI;
import me.tom.sparse.spigot.chat.menu.IElementContainer;
import me.tom.sparse.spigot.chat.util.State;
import me.tom.sparse.spigot.chat.util.Text;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collections;
import java.util.List;

public class InputElement extends Element
{
	@NotNull
	public final State<String> value;
	
	protected int     width;
	private   boolean editing;
	
	/**
	 * Constructs a new {@code InputElement}
	 *
	 * @param x     the x coordinate
	 * @param y     the y coordinate
	 * @param width the max width of the text
	 * @param value the starting text
	 */
	public InputElement(int x, int y, int width, @NotNull String value)
	{
		super(x, y);
		this.width = width;
		this.value = new State<>(value);
	}
	
	/**
	 * @return the current value
	 */
	@NotNull
	public String getValue()
	{
		return value.getCurrent();
	}
	
	/**
	 * Sets the text of this element, if the text is longer than the max width it will display "Too long"
	 *
	 * @param value the new value
	 */
	public void setValue(@NotNull String value)
	{
//		if(ChatMenuAPI.getWidth(text) > width)
//			throw new IllegalArgumentException("The provided text is too wide to fit!");
		this.value.setCurrent(value);
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return 1;
	}

	@NotNull
	public List<Text> render(@NotNull IElementContainer context)
	{
		ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, context.getCommand(this));
		
		String current = value.getOptionalCurrent().orElse("");
		boolean tooLong = ChatMenuAPI.getWidth(current) > width;
		
		Text text = new Text(tooLong ? "Too long" : current);
		text.expandToWidth(width);
		text.getComponents().forEach(it -> {
			if(tooLong)
				it.setColor(ChatColor.RED);
			if(editing)
				it.setColor(ChatColor.GRAY);
			it.setUnderlined(true);
			it.setClickEvent(click);
		});
		
		return Collections.singletonList(text);
	}
	
	public boolean onClick(@NotNull IElementContainer container, @NotNull Player player)
	{
		super.onClick(container, player);
		container.getElements().stream().filter(it -> it instanceof InputElement && it != this).map(it -> (InputElement) it).forEach(it -> it.editing = false);
		editing = !editing;
		
		if(editing)
		{
			CMListener.expectPlayerChat(player, (p, m) -> {
				editing = false;
				setValue(m);
				container.refresh();
				return true;
			});
		}else
		{
			CMListener.cancelExpectation(player);
		}
		
		return true;
	}
	
	public void edit(@NotNull IElementContainer container, @NotNull String[] args)
	{
	
	}
	
	@NotNull
	public List<State<?>> getStates()
	{
		return Collections.singletonList(value);
	}
}
