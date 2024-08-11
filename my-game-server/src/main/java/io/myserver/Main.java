package io.myserver;

public class Main
{
	public static void main(String[] args)
	{
		System.out.println("Welcome to my game server !");

		try
		{
			MyGameServer.getInstance().start();
		}
		catch (Exception ex)
		{
			MyGameServer.getInstance().stop();

			ex.printStackTrace();
		}

		System.out.println("Good bye !");
	}
}