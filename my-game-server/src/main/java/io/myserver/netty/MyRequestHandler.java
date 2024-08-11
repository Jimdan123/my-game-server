package io.myserver.netty;

import io.myserver.MyGameServer;
import io.myserver.gameplay.GameRoom;
import io.myserver.gameplay.GameRoomManager;
import io.myserver.Util;
import io.myserver.user.User;
import io.myserver.user.UserManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Arrays;


public class MyRequestHandler extends SimpleChannelInboundHandler<byte[]>
{
	private static final int CTRL_TO_READ_SHORT = 1;
	private static final int CTRL_TO_READ_ARRAY_SHORT = 2;
	private static final int CTRL_TO_CHANGE_GAME = 3;
	private static final int CTRL_TO_SEND_MSG = 4;
	private static final int CTRL_TO_CREATE_NEW_ROOM = 5;
	private static final int CTRL_TO_SEND_MSG_IN_GAME_ROOM = 6;
	private static final int CTRL_TO_LOGIN_GAME_ROOM = 7;
	private static final int CTRL_PRINT_ROOM_INFO = 8;
	private static final int CTRL_TO_LOGIN = 9;
	private static final int CTRL_TO_CREATE_NEW_USER = 10;
	private static final int CTRL_TO_CHANGE_PASS = 11;
	private static final int CTRL_TO_LOGOUT = 12;
	private static final int CTRL_TO_USER_INFO = 13;

	User bl;
	GameRoomManager gameRoomManager = GameRoomManager.getInstance();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, byte[] data) throws Exception
	{
		//Message from client
		System.out.print("Message data: ");
		Util.printByteArray(data);
		/*
		for (int i = 0; i < data.length; i++)
		{
			System.out.println(data[i]);
		}
		*/
		String msg = new String(data);
		int ctrlCode = data[0];
		System.out.println("Ctrl code: '" + ctrlCode + "'");

		switch (ctrlCode)
		{
			case CTRL_TO_READ_SHORT:
				short value = Util.readShort(data, 1);
				System.out.println(data.length /2 + " " + value);
				break;

			case CTRL_TO_READ_ARRAY_SHORT:
				short values = Util.readShortArray(data,1);
				System.out.println(data.length /2 + " " + values);
				break;

			case CTRL_TO_LOGIN:
				handleLogin(ctx, data);
				break;

			case  CTRL_TO_CHANGE_PASS:
				changePassForUser(msg, data);
				break;

			case CTRL_TO_CREATE_NEW_USER:
				createNewUser(ctx,msg,data);
				break;

			case CTRL_TO_LOGOUT:
				if(bl.getIdRoom() != null)
					gameRoomManager.leaveGameRoom(bl);
				UserManager.getInstance().saveDb(bl);
				bl = null;
				break;

			case CTRL_TO_SEND_MSG:
				dmToUser(data);
				break;

			case CTRL_TO_CREATE_NEW_ROOM:
				handleCreateRoom(ctx, data);
				break;

			case CTRL_TO_SEND_MSG_IN_GAME_ROOM:
				String message  = Util.readString(data, 1);
				GameRoom cr = gameRoomManager.getGameRoom(bl.getIdRoom());
				cr.sendMessage(bl, message);
				break;

			case CTRL_TO_LOGIN_GAME_ROOM:
				handleJoinRoom(ctx, data);
				break;

			case CTRL_TO_CHANGE_GAME:
				boolean checkChangeScene = UserManager.getInstance().changeScene(bl);
				if(checkChangeScene == true)
				{
					changeGameRoom(data);
				}
				break;

			case CTRL_PRINT_ROOM_INFO:
				sendRoomInfo(ctx);
				break;

			case CTRL_TO_USER_INFO:
				sendUserInfo(ctx);
				break;
		}



//			System.out.println(data.length /2 + " " + values);
//			System.out.println(data.length /2 + " " + value);

		//reply to client
		/*
		byte[] resp = new byte[1];
		resp[0] = (byte) ctrlCode;
		ctx.writeAndFlush(resp);
		 */
	}

	private void sendUserInfo(ChannelHandlerContext ctx)
	{
		String username = bl.getUserName();
		String password = bl.getPassword();
		String gold = String.valueOf(bl.getGold());
		String level = String.valueOf(bl.getLevel());
		String resp = String.valueOf(CTRL_TO_USER_INFO) + "Create/Login Pls!!!";
		if (username == null || password == null)
			return;
		if(UserManager.getInstance().getUser(username) != null)
		{
			//TODO: tra ve level + gold ...
			resp = String.valueOf(CTRL_TO_USER_INFO + "Username=" + username + " " + "Password=" + password +" " +"Gold=" + gold + " "+"Level=" +  level);
			System.out.println(resp);
		}
		ctx.writeAndFlush(resp.getBytes());
	}

	private void sendRoomInfo(ChannelHandlerContext ctx)
	{
		String resp = String.valueOf(CTRL_PRINT_ROOM_INFO) + "Join A room pls!!!";;

		if (bl.getIdRoom() != null) {
			GameRoom room = gameRoomManager.getGameRoom(bl.getIdRoom());
			String idRoom = bl.getIdRoom();
			String pin = String.valueOf(room.getPin());
			String userJob = bl.getJobIdRoom();
			//TODO: tra ve id phong, role
			if (bl.getJobIdRoom() != "Host")
				resp = String.valueOf(CTRL_PRINT_ROOM_INFO) + "IdRoom=" + idRoom + " " + "Title=" + userJob;
			else
				resp = String.valueOf(CTRL_PRINT_ROOM_INFO) + "IdRoom=" + idRoom + " " + "Pin=" + pin + " " + "Title=" + userJob;
		}
		ctx.writeAndFlush(resp.getBytes());
	}

	private void changePassForUser(String msg, byte[] data)
	{
		if(bl == null) {
			System.out.println("You forget to log in");
			return;
		}
		//String[] da = readAllString(data);
		String newPassword = Util.readString(data, 1);
		System.out.println("newPassword = " + newPassword);
		boolean checkValidChangePass = UserManager.getInstance().checkValidChangePass(bl.getUserName(), newPassword);
		if(checkValidChangePass == true)
			bl.setPassword(newPassword);
		else {
			msg = "This password is invalid!!!";
			data = msg.getBytes();
			bl.sendMsgToClient(data);
		}
	}

	private void createNewUser(ChannelHandlerContext ctx,String msg, byte[] data)
	{
		String str[] = readAllString(data);
		String name = str[0];
		String pass = str[1];
		//User u = new User(newUsername,newPassword);
		boolean bol = UserManager.getInstance().newUser(str[0], str[1]);
		if (bol == true)
		{
			bl = UserManager.getInstance().getUser(str[0]);
			bl.setChannelCtx(ctx);
		}
		else
		{
			msg += "is invalid";
			data = msg.getBytes();
			ctx.writeAndFlush(data);
		}
		System.out.println(bol);
	}

	private void dmToUser(byte[] data)
	{
		String username = Util.readString(data, 1);
		int index = 2 + username.length();

		String msg = Util.readString(data, index);
		//byte[] subarr = {data[index], data[index + 1]};
		User receiver = UserManager.getInstance().getUser(username);
		if (receiver != null)
		{
			receiver.sendMsgToClient(msg.getBytes());
		}
		System.out.println(UserManager.getInstance().getUser(username));
	}

	private void handleLogin(ChannelHandlerContext ctx, byte[] data)
	{
		String[] da = readAllString(data);
		bl = UserManager.getInstance().login(da[0], da[1]);
		if (bl == null) {
			System.out.println("Invalid Information");
		}
		else
		{
			bl.setChannelCtx(ctx);
			System.out.println("Welcome," + bl.getUserName());

			String gold = String.valueOf(bl.getGold());
			String level = String.valueOf(bl.getLevel());
			//TODO: tra ve level + gold ...
			String resp = String.valueOf(CTRL_TO_LOGIN) + gold + " " +  level;
			System.out.println(resp);
			ctx.writeAndFlush(resp.getBytes());

		}
	}

	private void handleCreateRoom(ChannelHandlerContext ctx, byte[] data)
	{
		String[] room = readAllString(data);
		gameRoomManager.addNewGameRoom(room[0],Short.parseShort(room[1]), bl);
		bl.setIdRoom(room[0]);
		bl.setJobIdInRoom("Host");

		String idRoom = bl.getIdRoom();
		String userJob = bl.getJobIdRoom();
		//TODO: tra ve id phong, role
		String resp = String.valueOf(CTRL_TO_CREATE_NEW_ROOM) + idRoom + " " + userJob;
		ctx.writeAndFlush(resp.getBytes());
	}

	private void handleJoinRoom(ChannelHandlerContext ctx, byte[] data)
	{
		String[] params = readAllString(data);

		String desired_room_id = params[0];
		short pin = Short.parseShort(params[1]);
		String message  = new String(data);
		if (gameRoomManager.checkLoginGameRoom(desired_room_id, pin, bl))
		{
			bl.setIdRoom(desired_room_id);
			bl.setJobIdInRoom("Guest");


			String idRoom = bl.getIdRoom();
			String userJob = bl.getJobIdRoom();
			//TODO: tra ve id phong, role
			String resp = String.valueOf(CTRL_TO_LOGIN_GAME_ROOM) + idRoom + " "+ userJob;
			ctx.writeAndFlush(resp.getBytes());
		}
	}

//	private void checkFullGameRoomToRun(ChannelHandlerContext ctx, byte[] data)
//	{
//		String message = new String(data);
//		message = "";
//		User[] user = gameRoomManager.checkFullToJoinGame(bl.getIdRoom());
//		if ( user != null)
//		{
//			for (int i = 0; i < user.length; i++)
//			{
//				message += user[i].getJobIdRoom() + " ";
//			}
//		}
//		data = message.getBytes();
//		ctx.writeAndFlush(data);
//	}

	private String[] readAllString (byte[] data)
	{
		int off = 1;
		String param1 = Util.readString(data,off);
		off += (1 + param1.length());
		System.out.println("param1 = " + param1);

		String param2 = Util.readString(data, off);
		System.out.println("param2 = " + param2);

		String str[] = new String[2];
		str[0] = param1;
		str[1] = param2;
        return str;
    }

	private void changeGameRoom(byte[] data)
	{
		String msg = Util.readString(data, 1);
		msg = String.valueOf(data[0]) + String.valueOf(data[1]) + msg;
		if(bl.getJobIdRoom() != "Guest")
		{
//			ctx.writeAndFlush(data);
			gameRoomManager.getGameRoom(bl.getIdRoom()).sendMessageToChangeRoom(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		System.out.println("exceptionCaught " + cause.getMessage());
		cause.printStackTrace();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx)
	{
		System.out.println("Disconnect !!!");
		if (bl != null)
		{
//			bl.sa
			UserManager.getInstance().saveDb(bl);
			gameRoomManager.leaveGameRoom(bl);
			bl = null;
		}
	}

}
