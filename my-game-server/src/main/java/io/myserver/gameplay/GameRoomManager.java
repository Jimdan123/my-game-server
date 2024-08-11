package io.myserver.gameplay;

import io.myserver.gameplay.GameRoom;
import io.myserver.user.User;
import io.myserver.user.UserManager;

import java.util.concurrent.ConcurrentHashMap;

public class GameRoomManager
{
    private static final GameRoomManager _INSTANCE = new GameRoomManager();

    public static GameRoomManager getInstance()
    {
        return _INSTANCE;
    }

    private static ConcurrentHashMap<String, GameRoom> onlineGameRooms;

    private GameRoomManager()
    {
        onlineGameRooms = new ConcurrentHashMap<>();
    }

    //User user;

    public void saveGameRoom(String id, GameRoom gameRoom)
    {
        onlineGameRooms.put(id, gameRoom);
    }

    public GameRoom getGameRoom(String id)
    {
        if(onlineGameRooms.containsKey(id) == false) {
            return null;
        }
        return onlineGameRooms.get(id);
    }

    public void addNewGameRoom(String id, short pin, User user) {
        if (onlineGameRooms.containsKey(id) == false)
        {
            GameRoom gameRoom = new GameRoom(id, pin);
            gameRoom.addUserToGameRoom(user);
            saveGameRoom(id, gameRoom);
//            this.user = user;
        }
    }

    public void leaveGameRoom(User user)
    {
        String id = user.getIdRoom();
        user.setIdRoom("");
        user.setJobIdInRoom("");
        GameRoom r = onlineGameRooms.get(id);
        System.out.println(id);
        if (r != null)
            r.removeUser(user);
    }

    public void deleteGameRoom(String id)
    {
        GameRoom r = onlineGameRooms.get(id);
        System.out.println(id);
        if (r != null)
            r.clearGameRoom();
        onlineGameRooms.remove(id);
    }

//    public User[] checkFullToJoinGame(String id)
//    {
//        GameRoom room = onlineGameRooms.get(id);
//        if(room.checkMaxCap() == true)
//        {
//            return room.returnJobOfAllUser();
//        }
//        return null;
//
//    }

    public boolean checkLoginGameRoom(String id, short pin, User user)
    {
        if (onlineGameRooms.containsKey(id) == true)
        {
            boolean checkMaxCap = onlineGameRooms.get(id).checkMaxCap();
            boolean checkPin = onlineGameRooms.get(id).getPin() == pin;
            if (checkPin == true && checkMaxCap == true)
            {
                onlineGameRooms.get(id).addUserToGameRoom(user);
                return true;
            }
        }
        return false;
    }
}
