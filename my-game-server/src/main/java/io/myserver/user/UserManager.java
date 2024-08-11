package io.myserver.user;

import io.myserver.MyGameServer;
import io.myserver.database.DBConnector;

import java.util.concurrent.ConcurrentHashMap;

public class UserManager
{
	private static final UserManager _INSTANCE = new UserManager();

	public static UserManager getInstance()
	{
		return _INSTANCE;
	}

	private static ConcurrentHashMap<String, User>		onlineUsers;

	private UserManager()
	{
		onlineUsers = new ConcurrentHashMap<>();
	}

	private int validUsernameLength = 3;
	private int validPasswordLength = 6;

	public boolean newUser(String userName, String password)
	{
		//TODO: implement
		if(onlineUsers.containsKey(userName) == true)
			return false;

		if(checkValidUser(userName, password) == false)
			return false;

		User u = new User(userName, password);
		onlineUsers.put(userName, u);

		return true;
		//return false;
	}

	public boolean checkValidUser(String username, String password)
	{
		System.out.println("userlength =" + username.length());
		System.out.println("passlength =" + password.length());
		if(username.length() >= validUsernameLength && password.length() >= validPasswordLength)
		{
			return true;
		}
		return false;
	}

	public boolean checkValidChangePass (String user, String newPass)
	{
		String checkUserPass = onlineUsers.get(user).getPassword();
		if(newPass.length() < validPasswordLength)
		{
			return false;
		}
		if(checkUserPass.equals(newPass))
			return false;
		return true;
	}

	public boolean checkValidUsersToEnterGame(User user)
	{
		String idRoom = user.getIdRoom();
		String username = user.getUserName();
		String password = user.getPassword();
		boolean checkValidUser = checkValidUser(username, password);

		if(idRoom != null && checkValidUser == true)
			return true;
		return false;
	}

	public boolean changeScene(User user)
	{
		if(checkValidUsersToEnterGame(user) == true)
			return true;
		return false;
	}

	public User login(String userName, String password)
	{
		//TODO: implement
		//System.out.println(onlineUsers.get(userName).getPassword());

		if (onlineUsers.containsKey(userName) == true)
		{
			if (onlineUsers.get(userName).getPassword().equals(password)) {
				System.out.println("True");
				return onlineUsers.get(userName);
			}
		}
		else
		{
			User offlineUser = loadDb(userName);
			if (offlineUser == null)
			{
				return null;
			}

			onlineUsers.put(userName, offlineUser);

			if (offlineUser.getPassword().equals(password)) {
				System.out.println("True");
				return offlineUser;
			}
		}

		return null;
	}

	public void saveDb(User user)
	{
		String data = user.serialize();

		MyGameServer.getInstance().getDbConn().set(user.getUserName(), data);
	}

	public void saveAllUsers()
	{
		for (User user : onlineUsers.values())
		{
			String data = user.serialize();

			MyGameServer.getInstance().getDbConn().set(user.getUserName(), data);
		}
	}


	public static User loadDb(String userName)
	{
		try
		{
			String data = MyGameServer.getInstance().getDbConn().get(userName);

			return User.deserialize(data);
		}
		catch (Exception ex)
		{
			System.out.println("Register to login");
		}

		return null;
	}

	public User getUser(String userName)
	{
		//TODO: implement
		if (onlineUsers.containsKey(userName) == true)
		{
			return onlineUsers.get(userName);
		}
		return null;
	}
}
