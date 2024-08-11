package io.myserver.gameplay;
import io.myserver.user.User;
import io.myserver.user.UserManager;
import io.myserver.gameplay.GameRoomManager;

import java.util.Arrays;

public class GameRoom
{
	private String 		id;
	private short		pin;
	private short currentUsers = 0;
	private short max_cap = 2;

	private User[] users = new User[max_cap];

	public GameRoom (String id, short pin)
	{
		this.id = id;
		this.pin = pin;
	}

	public User[] returnJobOfAllUser()
	{
		return users;
	}

	public void addUserToGameRoom(User user)
	{
		System.out.println("add user " + user.getUserName() + " to room " + getId());

		users[currentUsers++] = user;
	}

	public void removeUser(User user)
	{
		System.out.println("remove user " + user.getUserName() + " from room " + getId());
		currentUsers--;
		String id = getId();
		if(currentUsers <= 0)
		{
			currentUsers = 0;
			GameRoomManager.getInstance().deleteGameRoom(id);
		}
		else
		{
			boolean flag = false;
			for (int i = 0; i < max_cap - 1; i++)
			{
				if(users[i] == user || flag == true)
				{
					users[i] = users[i + 1];
				}
			}
		}
	}

	public  void sendMessageToChangeRoom(String msg)
	{
		byte [] tmp = msg.getBytes();
		System.out.println(tmp);
		for (int i = 0; i < currentUsers; i++)
		{
			users[i].sendMsgToClient(tmp);
		}
	}

	public void sendMessage(User sender, String msg)
	{
		String final_data = sender.getUserName() + "=>" + msg;

		byte[] tmp = final_data.getBytes();

		for (int i = 0; i < currentUsers; i++)
		{
			users[i].sendMsgToClient(tmp);
		}
	}

	public void clearGameRoom()
	{
		this.id = null;
		this.pin = 0;
		this.currentUsers = 0;
		for (int i = 0; i < max_cap; i++)
		{
			users[i] = null;
		}
	}

	public User getGameHost()
	{
		return users[0];
	}

	public boolean checkMaxCap()
	{
		return currentUsers <= max_cap;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public short getPin()
	{
		return pin;
	}

	public void setPin(short pin)
	{
		this.pin = pin;
	}

	public int getCurrentPlayers()
	{
		return users.length;
	}
}
