import java.io.Serializable;
import java.util.Random;

public class Message implements Serializable
{

	private String usernameValue;
	private int idValue;
	static Random randomGenerator = new Random();

	private int typeValue;

	public static final int DRAW_OPERATION = 1;
	public static final int UNDO = 2;
	public static final int CREATE_CANVAS = 3;
	public static final int SAVE_CANVAS = 4;
	public static final int LOAD_CANVAS = 5;


	public static final int CHAT_MESSAGE = 6;
	public static final int JOIN_REQUEST = 7;
	public static final int JOIN_RESPONSE = 8;
	public static final int KICK_USER = 9;
	public static final int USER_GOT_KICKED = 10;

	public static final int EXIT = 11;
	public static final int SHUTDOWN = 12;

	public static final int DRAW_UPDATE = 13;

	// contentValue should be immutable
	private Serializable contentValue;

	public String username()
	{
		return usernameValue;
	}

	public int id()
	{
		return idValue;
	}

	public int type()
	{
		return typeValue;
	}

	public Serializable content()
	{
		return contentValue;
	}

	public Message(String user, int idValue, int typeValue, Serializable contentValue)
	{
		this.usernameValue = user;
		this.typeValue = typeValue;
		this.idValue = idValue;
		this.contentValue = contentValue;
	}

	public Message(String user, int typeValue, Serializable contentValue)
	{
		this.usernameValue = user;
		this.typeValue = typeValue;
		this.contentValue = contentValue;
		this.idValue = randomGenerator.nextInt();
	}

	@Override
	public boolean equals(Object obj)
	{
		Message message = (Message) obj;
		return username().equals(message.username()) && id() == message.id() && type() == message.type();
	}

	@Override
	public String toString()
	{
		String tp = "";

		switch (this.type())
		{
			case 1:
				tp = "DRAW_OPERATION";
				break;
			case 2:
				tp = "UNDO";
				break;
			case 3:
				tp = "CREATE_CANVAS";
				break;
			case 4:
				tp = "SAVE_CANVAS";
				break;
			case 5:
				tp = "LOAD_CANVAS";
				break;
			case 6:
				tp = "CHAT_MESSAGE";
				break;
			case 7:
				tp = "JOIN_REQUEST";
				break;
			case 8:
				tp = "JOIN_RESPONSE";
				break;
			case 9:
				tp = "KICK_USER";
				break;
			case 10:
				tp = "USER_GOT_KICKED";
				break;
			case 11:
				tp = "EXIT";
				break;
			case 12:
				tp = "SHUTDOWN";
				break;

		}

		return "<Message>User:" + username() + "\tID:" + id() + "\tType:" + tp;
	}
}
