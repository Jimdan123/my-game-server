package io.myserver.user;

import io.myserver.MyGameServer;
import io.myserver.Util;
import io.netty.channel.ChannelHandlerContext;

import javax.print.attribute.standard.JobOriginatingUserName;
import java.util.Arrays;

public class User
{
    private String userName;
    private String password;
    private String idRoom;
    private String jobInRoom;

    private int level;
    private long gold;

    private ChannelHandlerContext ctx;

    public User (String name, String password)
    {
        this.userName = name;
        this.password = password;
        idRoom = "";
        jobInRoom = "";
    }

    public User (String name)
    {
        this(name,"");
    }

    public String serialize()
    {
        String str = "";
        str += this.userName + ';';
        str += this.password + ';';
        str += String.valueOf(this.gold) + ';';
        str += String.valueOf(this.level);
        System.out.println(str);
        return str;
    }

//    public void saveDb()
//    {
//        String data = this.serialize();
//
//        MyGameServer.getInstance().getDbConn().set(userName, data);
//    }

    public static User deserialize(String input)
    {
        String[] strs = input.split(";");
        User u = new User(strs[0]);
        u.setPassword(strs[1]);
        u.setGold(Long.parseLong(strs[2]));
        u.setLevel(Integer.parseInt(strs[3]));
        return u;
    }

//    public static User loadDb(String userName)
//    {
//        try
//        {
//            String data = MyGameServer.getInstance().getDbConn().get(userName);
//
//            return deserialize(data);
//        }
//        catch (Exception ex)
//        {
//            //log
//        }
//
//        return null;
//    }

    public void sendMsgToClient(byte[] msg)
    {
        System.out.println("Send msg to user '" + this.userName + "':");
        Util.printByteArray(msg);

        this.ctx.writeAndFlush(msg);
    }

    public void setChannelCtx(ChannelHandlerContext ctx)
    {
        this.ctx = ctx;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

//    public void send

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setGold(long gold)
    {
        this.gold = gold;
    }

    public long getGold()
    {
        return gold;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public void setIdRoom(String  id)
    {
        this.idRoom = id;
    }

    public  String getIdRoom()
    {
        return this.idRoom;
    }

    public void setJobIdInRoom(String job)
    {
        this.jobInRoom = job;
    }

    public String getJobIdRoom()
    {
        return this.jobInRoom;
    }

}
